import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    public static int addr(int r, int g, int b){
        float[] hsv = new float[3];
        Color.RGBtoHSB(r,g,b,hsv);
        return (int)(15*hsv[0]) *64 + (int)(3*hsv[1])*16+(int)(15*hsv[2]) + 1;
    }
    public static double f(double t){
        double delta = 6.0/29.0;
        if(t > delta*delta*delta){
            return Math.cbrt(t);
        } else {
            return t / (3*delta*delta) + 4.0/29.0;
        }
    }
    public static double [] RGBtoLab(int r, int g, int b){
        double rf = (double)r / 255.0;
        double gf = (double)g / 255.0;
        double bf = (double)b / 255.0;
        double B[][] = {
                {0.49   , 0.31  , 0.2       },
                {0.17697, 0.8124, 0.01063   },
                {0      , 0.01  , 0.99      }
        };
        double xf = rf*B[0][0] + gf*B[0][1] + bf*B[0][2] ;
        double yf = rf*B[1][0] + gf*B[1][1] + bf*B[1][2] ;
        double zf = rf*B[2][0] + gf*B[2][1] + bf*B[2][2] ;

        double Xn = 95.0489, Yn = 100, Zn = 108.884;
        double Le = 116* f(yf/Yn) - 16;
        double ae = 500*(f(xf/Xn) - f(yf/Yn));
        double be = 200*(f(yf/Yn) - f(zf/Zn));

        return new double[]{Le, ae, be};
    }
/*    public static int addr(int r, int g, int b){
        float[] hsv = new float[3];
        Color.RGBtoHSB(r,g,b,hsv);
        return (int)(15*hsv[0]) *64 + (int)(3*hsv[1])*16+(int)(15*hsv[2]) + 1;
    }
*/
    public static void main(String[] args) throws IOException {
        BufferedImage img = ImageIO.read(new File("./src/carton_jaune3.jpeg"));
        int r, g, b;
        for (int x = 0 ; x < img.getWidth() ; x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int pixelcolor = img.getRGB(x, y);
                r = (pixelcolor & 0xFF0000) >> 8*2;
                g = (pixelcolor & 0x00FF00) >> 8 ;
                b = (pixelcolor & 0x0000FF);
                float[] hsv = new float[3];
                Color.RGBtoHSB(r,g,b,hsv);
                float[] hsv_red = new float[3];
                Color.RGBtoHSB(255,0,0,hsv_red);
                //if ( y == 0 )
                //System.out.println(hsv[0] + " " + hsv[1] + " " + hsv[2]);
                int Rc = (int)( 255* r / Math.sqrt(r*r+g*g+b*b)) ;
                if (r+g+b < 100 || g > r || b > r){
                    Rc = 0;
                }
                Rc = 255 - 2*Math.abs( ((int)(255 * ( ( hsv[0]-hsv_red[0]) )) + 127 )%255 -127 );
               // Rc = (255*Math.abs(addr(r, g, b) - addr(255, 0, 0)) ) / (16*64) ;
                double [] colab = RGBtoLab(r, g, b);
                double [] redlab = RGBtoLab(255, 200, 50);
                double [] delab = {colab[0]-redlab[0], colab[1]-redlab[1], colab[2]-redlab[2]};
                double sq_dist = 2*delab[0]*delab[0] + delab[1]*delab[1] +delab[2]*delab[2];
                double sgm = 50;
                double g_dist = Math.exp(-sq_dist/sgm);
                Rc = (int) (255*g_dist);
                //System.out.println(Rc + " " + sq_dist);
                if (Rc > 180) {
                    Rc = 255;
                }

                //if (Rc != 0)
                //Rc = 255-Rc;
                img.setRGB(x, y, (Rc<<8*2) + (Rc<<8) );
            }
        }
        ImageIO.write(img, "jpg", new File("./src/carton_rouge_toutrouge.jpg"));
        System.out.println(img.getWidth(null));
    }

}