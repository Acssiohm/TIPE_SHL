import java.awt.*;

public class ColorHandler {
	int r, g, b;

	public ColorHandler(int pixel_color){
		r = (pixel_color & 0xFF0000) >> 8 * 2;
		g = (pixel_color & 0x00FF00) >> 8;
		b = (pixel_color & 0x0000FF);
	}
	public ColorHandler(int rc, int gc, int bc){
		r = rc;
		g = gc;
		b = bc;
	}
	public int [] getHSV(){
		float[] hsv = new float [3];
		Color.RGBtoHSB(r, g, b, hsv );
		return new int[]{ (int)(hsv[0]*255), (int)(hsv[1]*255), (int)(hsv[2]*255)  };
	}
	public int [] getYCbCr(){
		Matrix RGB1_to_YCbCr = new Matrix( new double[][] {
				{0.299, 0.587, 0.114, 0},
				{-0.1687, -0.5, 0.5, 128},
				{0.5, -.4187, -0.0813, 128}
		});
		Matrix cRGB1 = new Matrix(new double[][] {{r}, {g}, {b}, {1}});
		Matrix res = Matrix.Product( RGB1_to_YCbCr, cRGB1 );
		return new int[] { (int)res.tab[0][0], (int)res.tab[1][0], (int)res.tab[2][0]};
	}
	public int [] getInvYCbCr(){
		Matrix YCbCr1_To_RGB = new Matrix( new double[][] {
				{1, 1.402, 0, -128*1.402},
				{1, -0.34414, -0.71414, (0.34414+0.71414)*128},
				{1, 1.772, 0, -1.772*128}
		});
		Matrix cYCrCb1 = new Matrix(new double[][] {{r}, {g}, {b}, {1}});
		Matrix res = Matrix.Product( YCbCr1_To_RGB, cYCrCb1 );
		return new int[] { (int)res.tab[0][0], (int)res.tab[1][0], (int)res.tab[2][0]};
	}
	public ColorHandler(int[] c){
		assert c.length == 3 ;
		r = c[0];
		g = c[1];
		b = c[2];
	}
	public int [] getRGB(){
		return new int[]{r, g, b};
	}
	public int getInt(){
		return (r << 16) + (g << 8) + b;
	}
	 /*
    public static int addr(int r, int g, int b) {
        float[] hsv = new float[3];
        Color.RGBtoHSB(r, g, b, hsv);
        return (int) (15 * hsv[0]) * 64 + (int) (3 * hsv[1]) * 16 + (int) (15 * hsv[2]) + 1;
    }*/
	public int[] opacify( double opacity ){
		return new int[]{(int) (r * opacity), (int) (g * opacity), (int) (b * opacity)};
	}
	int luminosity(){
		return (int)(0.3*r + 0.59*g + 0.11*b);
	}
	static double f(double t) {
		double delta = 6.0 / 29.0;
		if (t > delta * delta * delta) {
			return Math.cbrt(t);
		} else {
			return t / (3 * delta * delta) + 4.0 / 29.0;
		}
	}

	public double[] getLab() {
		Matrix cRGB = new Matrix(new double[][]{{(double) r / 255.0}, {(double) g / 255.0}, {(double) b / 255.0}});
		Matrix B = new Matrix( new double[][]{
				{0.49, 0.31, 0.2},
				{0.17697, 0.8124, 0.01063},
				{0, 0.01, 0.99}
		});
		Matrix cXYZ = Matrix.Product(B, cRGB);
		double xf = cXYZ.tab[0][0], yf = cXYZ.tab[1][0], zf = cXYZ.tab[2][0];
		final double Xn = 95.0489, Yn = 100, Zn = 108.884;
		double Le = 116 * f(yf / Yn) - 16;
		double ae = 500 * (f(xf / Xn) - f(yf / Yn));
		double be = 200 * (f(yf / Yn) - f(zf / Zn));

		return new double[]{Le, ae, be};
	}

	@Override
	public String toString() {
		return "Colorhandler ; r:" + r + ",g:" + g + ",b:" +b;
	}
}
