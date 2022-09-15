import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author: happyaaakkk
 * @fecha: 28 de abril de 2020
 * @Description: prueba de curva de Bezier
 */
public class CurvasDeBezier {

    /**
     * Matriz de xey para puntos clave
     */
    private static Point2D[] keyPointP;
    /**
     * Una matriz de x, y, ancho y alto para almacenar puntos clave
     */
    private static Ellipse2D.Double[] keyPointE;
    /**
     * Puntos clave
     */
    private static int keyPointNum;
    /**
     * Desplazamiento, cuanto más pequeña es la curve, más fina es la curva
     */
    private static double t = 0.001;
    /**
     * Muestra la marca de la línea auxiliar azul
     */
    private static boolean flagShow = true;

    static class BezierPanel extends JPanel {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        /**
         * Número de punto clave
         */
        private int keyPointID = -1;

        BezierPanel() {
            this.addMouseListener(new MouseAction());
            this.addMouseMotionListener(new MouseMotion());
        }

        @Override
        protected void paintComponent(Graphics gs) {
            // Sobrescribir repintar
            super.paintComponent(gs);
            Graphics2D g = (Graphics2D) gs;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Color.BLUE);
            if (flagShow) {
                for (int i = 0; i <keyPointNum; i ++) {// dibuja un punto
                    if (i > 0 && i < (keyPointNum - 1)) {
                        g.fill (keyPointE [i]); // El punto clave en el medio dibuja un círculo sólido
                    } else {
                        g.draw (keyPointE [i]); // El primero y el último dibujan un círculo hueco
                    }
                    if (keyPointNum > 1 && i < (keyPointNum - 1)) {
                        g.drawLine((int) keyPointP[i].getX(), (int) keyPointP[i].getY(),
                                (int) keyPointP[i + 1].getX(),
                                // Dibuja una línea recta que conecte los puntos clave
                                (int) keyPointP [i + 1] .getY ());
                    }
                    if (i == 0) {
                        g.drawString("A", (int) keyPointE[i].x, (int) keyPointE[i].y);
                    } else if (i == 1) {
                        g.drawString("B", (int) keyPointE[i].x, (int) keyPointE[i].y);
                    } else if (i == 2) {
                        g.drawString("C", (int) keyPointE[i].x, (int) keyPointE[i].y);
                    } else if (i == 3) {
                        g.drawString("D", (int) keyPointE[i].x, (int) keyPointE[i].y);
                    }
                }
            }
            // Curva cuadrática de Bezier
            if (keyPointNum == 3) {
                double x, y;
                g.setColor(Color.RED);
                for (double k = t; k <= 1 + t; k += t) {
                    double r = 1 - k;
                    x = Math.pow(r, 2) * keyPointP[0].getX()
                            + 2 * k * r * keyPointP[1].getX() + Math.pow(k, 2) * keyPointP[2].getX();

                    y = Math.pow(r, 2) * keyPointP[0].getY()
                            + 2 * k * r * keyPointP[1].getY() + Math.pow(k, 2) * keyPointP[2].getY();

                    // La forma de dibujar un círculo es mejor que el dibujo lineal comentado a continuación
                    g.drawOval ((int) x, (int) y, 1, 1);
                    // g.drawLine((int) x, (int) y, (int) x, (int) y);
                }
            }
            // Curva de Bezier cúbica
            if (keyPointNum == 4) {
                double x, y;
                g.setColor(Color.RED);
                for (double k = t; k <= 1 + t; k += t) {
                    double r = 1 - k;
                    x = Math.pow(r, 3) * keyPointP[0].getX() + 3 * k * Math.pow(r, 2) * keyPointP[1].getX()
                            + 3 * Math.pow(k, 2) * (1 - k) * keyPointP[2].getX() + Math.pow(k, 3) * keyPointP[3].getX();
                    y = Math.pow(r, 3) * keyPointP[0].getY() + 3 * k * Math.pow(r, 2) * keyPointP[1].getY()
                            + 3 * Math.pow(k, 2) * (1 - k) * keyPointP[2].getY() + Math.pow(k, 3) * keyPointP[3].getY();
                    g.drawOval((int) x, (int) y, 1, 1);
                }
            }
        }

        class MouseAction extends MouseAdapter {
            @Override
            public void mouseClicked(MouseEvent e) {
                // H    aga clic en el botón izquierdo del mouse
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (keyPointNum < 4) {
                        double x = e.getX();
                        double y = e.getY();
                        keyPointP[keyPointNum] = new Point2D.Double(x, y);
                        keyPointE[keyPointNum] = new Ellipse2D.Double(x - 4, y - 4, 8, 8);
                        keyPointNum++;
                        repaint();
                    }
                } // botón derecho del ratón
                else if (e.getButton() == MouseEvent.BUTTON3) {
                    flagShow = false; // Ocultar la línea auxiliar azul, pero realmente no se puede eliminar
                    repaint();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // Presione el mouse para determinar si se hace clic en el punto clave
                for (int i = 0; i < keyPointNum; i++) {
                    if (keyPointE[i].contains((Point2D) e.getPoint())) {
                        keyPointID = i;
                        break;
                    } else {
                        keyPointID = -1;
                    }
                }
            }
        }

        class MouseMotion extends MouseMotionAdapter {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Arrastra el punto clave con el mouse
                if (keyPointID != -1) {
                    double x = e.getX();
                    double y = e.getY();
                    keyPointP[keyPointID] = new Point2D.Double(x, y);
                    keyPointE[keyPointID] = new Ellipse2D.Double(x - 4, y - 4, 8, 8);
                    repaint();
                }
            }
        }
    }

    public CurvasDeBezier() {
        JFrame f = new JFrame();
        f.setTitle("Test - Curvas de Bezier");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(800, 600);
        f.setLocationRelativeTo(null);

        keyPointNum = 0;
        keyPointP = new Point2D[4];
        keyPointE = new Ellipse2D.Double[4];
        BezierPanel bezierPanel = new BezierPanel();
        bezierPanel.setPreferredSize(new Dimension(800, 600));
        bezierPanel.setBackground(Color.WHITE);

        f.setContentPane(bezierPanel);
        f.setVisible(true);
    }

    public static void main(String[] args){
        new CurvasDeBezier();
    }
}
