package br.com.caronas;

import br.com.caronas.theme.AppTheme;
import br.com.caronas.views.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Configura renderização anti-aliasing no Java 2D
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Inicializa o tema moderno FlatLaf
        AppTheme.initialize();

        // Inicia a interface na EDT
        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
