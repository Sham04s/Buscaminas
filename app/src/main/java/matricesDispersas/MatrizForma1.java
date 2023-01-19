package matricesDispersas;

import java.util.function.BiFunction;

/**
 * Clase que representa una matriz dispersa implementando listas doblemente
 * ligadas circulares con nodo cabeza.
 * <p>
 * <b>Nota:</b> Esta matriz dispersa tiene una lista doblemente ligada circular
 * de nodos cabeza, cada uno representa una misma filas y columna.
 * 
 * @author sneyd
 *
 */
public class MatrizForma1 {

	/**
	 * {@link NodoDoble} que representa la matriz dispersa.
	 * <p>
	 * <b>Nota:</b> Contiene el tamaño de la matriz dispersa.
	 */
	private NodoDoble mat;

	/**
	 * Objeto que representa el elemento nulo o vacío de la matriz dispersa.
	 * 
	 * <b>Nota:</b> Este campo se inicializa en los constructores.
	 */
	private final Object nulo;

	/**
	 * Constructor. Inicializa el objeto instanciado creando un {@link NodoDoble}
	 * conteniendo una {@link Tripleta} que describe el tamaño de la matriz
	 * dispersa. Luego llama a otro método que construye los nodos cabeza.
	 * <p>
	 * Si se intenta crear una matriz con cero elementos
	 * <code>(m == 0 || n == 0)</code>, ocurrirá una excepción.
	 * 
	 * @param m    Número de filas de la matriz dispersa.
	 * @param n    Número de columnas de la matriz dispersa.
	 *             <p>
	 * @param nulo Establece el objet nulo o vacío de la matriz dispersa.<br>
	 *             Ejs: <br>
	 *             <i>1.</i> Si la matriz es de tipo {@link Boolean}, el parámetro
	 *             <b>nulo</b> es <code>false</code>.<br>
	 *             <i>2.</i> Si es de tipo {@link Integer}, <b>nulo</b> es
	 *             <b>0</b>.<br>
	 *             <i>3.</i> Si es tipo {@link String}, <b>nulo</b> es <b>""</b>.
	 */
	public MatrizForma1(int m, int n, Object nulo) {

		assert (m > 0 && n > 0) : "m y n deben ser mayores a cero";

		Tripleta tripletaT = new Tripleta(m, n, null);
		this.mat = new NodoDoble(tripletaT);
		tripletaT.setValor(this.mat); // inicialmente apunta hacia si mismo con el valor de la tripleta.
		this.nulo = nulo;
		construirNodosCabeza();
	}

	/**
	 * Constructor. Construye la matriz dispersa a partir de los elementos
	 * diferentes al elemento nulo o vacío de la matriz entregada como parámetro.
	 * 
	 * @param matriz Matriz de enteros de la cual se construye esta matriz dispersa.
	 *               <p>
	 * @param nulo   Establece el objeto nulo o vacío de la matriz dispersa.<br>
	 *               Ejs: <br>
	 *               <i>1.</i> Si la matriz es de tipo {@link Boolean}, el parámetro
	 *               <b>nulo</b> es <code>false</code>.<br>
	 *               <i>2.</i> Si es de tipo {@link Integer}, <b>nulo</b> es
	 *               <b>0</b>.<br>
	 *               <i>3.</i> Si es tipo {@link String}, <b>nulo</b> es <b>""</b>.
	 */
	public MatrizForma1(Object[][] matriz, Object nulo) {

		// tamaño de la matriz que se almacena en el nodo mat
		int m = matriz.length;
		int n = matriz[0].length;
		Tripleta tripletaT = new Tripleta(m, n, null);
		mat = new NodoDoble(tripletaT);
		tripletaT.setValor(this.mat);
		this.nulo = nulo;

		// se construye los nodos cabeza requeridos.
		construirNodosCabeza();

		// por cada elemento diferente del elemento nulo de la matriz se añade un nodo a
		// la matriz
		// dispersa conectandolo como corresponde.
		Object v;
		for (int i = 0; i < matriz.length; i++) {
			for (int j = 0; j < matriz[i].length; j++) {
				v = matriz[i][j];
				if (v != this.nulo) {
					tripletaT = new Tripleta(i, j, (Object) v);
					NodoDoble x = new NodoDoble(tripletaT);
					conectar(x, true);
				}
			}
		}
	}

	/**
	 * Construye <b>n</b> nodos doble cabeza, donde n es el máximo entre el número
	 * de filas y columnas de la matriz dispersa.
	 */
	public void construirNodosCabeza() {

		// tamaño de la matriz dispersa
		Tripleta tripletaT = (Tripleta) this.mat.getD();
		int m = tripletaT.getFila();
		int n = tripletaT.getColumna();
		int max = Math.max(m, n); // maximo entre filas y columnas

		NodoDoble ultimo = this.mat;

		// se crean max * (nodos doble cabeza) y se conectan en forma circular
		for (int i = 0; i < max; i++) {

			tripletaT = new Tripleta(0, 0, this.mat); // Nodo doble cabeza con 0 elemtos por fila y 0 elementos por
														// columna
			NodoDoble nodoX = new NodoDoble(tripletaT);

			nodoX.setLd(nodoX); // conexión circular por fila
			nodoX.setLi(nodoX); // conexión circular por columna

			// conexión circular de los nodods cabeza
			tripletaT = (Tripleta) ultimo.getD();
			tripletaT.setValor(nodoX);

			ultimo = nodoX;
		}
	}

	/**
	 * Invoca las funciones para conectar un {@link NodoDoble} <b>x</b> por filas y
	 * por columnas. <br>
	 * Si ya existe un nodo con la misma <b>fila</b> y <b>columna</b> que el nodo
	 * <b>x</b>, <b>reemplazar</b> determina que operación se hace.
	 * 
	 * @param nodoX      {@link NodoDoble} a conectar en la matriz dispersa.
	 * @param reemplazar Si es <code>true</code> se reemplaza el valor del nodo en
	 *                   la matriz dispersa con el valor del nodo <b>x</b>. De lo
	 *                   contrario no se reemplaza.
	 * @return <code>true</code> si ya existe un nodo con la misma fila y columna
	 *         que el <b>nodoX</b>, <code>false</code> de lo contrario.
	 */
	public boolean conectar(NodoDoble nodoX, boolean reemplazar) {
		return conectarPorFilas(nodoX, reemplazar) || conectarPorColumnas(nodoX, reemplazar);
	}

	/**
	 * Conecta un {@link NodoDoble} <b>x</b> en la posición que le corresponde según
	 * su fila y columna. el {@link NodoDoble} es conectado por filas, osea,
	 * utilizando su liga derecha.<br>
	 * Si ya existe un nodo con la misma <b>fila</b> y <b>columna</b> que el nodo
	 * <b>x</b>, <b>reemplazar</b> determina que operación se hace.
	 * 
	 * @param nodoX      {@link NodoDoble} a conectar.
	 * @param reemplazar Si es <code>true</code> se reemplaza el valor del nodo en
	 *                   la matriz dispersa con el valor del nodo <b>x</b>. De lo
	 *                   contrario no se reemplaza.
	 * @return <code>true</code> si ya existe un nodo con la misma fila y columna
	 *         que el <b>nodoX</b>, <code>false</code> de lo contrario.
	 */
	public boolean conectarPorFilas(NodoDoble nodoX, boolean reemplazar) {
		Tripleta tripletaT = (Tripleta) nodoX.getD();
		int f = tripletaT.getFila();
		int c = tripletaT.getColumna();
		Object v = tripletaT.getValor();

		NodoDoble nodoFila = getPrimerNodo();

		// Se ubica en el nodo cabeza correspondiente a la fila del nodo
		for (int i = 0; i < f; i++) {
			tripletaT = (Tripleta) nodoFila.getD();
			nodoFila = (NodoDoble) tripletaT.getValor();
		}

		NodoDoble antNodoQ = nodoFila;
		NodoDoble nodoQ = nodoFila.getLd();
		tripletaT = (Tripleta) nodoQ.getD();

		// se busca el último nodo con columna menor a 'c'
		while (nodoQ != nodoFila && tripletaT.getColumna() < c) {
			antNodoQ = nodoQ;
			nodoQ = nodoQ.getLd();
			tripletaT = (Tripleta) nodoQ.getD();
		}

		// si el nodo no existe en la matriz
		if (nodoQ == nodoFila || c < tripletaT.getColumna()) {

			// se conecta el nodo entre 'antNodoQ' y 'nodoQ'
			nodoX.setLd(nodoQ);
			antNodoQ.setLd(nodoX);
		} else { // ya existe

			// si 'reemplazar' es 'true'
			if (reemplazar) {
				tripletaT.setValor(v);
			} // else
			return true;
		}

		// se le suma 1 al número de elementos de la fila del nodo cabeza 'p'
		tripletaT = (Tripleta) nodoFila.getD();
		tripletaT.setFila(tripletaT.getFila() + 1);

		return false;
	}

	/**
	 * Conecta un {@link NodoDoble} <b>x</b> en la posición que le corresponde según
	 * su fila y columna. el {@link NodoDoble} es conectado por columnas, osea,
	 * utilizando su liga izquierda.<br>
	 * Si ya existe un nodo con la misma <b>fila</b> y <b>columna</b> que el nodo
	 * <b>x</b>, <b>reemplazar</b> determina que operación se hace.
	 * 
	 * @param nodoX      {@link NodoDoble} a conectar.
	 * @param reemplazar Si es <code>true</code> se reemplaza el valor del nodo en
	 *                   la matriz dispersa con el valor del nodo <b>x</b>. De lo
	 *                   contrario no se reemplaza.
	 * @return <code>true</code> si ya existe un nodo con la misma fila y columna
	 *         que el <b>nodoX</b>, <code>false</code> de lo contrario.
	 */
	public boolean conectarPorColumnas(NodoDoble nodoX, boolean reemplazar) {
		Tripleta tripletaT = (Tripleta) nodoX.getD();
		int f = tripletaT.getFila();
		int c = tripletaT.getColumna();
		Object v = tripletaT.getValor();

		NodoDoble nodoColumna = getPrimerNodo();

		// Se ubica en el nodo cabeza correspondiente a la columna del nodo
		for (int i = 0; i < c; i++) {
			tripletaT = (Tripleta) nodoColumna.getD();
			nodoColumna = (NodoDoble) tripletaT.getValor();
		}

		NodoDoble antNodoQ = nodoColumna;
		NodoDoble nodoQ = nodoColumna.getLi();
		tripletaT = (Tripleta) nodoQ.getD();

		// se busca el último nodo con fila menor a 'f'
		while (nodoQ != nodoColumna && tripletaT.getFila() < f) {
			antNodoQ = nodoQ;
			nodoQ = nodoQ.getLi();
			tripletaT = (Tripleta) nodoQ.getD();
		}

		// si el nodo no existe en la matriz
		if (nodoQ == nodoColumna || f < tripletaT.getFila()) {

			// se conecta el nodo entre 'antNodoQ' y 'nodoQ'
			nodoX.setLi(nodoQ);
			antNodoQ.setLi(nodoX);
		} else { // ya existe

			// si 'reemplazar' es 'true'
			if (reemplazar) {
				tripletaT.setValor(v);
			} // else
			return true;
		}

		// se aumenta el número de elementos de la columna actual
		tripletaT = (Tripleta) nodoColumna.getD();
		tripletaT.setColumna(tripletaT.getColumna() + 1);

		return false;
	}

	/**
	 * Elimina el {@link NodoDoble} con fila y columna (<b>i</b>, <b>j</b>),
	 * desconectandolo por filas y por columnas.
	 * 
	 * @param i Fila del {@link NodoDoble} a eliminar.
	 * @param j Columna del {@link NodoDoble} a eliminar.
	 */
	public void eliminar(int i, int j) {
		desconectarPorFilas(i, j);
		desconectarPorColumnas(i, j);
	}

	/**
	 * Desconecta el {@link NodoDoble} con fila y columna (<b>i</b>, <b>j</b>) por
	 * filas, o sea, utilizando su campo de liga derecha.
	 * 
	 * @param i Fila del {@link NodoDoble} a desconectar.
	 * @param j Columna del {@link NodoDoble} a desconectar.
	 */
	public void desconectarPorFilas(int i, int j) {
		NodoDoble nodoFila;
		Tripleta tripletaT;

		nodoFila = getPrimerNodo();
		tripletaT = (Tripleta) nodoFila.getD();

		// se ubica en la fila 'i'
		for (int fila = 0; fila < i; fila++) {
			nodoFila = (NodoDoble) tripletaT.getValor();
			tripletaT = (Tripleta) nodoFila.getD();
		}

		NodoDoble antNodoQ = nodoFila;
		NodoDoble nodoQ = nodoFila.getLd();
		tripletaT = (Tripleta) nodoQ.getD();

		// se busca el nodo con columna mayor o igual a 'j'
		while (nodoQ != nodoFila && tripletaT.getColumna() < j) {
			antNodoQ = nodoQ;
			nodoQ = nodoQ.getLd();
			tripletaT = (Tripleta) nodoQ.getD();
		}

		// si el nodo no existe en la matriz
		if (nodoQ == nodoFila || j < tripletaT.getColumna()) {
			return;
		} // else: ya existe

		// se desconecta
		antNodoQ.setLd(nodoQ.getLd());
		nodoQ.setLd(null);

		// se le resta 1 al número de elementos de la fila del nodoFila
		tripletaT = (Tripleta) nodoFila.getD();
		tripletaT.setFila(tripletaT.getFila() - 1);
	}

	/**
	 * Desconecta el {@link NodoDoble} con fila y columna (<b>i</b>, <b>j</b>) por
	 * columnas, o sea, utilizando su campo de liga izquierda.
	 * 
	 * @param i Fila del {@link NodoDoble} a desconectar.
	 * @param j Columna del {@link NodoDoble} a desconectar.
	 */
	public void desconectarPorColumnas(int i, int j) {
		NodoDoble nodoColumna;
		Tripleta tripletaT;

		nodoColumna = getPrimerNodo();
		tripletaT = (Tripleta) nodoColumna.getD();

		// se ubica en la columna 'j'
		for (int columna = 0; columna < j; columna++) {
			nodoColumna = (NodoDoble) tripletaT.getValor();
			tripletaT = (Tripleta) nodoColumna.getD();
		}

		NodoDoble antNodoQ = nodoColumna;
		NodoDoble nodoQ = nodoColumna.getLi();
		tripletaT = (Tripleta) nodoQ.getD();

		// se busca el nodo con fila mayor o igual a 'i'
		while (nodoQ != nodoColumna && tripletaT.getFila() < i) {
			antNodoQ = nodoQ;
			nodoQ = nodoQ.getLi();
			tripletaT = (Tripleta) nodoQ.getD();
		}

		// si el nodo no existe en la matriz
		if (nodoQ == nodoColumna || i < tripletaT.getFila()) {
			return;
		} // else: ya existe

		// se desconecta
		antNodoQ.setLi(nodoQ.getLi());
		nodoQ.setLi(null);

		// se le resta 1 al número de elementos de la columna del nodoColumna
		tripletaT = (Tripleta) nodoColumna.getD();
		tripletaT.setColumna(tripletaT.getColumna() - 1);
	}

	/**
	 * Retorna el {@link NodoDoble} que representa la matriz dispersa.
	 * 
	 * @return {@link NodoDoble} {@link #mat}
	 */
	public NodoDoble getNodoCabeza() {
		return this.mat;
	}

	/**
	 * Retorna el primer {@link NodoDoble} cabeza, nodo que corresponde tanto a la
	 * primera fila, como la primera columna de la matriz dispersa.
	 * <p>
	 * Este {@link NodoDoble} está contenido en el campo <b>valor</b> de la
	 * {@link Tripleta} del {@link NodoDoble} cabeza {@link #mat}.
	 * 
	 * @return {@link NodoDoble} cabeza que representa la primera fila y la primera
	 *         columna de la matriz dispersa.
	 */
	public NodoDoble getPrimerNodo() {
		Tripleta tripletaT = (Tripleta) this.mat.getD();
		NodoDoble primerNodo = (NodoDoble) tripletaT.getValor();
		return primerNodo;
	}

	/**
	 * Retorna el elemento nulo o vacío que tiene definido esta matriz dispersa.
	 * 
	 * @return {@link #nulo}.
	 */
	public Object getNulo() {
		return this.nulo;
	}

	/**
	 * Retorna el número de filas que tiene esta matriz dispersa, el cual está en el
	 * campo <b>fila</b> de la {@link Tripleta} contenida en el {@link NodoDoble}
	 * cabeza {@link #mat}.
	 * 
	 * @return Número de filas de la matriz dispersa.
	 */
	public int getNumFilas() {
		Tripleta tripletaT = (Tripleta) this.mat.getD();
		int numFilas = tripletaT.getFila();
		return numFilas;
	}

	/**
	 * Retorna el número de columnas que tiene esta matriz dispersa, el cual está en
	 * el campo <b>columna</b> de la {@link Tripleta} contenida en el
	 * {@link NodoDoble} cabeza {@link #mat}.
	 * 
	 * @return Número de columnas de la matriz dispersa.
	 */
	public int getNumColumnas() {
		Tripleta tripletaT = (Tripleta) this.mat.getD();
		int numColumnas = tripletaT.getColumna();
		return numColumnas;
	}

	/**
	 * Retorna el número de elementos diferentes del elemento nulo de la matriz
	 * dispersa, contando le número de elementos diferentes de {@link #nulo} de cada
	 * fila.
	 * 
	 * @return Número de elementos diferentes de {@link #nulo}.
	 */
	public int getNumElementos() {
		NodoDoble nodoFila;
		Tripleta tripletaT;
		int count = 0;

		nodoFila = getPrimerNodo();
		tripletaT = (Tripleta) nodoFila.getD();

		while (nodoFila != getNodoCabeza()) {
			count = count + tripletaT.getFila();
			nodoFila = (NodoDoble) tripletaT.getValor();
			tripletaT = (Tripleta) nodoFila.getD();
		}

		return count;
	}

	/**
	 * Busca un {@link NodoDoble} con fila y columna (<b>i</b>, <b>j</b>).
	 * 
	 * @param i Fila del {@link NodoDoble} a buscar.
	 * @param j Columna del {@link NodoDoble} a buscar.
	 * @return {@link NodoDoble} con fila y columna (<b>i</b>, <b>j</b>) si existe,
	 *         de lo contrario retorna <code>null</code>.
	 */
	protected NodoDoble getNodo(int i, int j) {
		int m, n;
		m = getNumFilas();
		n = getNumColumnas();

		assert (0 <= i && i < m && 0 <= j && j < n) : "i, j deben de estar dentro del rango de la matriz";

		NodoDoble nodoFila, nodoX;
		nodoFila = getPrimerNodo();
		Tripleta tripletaT = (Tripleta) nodoFila.getD();

		// se ubica en la fila 'i'
		for (int fila = 0; fila < i; fila++) {
			nodoFila = (NodoDoble) tripletaT.getValor();
			tripletaT = (Tripleta) nodoFila.getD();
		}

		// busca el primer nodo con columna mayor o igual a 'j'
		nodoX = nodoFila.getLd();
		tripletaT = (Tripleta) nodoX.getD();
		while (nodoX != nodoFila && tripletaT.getColumna() < j) {
			nodoX = nodoX.getLd();
			tripletaT = (Tripleta) nodoX.getD();
		}

		// si el nodo es diferente del nodoFila y tiene columna igual a 'j', lo retorna
		if (nodoX != nodoFila && tripletaT.getColumna() == j) {
			return nodoX;
		} // else
		return null;
	}

	/**
	 * Busca en la matriz dispersa el dato correspondiente a los índices (<b>i</b>,
	 * <b>j</b>), si alguno de los indices está fuera del rango de la matriz
	 * dispersa, ocurre una excepción.
	 *
	 * @param i Índice correspondiente a la fila.
	 * @param j Índice correcpondiente a la columna.
	 * @return El dato en la posición (<b>i</b>, <b>j</b>). Si no se encuentra
	 *         retorna {@link #nulo}.
	 */
	public Object get(int i, int j) {
		int m = getNumFilas();
		int n = getNumColumnas();

		// se comprueban que los índices están dentro del rango del tamaño de la matriz
		if ((i < 0 || i >= m) || (j < 0 || j >= n)) {
			String error = "el índice 'i' debe ser 0 <= i < %d, \\n y 'j' debe ser 0 <= j < %d";
			throw new IndexOutOfBoundsException(String.format(error, m, n));
		}

		Tripleta tripletaT;
		NodoDoble nodoFila;
		NodoDoble nodo;

		nodoFila = getPrimerNodo();
		tripletaT = (Tripleta) nodoFila.getD();

		// se busca la fila correspondiente
		for (int fila = 0; fila < i; fila++) {
			nodoFila = (NodoDoble) tripletaT.getValor();
			tripletaT = (Tripleta) nodoFila.getD();
		}

		nodo = nodoFila.getLd();
		tripletaT = (Tripleta) nodo.getD();

		// se busca la primera columna mayor o igual al índice 'j'
		while (nodo != nodoFila && tripletaT.getColumna() < j) {
			nodo = nodo.getLd();
			tripletaT = (Tripleta) nodo.getD();
		}

		// Si recorrió toda la fila o la columna 'j' no se encontró, el dato a devolver
		// es nulo
		if (nodo == nodoFila || j < tripletaT.getColumna()) {
			return this.nulo;
		} else {
			return tripletaT.getValor();
		}
	}

	/**
	 * Crea un nodo con los datos entregados y lo inserta en donde corresponde.
	 * <p>
	 * - Si ya existe un nodo con la misma <b>fila</b> y <b>columna</b>, se
	 * reemplaza el valor de este siempre que sea diferente del {@link #nulo} de la
	 * matriz, si es {@link #nulo} se elimina ese nodo.<br>
	 * - Si alguno de los indices está fuera del rango de la matriz, ocurre una
	 * excepción.
	 * 
	 * @param i Fila en donde se asigna el valor <b>v</b>.
	 * @param j Columna en donde se asigna el valor <b>v</b>.
	 * @param v Valor que se asigna en la posición (<b>i</b>, <b>j</b>).
	 */
	public void set(int i, int j, Object v) {
		int m = getNumFilas();
		int n = getNumColumnas();

		// se comprueban que los índices están dentro del rango del tamaño de la matriz
		if ((i < 0 || i >= m) || (j < 0 || j >= n)) {
			String error = "el índice 'i' debe ser 0 <= i < %d, \n y 'j' debe ser 0 <= j < %d";
			throw new IndexOutOfBoundsException(String.format(error, m, n));
		}

		if (v == getNulo()) {
			eliminar(i, j);
			return;
		}

		Tripleta tripletaT = new Tripleta(i, j, v);
		NodoDoble nodoX = new NodoDoble(tripletaT);
		conectar(nodoX, true);
	}

	/**
	 * Suma dos matrices dispersas, recorriendo sus filas y añadiendo un nuevo
	 * {@link NodoDoble} por cada suma diferente de {@link #nulo}. <br>
	 * Además, para que la suma se dé, se tienen que cumplir ciertas condiciones, de
	 * lo contrario ocurrirá una excepción.
	 * <p>
	 * Las condiciones son: <br>
	 * - Las dimensiones de las dos matrices deben ser iguales. <br>
	 * - Los datos de cada posición de las matrices deben ser del mismo tipo de
	 * dato. <br>
	 * - El elemento <b>nulo</b> de las dos matrices debe ser el mismo.
	 * <p>
	 * <b>Nota:</b> El tipo de dato debe ser inmutable, de lo contrario es posible
	 * que los objetos de las matrices a sumar queden duplicados en la matriz
	 * resultante, y por tanto puedan ser cambiados desde 2 o más matrices.
	 * <p>
	 * Veáse {@link Tripleta#copiar()}.
	 * 
	 * @param matrizB Matriz dispersa a sumar
	 * @param sumador Función de tipo {@link BiFunction} para sumar los elementos de
	 *                las matrices dispersas. Este método se utiliza para sumar
	 *                diferentes tipos de datos según sea el caso, debe ser de la
	 *                forma <br>
	 *                <i><b>({@link Object} a, {@link Object} b) -
	 *                {@link Object}</b></i>.
	 * @return Una nueva {@link MatrizForma1} la cual es la suma de esta matriz
	 *         dispersa con la matriz dispersa <b>matrizB</b>.
	 */
	public MatrizForma1 sumar(MatrizForma1 matrizB, BiFunction<Object, Object, Object> sumador) {

		MatrizForma1 matrizA = this;

		// se comprueba que las dimensiones sean iguales
		assert (matrizA.getNumFilas() == matrizB.getNumFilas() && matrizA.getNumColumnas() == matrizB.getNumColumnas())
				: "las dimensiones de las matrices deben ser iguales";

		// se comprueba que los elementos nulos sean iguales
		assert (matrizA.getNulo() == matrizB.getNulo()) : "los elementos 'nulo' de las matrices deben ser los mismos";

		// se crea la matriz a retornar
		MatrizForma1 matrizC = new MatrizForma1(matrizA.getNumFilas(), matrizA.getNumColumnas(), matrizA.getNulo());

		// nodos para recorrer las filas de las matrices
		NodoDoble nodoFilaA = matrizA.getPrimerNodo();
		NodoDoble nodoFilaB = matrizB.getPrimerNodo();

		NodoDoble nodoA, nodoB, nodoC;
		Tripleta tripletaA, tripletaB, tripletaC;
		Object nulo = matrizA.getNulo();
		int cA, cB;
		Object suma;

		while (nodoFilaA != matrizA.getNodoCabeza()) {

			// con estos nodos se avanza en la misma fila de las dos matrices
			nodoA = nodoFilaA.getLd();
			nodoB = nodoFilaB.getLd();

			while (nodoA != nodoFilaA && nodoB != nodoFilaB) {

				// se obtienen el valor de la columna de los dos nodos
				tripletaA = (Tripleta) nodoA.getD();
				tripletaB = (Tripleta) nodoB.getD();
				cA = tripletaA.getColumna();
				cB = tripletaB.getColumna();

				// se comparan las columnas

				if (cA < cB) { // si 'cA' es menor que 'cB' se añade un nodo con la tripleta A

					nodoC = new NodoDoble(tripletaA.copiar());
					matrizC.conectar(nodoC, true);

					// se avanza en la fila de la matriz A
					nodoA = nodoA.getLd();
				} else if (cA > cB) { // si 'cB' < 'cA' se añade un nodo con la tripleta B

					nodoC = new NodoDoble(tripletaB.copiar());
					matrizC.conectar(nodoC, true);

					// se avanza en la fila de la matriz B
					nodoB = nodoB.getLd();
				} else { // si 'cA' == 'cB', se suman los valores de las tripletas

					// si los valores son de diferente tipo salta una excepción
					if (tripletaA.getValor().getClass() != tripletaB.getValor().getClass()) {
						String error = "la suma no se puede efectuar, los tipos de dato en la posición (%d, %d) son diferentes";
						throw new AssertionError(String.format(error, tripletaA.getFila(), tripletaA.getColumna()));
					}

					suma = sumador.apply(tripletaA.getValor(), tripletaB.getValor());

					// si la suma es diferente del elemento nulo o vacío, se añade a la matriz C
					if (suma != nulo) {
						tripletaC = new Tripleta(tripletaA.getFila(), cA, suma);
						nodoC = new NodoDoble(tripletaC);
						matrizC.conectar(nodoC, true);
					}

					// se avanza con en las dos filas de las matrices
					nodoA = nodoA.getLd();
					nodoB = nodoB.getLd();
				}
			}

			// si quedan nodos en la fila de la matriz A, se añaden a la matriz C
			while (nodoA != nodoFilaA) {
				tripletaA = (Tripleta) nodoA.getD();
				nodoC = new NodoDoble(tripletaA.copiar());
				matrizC.conectar(nodoC, true);
				nodoA = nodoA.getLd();
			}

			// si quedan nodos en la fila de la matriz B, se añaden a la matriz C
			while (nodoB != nodoFilaB) {
				tripletaB = (Tripleta) nodoB.getD();
				nodoC = new NodoDoble(tripletaB.copiar());
				matrizC.conectar(nodoC, true);
				nodoB = nodoB.getLd();
			}

			// se avanza a la siguiente fila de las dos matrices
			tripletaA = (Tripleta) nodoFilaA.getD();
			tripletaB = (Tripleta) nodoFilaB.getD();
			nodoFilaA = (NodoDoble) tripletaA.getValor();
			nodoFilaB = (NodoDoble) tripletaB.getValor();
		}

		return matrizC;
	}

	/**
	 * Muestra los elementos diferentes de {@link #nulo} de la matriz dispersa,
	 * ordenado ascendentemente por filas o columnas, según el valor del parámetro
	 * <b>mode</b>.
	 * 
	 * @param mode Define el orden en el que se muestran los elementos diferenetes
	 *             de <b>0</b>. <br>
	 *             Si es <b>0</b>, se muestra ordenado por filas, si es <b>1</b>,
	 *             ordenado por columnas.<br>
	 *             Si es diferente de <b>0</b> o <b>1</b> provocará un
	 *             {@link AssertionError}.
	 */
	public void show(int mode) {

		assert (mode == 0 || mode == 1) : "\\'mode\\' debe ser 0 o 1";

		NodoDoble nodoCabeza = getPrimerNodo();

		NodoDoble nodoP; // NodoDoble para recorrer la matriz

		Tripleta tCabeza = (Tripleta) nodoCabeza.getD();

		int m, n, f, c;
		Object v;
		Tripleta t;

		System.out.println(String.format("[%d, %d]", getNumFilas(), getNumColumnas())); // tamaño de la matriz

		if (mode == 0) {
			m = getNumFilas(); // número de filas de la matriz
		} else {
			m = getNumColumnas(); // número de columnas de la matriz
		}
		for (int i = 0; i < m; i++) {

			if (mode == 0) {
				n = tCabeza.getFila(); // número de elementos de la fila
			} else {
				n = tCabeza.getColumna(); // número de elementos de la fila
			}

			nodoP = nodoCabeza;
			for (int elemento = 0; elemento < n; elemento++) {

				if (mode == 0) {
					nodoP = nodoP.getLd(); // la liga derecha es para recorrer la fila
				} else {
					nodoP = nodoP.getLi(); // la liga izquierda es para recorrer la columna
				}

				t = (Tripleta) nodoP.getD();

				f = t.getFila();
				c = t.getColumna();
				v = t.getValor();
				System.out.println(String.format("(%d, %d, %s)", f, c, v)); // (fila, columna, valor)
			}

			// siguiente fila o columna
			nodoCabeza = (NodoDoble) tCabeza.getValor();
			tCabeza = (Tripleta) nodoCabeza.getD();
		}
	}

	public void showAsArrayOfArrays() {
		NodoDoble nodoFila, nodoP;
		Tripleta tripletaT;
		int f, c;
		Object v;
		String output;

		nodoFila = getPrimerNodo();
		nodoP = nodoFila.getLd();
		while (nodoFila != getNodoCabeza() && nodoP == nodoFila) {
			tripletaT = (Tripleta) nodoFila.getD();
			nodoFila = (NodoDoble) tripletaT.getValor();
			nodoP = nodoFila.getLd();
		}
		tripletaT = (Tripleta) nodoP.getD();
		f = tripletaT.getFila();
		c = tripletaT.getColumna();
		v = tripletaT.getValor();

		for (int i = 0; i < getNumFilas(); i++) {
			for (int j = 0; j < getNumColumnas(); j++) {
				if (j == getNumColumnas() - 1) {
					output = "%s\n";
				} else {
					output = "%s, ";
				}
				if (nodoFila != getNodoCabeza() && nodoP != nodoFila) {
					if (i == f && j == c) {
						System.out.print(String.format(output, v));
						nodoP = nodoP.getLd();

						while (nodoFila != getNodoCabeza() && nodoP == nodoFila) {
							tripletaT = (Tripleta) nodoFila.getD();
							nodoFila = (NodoDoble) tripletaT.getValor();
							nodoP = nodoFila.getLd();
						}
						if (nodoP != null) {
							tripletaT = (Tripleta) nodoP.getD();
							f = tripletaT.getFila();
							c = tripletaT.getColumna();
							v = tripletaT.getValor();
						}
					} else {
						System.out.print(String.format(output, getNulo()));
					}
				} else {
					System.out.print(String.format(output, getNulo()));
				}
			}
		}
	}
}
