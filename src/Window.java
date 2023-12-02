import javax.swing.*;
import java.awt.image.BufferedImage;

public class Window extends JFrame {
    public final static int SIZEX = 1200;
    public final static int SIZEY = 800;

    Panel pan;
    public Window() {
        pan = new Panel();
        this.setContentPane(pan);
        this.setSize(SIZEX+100, SIZEY);
        this.setUndecorated(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public void showImage(BufferedImage img) {
        BufferedImage nextimg = new BufferedImage(SIZEX, SIZEY, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < nextimg.getWidth(); x++) {
            for (int y = 0; y < nextimg.getHeight(); y++) {
                nextimg.setRGB(x, y, img.getRGB( ((img.getWidth()-1)*x) / SIZEX, ((img.getHeight()-1)*y) / SIZEY ));
            }
        }
        pan.nextimage = nextimg;
        pan.repaint();
    }
}
