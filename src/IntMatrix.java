public class IntMatrix {
	int n;
	int m;
	public int[][] tab;

	public IntMatrix(int size) {
		n = size;
		m = size;
		tab = new int[n][m];
	}
	public IntMatrix(int width, int height) {
		n = width;
		m = height;
		tab = new int[n][m];
	}

	public IntMatrix(int[][] t) {
		n = t.length;
		m = t[0].length;
		tab = t.clone();
	}
	public IntMatrix(int[] t) {
		n = 1;
		m = t.length;
		tab = new int[][]{t};
	}

	public static IntMatrix Add(IntMatrix a, IntMatrix b) {
		assert a.n == b.n; assert a.m == b.m;

		IntMatrix c = new IntMatrix(a.n, a.m);
		for (int i = 0; i < a.n; i++) {
			for (int j = 0; j < a.m; j++) {
				c.tab[i][j] = a.tab[i][j] + b.tab[i][j];
			}
		}
		return c;
	}

	public static IntMatrix Scale(IntMatrix a, int lambda) {
		IntMatrix b = new IntMatrix(a.n, a.m);

		for (int i = 0; i < a.n; i++) {
			for (int j = 0; j < a.m; j++) {
				b.tab[i][j] = a.tab[i][j] * lambda;
			}
		}
		return b;
	}

	public static IntMatrix Product(IntMatrix a, IntMatrix b) {
		assert a.m == b.n;
		IntMatrix c = new IntMatrix(a.n, b.m);
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
