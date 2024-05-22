public class SuperMatrix {
	int n;
	int m;
	public Vector[][] tab;

	public SuperMatrix(int width, int height) {
		n = width;
		m = height;
		tab = new Vector[n][m];
	}
	public static SuperMatrix Add(SuperMatrix a, SuperMatrix b) {
		assert a.n == b.n; assert a.m == b.m;

		SuperMatrix c = new SuperMatrix(a.n, a.m);
		for (int i = 0; i < a.n; i++) {
			for (int j = 0; j < a.m; j++) {
				c.tab[i][j] = Vector.Add(a.tab[i][j], b.tab[i][j]);
			}
		}
		return c;
	}

	public static SuperMatrix Scale(SuperMatrix a, int lambda) {
		SuperMatrix b = new SuperMatrix(a.n, a.m);

		for (int i = 0; i < a.n; i++) {
			for (int j = 0; j < a.m; j++) {
				b.tab[i][j] = Vector.Scale(a.tab[i][j],  lambda);
			}
		}
		return b;
	}

	public static SuperMatrix Product(SuperMatrix a, SuperMatrix b) {
		assert a.m == b.n;
		SuperMatrix c = new SuperMatrix(a.n, b.m);
		for (int i = 0; i < c.n; i++) {
			for (int j = 0; j < c.m; j++) {
				for (int k = 0; k < a.m; k++) {
					c.tab[i][j].add( Vector.Multiply(a.tab[i][k], b.tab[k][j]) );
				}
			}
		}
		return c;
	}
	public  SuperMatrix convolute(IntMatrix mat){
		SuperMatrix res = new SuperMatrix(n- mat.n, m-mat.m);
		int v = tab[0][0].n;
		for(int i = 0; i < n - mat.n ; i++){
			for (int j = 0 ; i < m - mat.m; j++){
				Vector r = new Vector(v);
				for(int x = 0; x < mat.n ; x++){
					for(int y = 0; y < mat.m; y++){
						r.add(Vector.Scale(tab[i+x][j+y], mat.tab[x][y]));
					}
				}
				res.tab[i][j] = r;
			}
		}
		return  res;
	}
}

