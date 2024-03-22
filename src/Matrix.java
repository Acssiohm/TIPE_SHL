import java.lang.reflect.Array;

public class Matrix {
	int n;
	int m;
	public double[][] tab;

	public Matrix(int size) {
		n = size;
		m = size;
		tab = new double[n][m];
	}

	public Matrix(int width, int height) {
		n = width;
		m = height;
		tab = new double[n][m];
	}

	public Matrix(double[][] t) {
		n = t.length;
		m = t[0].length;
		tab = t.clone();
	}
	public Matrix(int[][] t) {
		n = t.length;
		m = t[0].length;
		tab = new double[n][m];
		for(int i = 0; i < n ; i++){
			for(int j = 0; j < m ; j++){
				tab[i][j] = t[i][j];
			}
		}
	}
	public static Matrix Add(Matrix a, Matrix b) {
		assert a.n == b.n; assert a.m == b.m;

		Matrix c = new Matrix(a.n, a.m);
		for (int i = 0; i < a.n; i++) {
			for (int j = 0; j < a.m; j++) {
				c.tab[i][j] = a.tab[i][j] + b.tab[i][j];
			}
		}
		return c;
	}

	public static Matrix Scale(Matrix a, double lambda) {
		Matrix b = new Matrix(a.n, a.m);

		for (int i = 0; i < a.n; i++) {
			for (int j = 0; j < a.m; j++) {
				b.tab[i][j] = a.tab[i][j] * lambda;
			}
		}
		return b;
	}

	public static Matrix Product(Matrix a, Matrix b) {
		assert a.m == b.n;
		Matrix c = new Matrix(a.n, b.m);
		for (int i = 0; i < c.n; i++) {
			for (int j = 0; j < c.m; j++) {
				for (int k = 0; k < a.m; k++) {
					c.tab[i][j] += a.tab[i][k] * b.tab[k][j];
				}
			}
		}
		return c;
	}
}
