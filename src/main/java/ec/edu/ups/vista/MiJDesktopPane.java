package ec.edu.ups.vista;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MiJDesktopPane extends JDesktopPane {
    private BufferedImage imagen;

    public MiJDesktopPane() {
        setBackground(new Color(255, 223, 128));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(new Color(255, 223, 128));
        g.fillRect(0, 0, getWidth(), getHeight());

        String texto = "Compras en linea";
        char[] letras = texto.toCharArray();

        Font fuenteTitulo = new Font("Times new roman", Font.BOLD, 150);
        g.setFont(fuenteTitulo);
        FontMetrics fm = g.getFontMetrics();


        int anchoTotal = 0;
        for (char c : letras) {
            anchoTotal += fm.charWidth(c);
        }

        int x = (getWidth() - anchoTotal) / 2;
        int y = getHeight() / 3;

        Color azulOscuro = new Color(0, 45, 100);
        Color celeste = new Color(0, 200, 200);


        for (int i = 0; i < letras.length; i++) {
            char letra = letras[i];
            if (i == 0 || i == letras.length - 1) {
                g.setColor(celeste);
            }
            g.drawString(String.valueOf(letra), x, y);
            x += fm.charWidth(letra);
        }

        String subtitulo = "La mejor manera de comprar sin perder tiempo";
        Font fuenteSub = new Font("Times new roman", Font.PLAIN, 70);
        g.setFont(fuenteSub);
        g.setColor(new Color(0, 80, 160));

        FontMetrics fmSub = g.getFontMetrics();
        int xSub = (getWidth() - fmSub.stringWidth(subtitulo)) / 2;
        int ySub = y + fmSub.getHeight() + 40;

        g.drawString(subtitulo, xSub, ySub);
    }
}