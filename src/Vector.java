public class Vector {
	int n;
	public int[] tab;

	public Vector(int size) {
		n = size;
		tab = new int[n];
	}
	public Vector(int[] t) {
		n = t.length;
		tab = t.clone();
	}
	public Vector(Vector a) {
		n = a.n;
		tab = a.tab.clone();
	}

	public static Vector Add(Vector a, Vector b) {
		Vector c = new Vector(a.n);
		for (int i = 0; i < a.n; i++) {
			c.tab[i] = a.tab[i] + b.tab[i];
		}
		return c;
	}
	public void add( Vector b) {
		for (int i = 0; i < n; i++) {
			tab[i] += b.tab[i];
		}
	}

	public void scale(int lambda) {
		for (int i = 0; i < n; i++) {
			tab[i] *= lambda;
		}
	}

	public static Vector Scale(Vector a, int lambda){
		Vector c = new Vector(a);
		c.scale(lambda);
		return c;
	}

	public static int Dot(Vector a , Vector b){
		int res = 0;
		for (int i = 0; i < a.n ; i++){
			res += a.tab[i]*b.tab[i];
		}
		return res;
	}
	public static Vector Multiply(Vector a, Vector b) {
		Vector c = new Vector(a.n);
		for (int i = 0; i < a.n; i++) {
			c.tab[i] = a.tab[i] * b.tab[i];
		}
		return c;
	}
}
