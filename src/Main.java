import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

public class Main {
	public static HashMap<String, Double> params = new HashMap<>();
	static Window window;
	public static double sq(double x){
		return x*x;
	}
	public static double closeness_score( double[] clr1_lab, double[] clr2_lab){
		double[] delta_lab = {clr1_lab[0] - clr2_lab[0], clr1_lab[1] - clr2_lab[1], clr1_lab[2] - clr2_lab[2]};
		double sq_dist = params.get("lf") * sq(delta_lab[0])
				+ params.get("af") * sq(delta_lab[1])
				+ params.get("bf") * sq( delta_lab[2]);
		double sgm = params.get("sigma");
		return Math.exp(-sq_dist / sgm);
	}
	public static BufferedImage open_image(String name){
		BufferedImage img;
		try {
			img = ImageIO.read(new File(name));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return  img;
	}
	public static void algo_couleurs() {
		BufferedImage img = open_image("./src/img/carton_rouge.jpg");
		int r, g, b;
		int pixel_color;
		ColorHandler ref_color = new ColorHandler(new int[]{params.get("r").intValue(), params.get("g").intValue(), params.get("b").intValue()});
		int block_size_x = img.getWidth()/4;
		int block_size_y = img.getHeight()/4;
		int Gmin = 58;
		int Gmax = 68;
		double [][] scores = new double[4][4];
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				ColorHandler c = new ColorHandler(img.getRGB(x, y));

				int [] hsv = c.getHSV();
				if(hsv[0] >= Gmin && hsv[0] <= Gmax ){

				}
                double[] clr_lab = c.getLab();
                double score = closeness_score(clr_lab, ref_color.getLab());
                //score = (double)r /Math.sqrt( r*r + g*g + b*b );
                if (score > 0.9) {
                    score = 1;
                }
                int blocx = x/(block_size_x+1);
                int blocy = y/(block_size_y+1);
                scores[ blocx ][blocy] += Math.sqrt(score);
                img.setRGB(x, y,  (new ColorHandler(ref_color.opacify(score))).getInt() ) ;
                if(y/block_size_y == 0 ){
                    //img.setRGB(x,y, 0xFFFFFF);
                }
			}
		}
        /*
        for (int x = 0; x < 10; x++){
            for (int y = 0; y < 10; y++){
                img.setRGB(x, y, RGBtoInt(ref_color));
            }
        }
        for (int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                System.out.print(scores[i][j]/(block_size_y*block_size_x) + " ");
            }
            System.out.println();
        }
        //ImageIO.write(img, "jpg", new File("./src/img/carton_rouge_toutrouge.jpg"));
        */
		System.out.println(img.getWidth(null));
		window.showImage(img);
	}
	public enum ColorMode {
		RGB,
		YCC,
		Grey
	}
	public static int convolution(int [][] matrice, double s, BufferedImage img, int x, int y, ColorMode cm ){
		int pixel_color;
		ColorHandler res = new ColorHandler(0);
		assert matrice.length %2 == 1;
		assert matrice[0].length %2 == 1;

		for(int i = 0; i < matrice.length; i++){
			for (int j = 0; j < matrice[0].length; j++) {
				assert (matrice.length-1)/2 == 2;
				int pix_pos_x = x + i - (matrice.length-1)/2;
				int pix_pos_y = y + j - (matrice[0].length-1)/2;
				ColorHandler c = new ColorHandler(img.getRGB(pix_pos_x, pix_pos_y));

				if(cm == ColorMode.Grey){
					int grey_scale = c.luminosity();
					c.r = grey_scale;
					c.g = grey_scale;
					c.b = grey_scale;
				} else if (cm == ColorMode.YCC){
					c = new ColorHandler(c.getYCbCr());
				}
				res.r += matrice[i][j]*c.r;
				res.g += matrice[i][j]*c.g;
				res.b += matrice[i][j]*c.b;
			}
		}
		res.r = (int)(Math.abs(res.r)/s);
		res.g = (int)(Math.abs(res.g)/s);
		res.b = (int)(Math.abs(res.b)/s);
		if(cm == ColorMode.YCC){
			res = new ColorHandler(res.getInvYCbCr());
		}
		return res.getInt();
	}
	public static BufferedImage algo_contours(){
		String p = "./src/img/public.png";
		String cj = "./src/img/carton_jaune.jpg";
		BufferedImage img = open_image(cj);
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				ColorHandler c = new ColorHandler(img.getRGB(x, y));
				img.setRGB(x,y,c.getInt());
			}
		}
		//window.showImage(img);
		BufferedImage nextimg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
		BufferedImage nextnextimg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
		int[][] h = new int[][]
						{{1,3,4,3,1},
						{3,9,12,9,3},
						{4,12,15,12,4},
						{3,9,12,9,3},
						{1,3,4,3,1}};
		for(int i = 0; i < 5; i++){
			for(int j = 0; j < 5; j++){
				//h[i][j] /= 159;
			}
		}
		for (int x = 2; x < nextimg.getWidth()-2; x++) {
			for (int y = 2; y < nextimg.getHeight()-2; y++) {
				//nextimg.setRGB(x,y,img.getRGB(x,y));
				nextimg.setRGB(x,y, convolution(h, 159-16 , img, x, y, ColorMode.RGB));
			}
		}
		int [][] d = new int[][]{
				{-1, 0, 1}
		};
		int[][] od2 = new int[][]{
				{-1},
				{0},
				{1}
		};
		int val = 0;
		for (int x = 1; x < nextimg.getWidth()-1; x++) {
			for (int y = 1; y < nextimg.getHeight()-1; y++) {
				ColorHandler res1 = new ColorHandler(convolution(d, 1, nextimg, x, y, ColorMode.YCC));
				ColorHandler res2 = new ColorHandler(convolution(od2, 1, nextimg, x, y, ColorMode.YCC) );
				int res = (int)Math.sqrt( sq(res1.b) + sq(res2.b));
				int b = res > 70 ? 1 : 0  ;
				val += b;
				//nextnextimg.setRGB(x,y, (new ColorHandler(val*255, val*255, val*255)).getInt() );
				nextnextimg.setRGB(x,y, (new ColorHandler(b*255,b*255,b*255)).getInt() );
			}
		}
		System.out.println(((double)val)/(nextimg.getHeight()*nextimg.getWidth()));
		return nextnextimg;
	}
	public static void HandleCmd(String command) {
		System.out.println("Prise en compte de : " + command);

		if(command.startsWith("setparam") || command.startsWith("set") || command.startsWith("sp") ) {
			String[] args = command.split(" ");
			if(args.length != 3 ) {
				System.out.println("Nombre invalide d'arguments");
				return;
			}
			params.put(args[1], Double.parseDouble(args[2]));
		}
		else if(command.startsWith("getparam") || command.startsWith("gp") ||  command.startsWith("get")) {
			String[] args = command.split(" ");
			if(args.length != 2) {
				System.out.println("Nombre invalide d'arguments");
				return;
			}
			if(params.containsKey(args[1])) {
				System.out.println(args[1]+" : "+params.get(args[1]));
			} else {
				System.out.println("Le paramètre n'existe pas");
				return;
			}
		} else {
			System.out.println("Commande non reconnu");
		}
		algo_couleurs();
	}



	public static void ParamsBaseValue() {
		params.put("r", 255d);
		params.put("g", 0d);
		params.put("b", 0d);
		params.put("sigma", 50d);
		params.put("lf", 2d);
		params.put("af", 8d);
		params.put("bf", 1d);
	}

	public static void main(String[] args) {
		//new SoundExtracting();

		ParamsBaseValue();
		window = new Window();
		window.showImage(algo_contours());
		//algo_couleurs();

		Scanner in = new Scanner(System.in);
		String s = in.nextLine() ;
		while(!Objects.equals(s, "end")) {
			HandleCmd(s);
			s = in.nextLine();
		}
	}
}