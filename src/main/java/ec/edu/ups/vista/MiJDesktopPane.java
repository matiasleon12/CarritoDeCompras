package ec.edu.ups.vista;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MiJDesktopPane extends JDesktopPane {
    private BufferedImage imagen;

    public MiJDesktopPane() {
        try {
            imagen = ImageIO.read(getClass().getClassLoader().getResource("imagenes/foto_principal.jpg"));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("No se pudo cargar la imagen de fondo: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (imagen != null) {
            g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this); // pinta imagen escalada
        }
    }
}