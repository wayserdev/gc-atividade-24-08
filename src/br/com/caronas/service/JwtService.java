package br.com.caronas.service;

import br.com.caronas.model.Usuario;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Emissor e validador local de tokens JWT HS256 para o mock da aplicação. */
public final class JwtService {
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final long DEFAULT_TTL_SECONDS = 7_200;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Base64.Encoder BASE64_URL = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder BASE64_DECODER = Base64.getUrlDecoder();

    private static final Pattern SUB = Pattern.compile("\\\"sub\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"");
    private static final Pattern EMAIL = Pattern.compile("\\\"email\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"");
    private static final Pattern LEVEL = Pattern.compile("\\\"nivel\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"");
    private static final Pattern IAT = Pattern.compile("\\\"iat\\\"\\s*:\\s*(\\d+)");
    private static final Pattern EXP = Pattern.compile("\\\"exp\\\"\\s*:\\s*(\\d+)");

    private final byte[] secret;
    private final long ttlSeconds;
    private final Clock clock;

    public JwtService() {
        this(resolveSecret());
    }

    public JwtService(String secret) {
        this(secret, Duration.ofSeconds(DEFAULT_TTL_SECONDS), Clock.systemUTC());
    }

    public JwtService(String secret, Duration ttl, Clock clock) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("O segredo JWT deve possuir pelo menos 32 caracteres.");
        }
        if (ttl == null || ttl.isZero() || ttl.isNegative() || ttl.getSeconds() <= 0) {
            throw new IllegalArgumentException("A validade do JWT deve ser positiva.");
        }
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.ttlSeconds = ttl.getSeconds();
        this.clock = clock == null ? Clock.systemUTC() : clock;
    }

    public String createToken(Usuario usuario) {
        if (usuario == null || usuario.getId() == null || usuario.getEmail() == null) {
            throw new IllegalArgumentException("Usuário inválido para emissão do token.");
        }

        long issuedAt = Instant.now(clock).getEpochSecond();
        long expiresAt = issuedAt + ttlSeconds;
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payload = "{\"sub\":\"" + escape(usuario.getId())
                + "\",\"email\":\"" + escape(usuario.getEmail())
                + "\",\"nivel\":\"" + escape(usuario.getNivel())
                + "\",\"iat\":" + issuedAt
                + ",\"exp\":" + expiresAt
                + ",\"jti\":\"" + UUID.randomUUID() + "\"}";

        String unsigned = BASE64_URL.encodeToString(header.getBytes(StandardCharsets.UTF_8))
                + "." + BASE64_URL.encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        return unsigned + "." + BASE64_URL.encodeToString(sign(unsigned));
    }

    public Optional<Claims> validate(String token) {
        if (token == null || token.trim().isEmpty()) {
            return Optional.empty();
        }

        try {
            String[] parts = token.split("\\.", -1);
            if (parts.length != 3) {
                return Optional.empty();
            }

            String unsigned = parts[0] + "." + parts[1];
            byte[] expectedSignature = BASE64_DECODER.decode(parts[2]);
            if (!MessageDigest.isEqual(expectedSignature, sign(unsigned))) {
                return Optional.empty();
            }

            String header = new String(BASE64_DECODER.decode(parts[0]), StandardCharsets.UTF_8);
            if (!header.contains("\"alg\":\"HS256\"") || !header.contains("\"typ\":\"JWT\"")) {
                return Optional.empty();
            }

            String payload = new String(BASE64_DECODER.decode(parts[1]), StandardCharsets.UTF_8);
            String sub = extract(payload, SUB);
            String email = extract(payload, EMAIL);
            String nivel = extract(payload, LEVEL);
            Long issuedAt = extractLong(payload, IAT);
            Long expiresAt = extractLong(payload, EXP);
            long now = Instant.now(clock).getEpochSecond();
            if (sub == null || email == null || nivel == null || issuedAt == null || expiresAt == null
                    || expiresAt <= now || issuedAt > now + 60) {
                return Optional.empty();
            }

            return Optional.of(new Claims(sub, email, nivel, issuedAt, expiresAt));
        } catch (RuntimeException exception) {
            return Optional.empty();
        }
    }

    private byte[] sign(String value) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
            return mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
        } catch (Exception exception) {
            throw new IllegalStateException("Não foi possível assinar o token JWT.", exception);
        }
    }

    private static String extract(String payload, Pattern pattern) {
        Matcher matcher = pattern.matcher(payload);
        return matcher.find() ? matcher.group(1) : null;
    }

    private static Long extractLong(String payload, Pattern pattern) {
        String value = extract(payload, pattern);
        try {
            return value == null ? null : Long.parseLong(value);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private static String escape(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String resolveSecret() {
        String configured = System.getenv("CARONAS_JWT_SECRET");
        if (configured != null && !configured.trim().isEmpty()) {
            return configured;
        }

        // O mock precisa funcionar sem configuração local, mas não deve
        // carregar um segredo previsível no código-fonte.
        byte[] generated = new byte[32];
        RANDOM.nextBytes(generated);
        return BASE64_URL.encodeToString(generated);
    }

    public static final class Claims {
        private final String subject;
        private final String email;
        private final String nivel;
        private final long issuedAt;
        private final long expiresAt;

        private Claims(String subject, String email, String nivel, long issuedAt, long expiresAt) {
            this.subject = subject;
            this.email = email;
            this.nivel = nivel;
            this.issuedAt = issuedAt;
            this.expiresAt = expiresAt;
        }

        public String getSubject() { return subject; }
        public String getEmail() { return email; }
        public String getNivel() { return nivel; }
        public long getIssuedAt() { return issuedAt; }
        public long getExpiresAt() { return expiresAt; }
    }
}
