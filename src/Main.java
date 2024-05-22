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

	public static double [][] algo_couleurs(String img_file, ColorHandler ref_color , int nb_block_x, int nb_block_y, boolean show) {
		BufferedImage img = open_image(img_file);

		int block_size_x = img.getWidth()/nb_block_x;
		int block_size_y = img.getHeight()/nb_block_y;
		int Gmin = 58;
		int Gmax = 68;
		double [][] scores = new double[nb_block_x][nb_block_y];
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				ColorHandler c = new ColorHandler(img.getRGB(x, y));

                double[] clr_lab = c.getLab();
                double score = closeness_score(clr_lab, ref_color.getLab());
                //score = (double)r /Math.sqrt( r*r + g*g + b*b );
				if (score > 0.9) {
                    score = 1;
                } else if ( score < 0.5){
					score = 0;
                }
                int blocx = x/(block_size_x+1);
                int blocy = y/(block_size_y+1);
                scores[ blocx ][blocy] += Math.sqrt(score);

				// Afficher le résultat
				img.setRGB(x, y,  (new ColorHandler(ref_color.opacify(score))).getInt() ) ;
                if(y%block_size_y == 0 || x%block_size_x < 2 ){
                    img.setRGB(x,y, 0xFFFFFF);
                }
			}
		}
		if(show)
			window.showImage(img);
		return scores;
	}
	public static void write_img_to(BufferedImage img, String img_filename) {
		try {
			ImageIO.write(img, "jpg", new File("./src/img/"+img_filename+ ".jpg"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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
	public static BufferedImage algo_contours( String img_file){
		BufferedImage temp_img1 = open_image(img_file);
		int width = temp_img1.getWidth(), height = temp_img1.getHeight();
		BufferedImage temp_img2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		// Floutage
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
		for (int x = 2; x < width-2; x++) {
			for (int y = 2; y < height-2; y++) {
				//nextimg.setRGB(x,y,img.getRGB(x,y));
				temp_img2.setRGB(x,y, convolution(h, 159-16 , temp_img1, x, y, ColorMode.RGB));
			}
		}
		// contour
		int [][] d = new int[][]{
				{-1, 0, 1}
		};
		int[][] od2 = new int[][]{
				{-1},
				{0},
				{1}
		};

		// Combinaison des deux directions + seuillage
		int seuil = 70;
		int val = 0;
		for (int x = 1; x < width-1; x++) {
			for (int y = 1; y < height-1; y++) {
				ColorHandler res1 = new ColorHandler(convolution(d, 1, temp_img2, x, y, ColorMode.YCC));
				ColorHandler res2 = new ColorHandler(convolution(od2, 1, temp_img2, x, y, ColorMode.YCC) );
				int res = (int)Math.sqrt( sq(res1.b) + sq(res2.b));
				int b = res > seuil ? 1 : 0  ;
				val += b;
				//temp_img1.setRGB(x,y, (new ColorHandler(val*255, val*255, val*255)).getInt() );
				temp_img1.setRGB(x,y, (new ColorHandler(b*255,b*255,b*255)).getInt() );
			}
		}
		System.out.println(((double)val)/(height*width));
		return temp_img1;
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
		String carton_jaune = "./src/img/carton_jaune.jpg";
		String publique = "./src/img/public.png";
		String carton_rouge = "./src/img/carton_rouge.jpg";
		String test_colors = "./src/img/hsv_101.png";

		ParamsBaseValue();
		window = new Window();
		//window.showImage(algo_contours(carton_jaune));
		ColorHandler ref_color = new ColorHandler(new int[]{params.get("r").intValue(), params.get("g").intValue(), params.get("b").intValue()});
		algo_couleurs(carton_rouge, ref_color , 1, 4, true);
		Scanner in = new Scanner(System.in);
		String s = in.nextLine() ;
		while(!Objects.equals(s, "end")) {
			HandleCmd(s);
			algo_contours(carton_rouge);
			s = in.nextLine();
		}
	}
}