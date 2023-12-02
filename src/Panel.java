import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Panel extends JPanel {
    public BufferedImage nextimage;
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(nextimage, 0, 0, null);
    }
}
