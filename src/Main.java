import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static HashMap<String, Double> params = new HashMap<>();
    static Window window;

    /*
    public static int addr(int r, int g, int b) {
        float[] hsv = new float[3];
        Color.RGBtoHSB(r, g, b, hsv);
        return (int) (15 * hsv[0]) * 64 + (int) (3 * hsv[1]) * 16 + (int) (15 * hsv[2]) + 1;
    }*/

    public static double f(double t) {
        double delta = 6.0 / 29.0;
        if (t > delta * delta * delta) {
            return Math.cbrt(t);
        } else {
            return t / (3 * delta * delta) + 4.0 / 29.0;
        }
    }

    public static double[] RGBtoLab(int r, int g, int b) {
        double rf = (double) r / 255.0;
        double gf = (double) g / 255.0;
        double bf = (double) b / 255.0;
        double[][] B = {
                {0.49, 0.31, 0.2},
                {0.17697, 0.8124, 0.01063},
                {0, 0.01, 0.99}
        };
        double xf = rf * B[0][0] + gf * B[0][1] + bf * B[0][2];
        double yf = rf * B[1][0] + gf * B[1][1] + bf * B[1][2];
        double zf = rf * B[2][0] + gf * B[2][1] + bf * B[2][2];

        double Xn = 95.0489, Yn = 100, Zn = 108.884;
        double Le = 116 * f(yf / Yn) - 16;
        double ae = 500 * (f(xf / Xn) - f(yf / Yn));
        double be = 200 * (f(yf / Yn) - f(zf / Zn));

        return new double[]{Le, ae, be};
    }

    /*    public static int addr(int r, int g, int b){
            float[] hsv = new float[3];
            Color.RGBtoHSB(r,g,b,hsv);
            return (int)(15*hsv[0]) *64 + (int)(3*hsv[1])*16+(int)(15*hsv[2]) + 1;
        }
    */
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

    public static int RGBtoInt(int[] clr){
        assert(clr[0] < 256 && clr[1] < 256 && clr[2] < 256);
        return (clr[0] << 16) + (clr[1] << 8) + clr[2];
    }
    public static int[] opacify( int [] clr, double opacity ){
        return new int[]{(int) (clr[0] * opacity), (int) (clr[1] * opacity), (int) (clr[2] * opacity)};
    }
    public static void algo() {
        BufferedImage img;
        try {
            img = ImageIO.read(new File("./src/carton_rouge.jpg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int r, g, b;
        int pixel_color;
        int[] ref_color = { params.get("r").intValue(), params.get("g").intValue(), params.get("b").intValue()};
        double[] ref_lab = RGBtoLab(ref_color[0], ref_color[1], ref_color[2]);

        int block_size_x = img.getWidth()/4;
        int block_size_y = img.getHeight()/4;

        double [][] scores = new double[4][4];
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                pixel_color = img.getRGB(x, y);

                r = (pixel_color & 0xFF0000) >> 8 * 2;
                g = (pixel_color & 0x00FF00) >> 8;
                b = (pixel_color & 0x0000FF);

                double[] clr_lab = RGBtoLab(r, g, b);
                double score = closeness_score(clr_lab, ref_lab);
                //score = (double)r /Math.sqrt( r*r + g*g + b*b );
                if (score > 0.9) {
                    score = 1;
                }
                int blocx = x/(block_size_x+1);
                int blocy = y/(block_size_y+1);
                scores[ blocx ][blocy] += Math.sqrt(score);
                img.setRGB(x, y, RGBtoInt(opacify(ref_color,  score)));
                if(x%block_size_x == 0 || y%block_size_y == 0){
                    img.setRGB(x,y, 0xFFFFFF);
                }
            }
        }
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
        //ImageIO.write(img, "jpg", new File("./src/carton_rouge_toutrouge.jpg"));
        System.out.println(img.getWidth(null));
        window.showImage(img);
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
        algo();
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
        new SoundExtracting();
        
        /*ParamsBaseValue();
        window = new Window();
        Scanner in = new Scanner(System.in);
        algo();
        String s = in.nextLine() ;
        while(!Objects.equals(s, "end")) {
            HandleCmd(s);
            s = in.nextLine();
        }*/
    }
}