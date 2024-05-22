import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Image {
	SuperMatrix img_mat;
	Image (String name){
		try {
			BufferedImage img = ImageIO.read(new File(name));
			img_mat = new SuperMatrix(img.getWidth(), img.getHeight() );
			for (int x = 0; x < img.getWidth(); x++) {
				for (int y = 0; y < img.getHeight(); y++) {
					ColorHandler c = new ColorHandler(img.getRGB(x, y));
					img_mat.tab[x][y] = new Vector( c.getRGB() );
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public enum ColorMode {
		RGB,
		YCC,
		Grey
	}
	public void convolution(IntMatrix matrice, double s ){
		img_mat = img_mat.convolute(matrice);
	}

}
