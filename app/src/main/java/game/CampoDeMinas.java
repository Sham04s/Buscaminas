package game;

import java.util.Random;
import java.util.Scanner;

import matricesDispersas.MatrizForma1;
import matricesDispersas.NodoDoble;
import matricesDispersas.Tripleta;

/**
 * Clase que representa el campo de minas del buscaminas, usando matrices
 * dispersas en forma 1.
 * <p>
 * Se utiliza una matriz dispersa para las casilla con minas y números (que no
 * sean 0), y otra para el estado de cada casilla.
 * <p>
 * Para interactuar con el campo de minas se utiliza:<br>
 * - {@link #tocarCasilla(int, int)} <br>
 * - {@link #posibleMina(int, int)}
 * <p>
 * Para obtener recorrer el campo de minas de forma óptima, es preferible
 * recorrerlo como {@link MatrizForma1}. Esta {@link MatrizForma1} contiene los
 * valores de las casillas, si el valor de una determinada casilla es <b>-1</b>,
 * esa casilla es una mina, de lo contrario, es un número. Además el nulo de
 * esta matriz es <b>0</b>, por tanto la matriz no contiene ningún
 * {@link NodoDoble} con valor 0.
 * <p>
 * La {@link MatrizForma1} {@link #estadoDeCasillas} contiene el estado de cada
 * casilla, y su nulo es {@link EstadoDeCasilla#oculta}.
 * 
 * @author sneyd
 *
 * @see #CampoDeMinas(int, int, int)
 */
public class CampoDeMinas extends MatrizForma1 {

	/**
	 * Enumerado para controlar el estado del campo de minas.
	 * <p>
	 * Sus estados son:<br>
	 * - noIniciado <br>
	 * - iniciado <br>
	 * - perdido <br>
	 * - ganado
	 */
	public enum Estado {
		noIniciado, iniciado, perdido, ganado
	}

	/**
	 * Enumerado para controlar el estado de cada casilla, incluyendo las que son 0.
	 * <p>
	 * Sus estados son: <br>
	 * - posibleMina <br>
	 * - oculta (es similar al nulo) <br>
	 * - descubierta
	 */
	public enum EstadoDeCasilla {
		posibleMina, oculta, descubierta
	}

	/**
	 * Indica el estado actual del campo de minas.
	 */
	private Estado estado;

	/**
	 * Número de minas que contiene el campo de minas.
	 */
	private final int minas;

	/**
	 * Número de casilla marcadas como posibles minas.
	 */
	private int posiblesMinas;

	/**
	 * Para almacenar la posición de la primera casilla despejada.
	 * 
	 * @see #tocarCasilla(int, int)
	 */
	private int primerDespeje[];

	/**
	 * Con esta variable se controla que no se generen nuevas minas al tocar de
	 * nuevo la primera casilla oculta.
	 */
	private boolean yaCreado;

	/**
	 * Matriz dispersa que indica el estado de cada casilla usando el enumerado
	 * {@link EstadoDeCasilla}.
	 */
	private MatrizForma1 estadoDeCasillas;

	/**
	 * {@link Random} para generar las minas aleatoriamente dentro del rango de la
	 * matriz.
	 */
	private Random rand = new Random();

	/**
	 * Constructor. Inicializa el campo de minas. A diferencia del otro constructor,
	 * la creación de minas se hace al despejar la primera casilla oculta, esto para
	 * asegurar que el jugador no toque una mina al interactuar por primera vez con
	 * el campo.
	 * <p>
	 * Para crear el campo de minas: <br>
	 * - <b>m</b> y <b>n</b> deben ser mayores a 0.<br>
	 * - El número mínimo de <b>minas</b> es 1, y el máximo es el total de casillas
	 * menos 1 (cuando solo hay una casilla que no es una mina).
	 * 
	 * @param m     Número de filas del campo de minas.
	 * @param n     Número de columnas del campo de minas
	 * @param minas Número de minas que se crearán en el campo de minas. <br>
	 *              Debe estar dentro del rango de la matriz (0 &lt; <b>minas</b>
	 *              &lt; <b><i>m</i></b> * <b><i>n</i></b>).
	 */
	public CampoDeMinas(int m, int n, int minas) {
		// crea la matriz dispersa
		super(m, n, 0);

		// se comprueba que el número de minas este dentro del rango de la matriz
		assert (0 < minas && minas < m * n)
				: "el número de minas debe ser menor al número total de casillas y mayor a cero";

		this.estado = Estado.noIniciado;
		this.minas = minas;
		this.posiblesMinas = 0;
		this.primerDespeje = null;
		this.yaCreado = false;
		this.estadoDeCasillas = new MatrizForma1(m, n, EstadoDeCasilla.oculta);
	}

	/**
	 * Genera el campo de minas de forma aleatoria.
	 */
	private void generarCampoDeMinas() {

		// se generan las minas aleatoriamente
		for (int i = 0; i < this.minas; i++) {
			generarMina();
		}
	}

	/**
	 * Pone una mina en una posición aleatoria y actualiza las casillas alrededor
	 * sumando 1.
	 */
	private void generarMina() {
		int f, c, m, n;
		NodoDoble nodoX;
		Tripleta cMina, casillaX;

		m = getNumFilas();
		n = getNumColumnas();

		// se generan la fila y columna dentro del rango de la matriz
		f = rand.nextInt(m);
		c = rand.nextInt(n);

		if (primerDespeje != null) {

			// si la primera casilla despejada es donde se quiere generar la mina
			if (primerDespeje[0] == f && primerDespeje[1] == c) {

				// se genera en otro lugar para asegurar que nunca se pierde al empezar la
				// partida
				generarMina();
				return;
			}
		}

		nodoX = getNodo(f, c);

		// si ya existe un nodo en esa posición
		if (nodoX != null) {
			casillaX = (Tripleta) nodoX.getD();

			// si ese nodo es una mina, se genera otra mina con diferente posición, y se
			// cancela la creación de la mina actual
			if (casillaX.getValor().equals(-1)) {
				generarMina();
				return;
			} else { // si es un numero, se reemplaza con una mina
				cMina = new Tripleta(f, c, -1);
				nodoX.setD(cMina);
			}
		} else { // si no existe, se crea una mina
			cMina = new Tripleta(f, c, -1);
			nodoX = new NodoDoble(cMina);
			conectar(nodoX, true);
		}

		// se aumenta en uno las casillas alrededor siempre que no sean minas
		for (int i = f - 1; i <= f + 1; i++) {
			for (int j = c - 1; j <= c + 1; j++) {
				try {
					sumarACasilla(i, j, 1);
				} catch (IndexOutOfBoundsException e) {
				}
			}
		}

	}

	/**
	 * Suma <b>num</b> a la casilla del {@link NodoDoble} con fila y columna
	 * (<b>i</b>, <b>j</b>). Esto es util para actualizar las casillas vecinas al
	 * añadir y eliminar minas a conveniencia.
	 * <p>
	 * Si la casilla es una mina, se deja intacta. <br>
	 * Si <b>i</b> o <b>j</b> estan fuera de la matriz, no se hace nada.
	 * <p>
	 * Este método es utilizado por el método {@link #generarMina()} para actualizar
	 * las casillas alrededor de una mina creada, y por
	 * {@link #tocarCasilla(int, int)} para asegurar que la primera casilla que se
	 * toca no sea una mina.
	 * 
	 * @param i         Fila de la casilla.
	 * @param j         Columna de la casilla.
	 * @param num Número a sumar a la casilla.
	 */
	private void sumarACasilla(int i, int j, int num) {
		int m, n;
		m = getNumFilas();
		n = getNumColumnas();

		// si (i, j) esta por fuera de la matriz
		if (i < 0 || m <= i || j < 0 || n <= j) {
			return;
		}

		NodoDoble nodoX;
		Tripleta casillaX;
		nodoX = getNodo(i, j);

		// Si no existe el nodo con posición (i,j) se crea uno y se aumenta a 1 su valor
		if (nodoX == null) {
			casillaX = new Tripleta(i, j, 1);
			nodoX = new NodoDoble(casillaX);
			conectar(nodoX, false);
		} else { // si ya existe

			casillaX = (Tripleta) nodoX.getD();

			// si no es una mina se aumenta su valor
			if (!casillaX.getValor().equals(-1)) {
				casillaX.setValor((int) casillaX.getValor() + 1);
			} // else: es mina, por lo tanto se deja intacta
		}
	}

	/**
	 * Método principal para interactuar con el campo de minas, el cual despeja la
	 * casilla si está oculta, en caso de que este descubierta, se despejan sus
	 * vecinos siempre que el número de minas marcadas alrededor de está sea mayor o
	 * igual al número de la casilla.
	 * 
	 * @param i Fila    de la casilla con la cual se quiere interactuar.
	 * @param j Columna de la casilla con la cual se quiere interactuar.
	 * @throws IndexOutOfBoundsException &gt; Si (<b>i</b>, <b>j</b>) está por fuera
	 *                                   de la matriz.
	 */
	public void tocarCasilla(int i, int j) throws IndexOutOfBoundsException {
		int m, n, marcadas, v;
		m = getNumFilas();
		n = getNumColumnas();

		// si (i, j) esta por fuera de la matriz
		if (i < 0 || m <= i || j < 0 || n <= j) {
			throw new IndexOutOfBoundsException();
		}

		// si ya se terminó el juego o no se ha iniciado
		if (getEstado() != Estado.iniciado) {
			return;
		}

		v = (int) get(i, j);

		EstadoDeCasilla estado = getEstadoDeCasilla(i, j);
		switch (estado) {
		case descubierta:

			// si es cero no se hace nada
			if (v == 0) {
				return;
			}

			marcadas = 0;

			// se cuentan las minas marcadas y las casilla ocultas alrededor de esta
			for (int ii = i - 1; ii <= i + 1; ii++) {
				for (int jj = j - 1; jj <= j + 1; jj++) {
					try {
						if (esPosibleMina(ii, jj)) {
							marcadas++;
						}
					} catch (IndexOutOfBoundsException e) {
					}
				}
			}

			// si las minas marcadas son mayores o iguales al número
			if (marcadas >= v) {
				despejarVecinos(i, j);
			}

			break;

		case oculta:

			// si no se ha creado el campo de minas y es la primera casilla que se toca se
			// genera el campo de minas (esto asegura no perder al tocar la primera casilla)
			if (yaCreado == false && primerDespeje == null) {

				primerDespeje = new int[2];
				primerDespeje[0] = i;
				primerDespeje[1] = j;

				generarCampoDeMinas();
			}
			despejarCasilla(i, j);
			break;

		default:
			break;
		}
	}

	/**
	 * Método secundario para interactuar con el campo de minas.<br>
	 * Marca o desmarca la casilla en la posición (<b>i</b>, <b>j</b>) como posible
	 * mina según sea el caso, y siempre que este oculta.
	 * 
	 * @param i - Fila de la casilla.
	 * @param j Columna de la casilla.
	 * @throws IndexOutOfBoundsException Si (<b>i</b>, <b>j</b>) están por fuera de
	 *                                   la matriz.
	 */
	public void posibleMina(int i, int j) throws IndexOutOfBoundsException {
		int m = getNumFilas();
		int n = getNumColumnas();

		// si (i, j) esta por fuera de la matriz
		if (i < 0 || m <= i || j < 0 || n <= j) {
			throw new IndexOutOfBoundsException();
		}

		// si ya se terminó el juego o no se ha iniciado
		if (getEstado() != Estado.iniciado) {
			return;
		}

		if (!esCasillaDescubierta(i, j)) {
			if (esPosibleMina(i, j)) {
				setEstadoDeCasilla(i, j, EstadoDeCasilla.oculta);
				this.posiblesMinas--;
			} else {
				setEstadoDeCasilla(i, j, EstadoDeCasilla.posibleMina);
				this.posiblesMinas++;
			}
		}
	}

	/**
	 * Despeja la casilla ubicada en la posición (<b>i</b>, <b>j</b>) en el campo de
	 * minas, siempre que este oculta.
	 * <p>
	 * Si <b>i</b> o <b>j</b> están por fuera de rango ocurrirá una excepción.
	 * 
	 * @param i Fila de la casilla a despejar.
	 * @param j Columna de la casilla a despejar.
	 * @throws IndexOutOfBoundsException Si (<b>i</b>, <b>j</b>) están por fuera de
	 *                                   la matriz.
	 */
	private void despejarCasilla(int i, int j) throws IndexOutOfBoundsException {
		int m = getNumFilas();
		int n = getNumColumnas();

		// si (i, j) esta por fuera de la matriz
		if (i < 0 || m <= i || j < 0 || n <= j) {
			throw new IndexOutOfBoundsException();
		}

		// si esta marcada como posible mina o esta descubierta
		if (esPosibleMina(i, j) || esCasillaDescubierta(i, j)) {
			return;
		}

		// si ya se terminó el juego o no se ha iniciado
		if (getEstado() != Estado.iniciado) {
			return;
		}

		// se añade esta posición como descubierta a el estado de la casilla
		setEstadoDeCasilla(i, j, EstadoDeCasilla.descubierta);

		// si existe una casilla en la posición (i,j)
		NodoDoble nodoX = getNodo(i, j);
		if (nodoX != null) {

			// si es una mina
			Tripleta cX = (Tripleta) nodoX.getD();
			if (cX.getValor().equals(-1)) {

				// si no se ha ganado aún, se establece el estado como perdido.
				if (getEstado() == Estado.iniciado) {
					setEstado(Estado.perdido);
					estadoDeCasillas = new MatrizForma1(m, n, EstadoDeCasilla.descubierta);
					return;
				}
			}
		} else { // es cero

			despejarVecinos(i, j);
		}

		// si las casillas descubiertas son iguales a las casillas sin minas, se procede
		// a ganar
		if (estadoDeCasillas.getNumElementos() - this.posiblesMinas == m * n - minas) {
			setEstado(Estado.ganado);
			estadoDeCasillas = new MatrizForma1(m, n, EstadoDeCasilla.descubierta);
		}
	}

	/**
	 * Despeja las casilla alrededor de la casilla (<b>i</b>, <b>j</b>).
	 * <p>
	 * Este método es utilizado por {@link #despejarCasilla(int, int)} y
	 * {@link #tocarCasilla(int, int)}, por tanto <b>i</b>, <b>j</b> siempre están
	 * dentro del rango de la matriz.
	 * 
	 * @param i Fila de la casilla alrededor de la cual se despeja.
	 * @param j Columna de la casilla alrededor de la cual se despeja.
	 */
	private void despejarVecinos(int i, int j) {

		// se despejan las casillas alrededor de esta
		for (int ii = i - 1; ii <= i + 1; ii++) {
			for (int jj = j - 1; jj <= j + 1; jj++) {
				try {
					despejarCasilla(ii, jj);
				} catch (IndexOutOfBoundsException e) {
				}
			}
		}
	}

	/**
	 * Establece el estado del campo de minas como iniciado. Esto para permitir el
	 * despeje de las casilla.
	 */
	public void iniciar() {
		if (getEstado() == Estado.noIniciado) {
			setEstado(Estado.iniciado);
		}
	}

	/**
	 * Oculta todas las casilla nuevamente y establece el estado como
	 * {@link Estado#noIniciado}. También reinicia las {@link #posiblesMinas}.
	 */
	public void reiniciar() {
		int m, n;
		m = getNumFilas();
		n = getNumColumnas();

		this.estadoDeCasillas = new MatrizForma1(m, n, EstadoDeCasilla.oculta);
		setEstado(Estado.noIniciado);
		this.posiblesMinas = 0;
		this.primerDespeje = null;
		;
		this.yaCreado = true;
	}

	/**
	 * Indica si la casilla en la posición (<b>i</b>, <b>j</b>) esta descubierta.
	 * 
	 * @param i Fila de la casilla.
	 * @param j Columna de la casilla.
	 * @return <code>true</code> si la casilla es una casilla ya descubierta o ya
	 *         despejada, <code>false</code> de lo contrario.
	 */
	private boolean esCasillaDescubierta(int i, int j) {
		EstadoDeCasilla estado = (EstadoDeCasilla) getEstadoDeCasilla(i, j);
		;
		return (estado == EstadoDeCasilla.descubierta);
	}

	/**
	 * Indica si la casilla en la posición (<b>i</b>, <b>j</b>) está marcada como
	 * posible mina o no.
	 * 
	 * @param i Fila de la casilla.
	 * @param j Columna de la casilla.
	 * @return <code>true</code> si la casilla está marcada como posible mina,
	 *         <code>false</code> de lo contrario.
	 */
	private boolean esPosibleMina(int i, int j) {
		EstadoDeCasilla estado = getEstadoDeCasilla(i, j);
		return estado == EstadoDeCasilla.posibleMina;
	}

	/**
	 * Devuelve el número de casillas marcadas como posibles minas.
	 * 
	 * @return Número de posibles minas.
	 */
	public int getPosiblesMinas() {
		return this.posiblesMinas;
	}

	/**
	 * Devuelve el número de minas que contiene el campo de minas.
	 * 
	 * @return {@link #minas}.
	 */
	public int getNumMinas() {
		return this.minas;
	}

	/**
	 * Establece el estado del campo de minas.
	 * 
	 * @param estado {@link Estado} a establecer.
	 * 
	 * @see Estado
	 */
	private void setEstado(Estado estado) {
		this.estado = estado;
	}

	/**
	 * Retorna el estado del campo de minas. Esto puede usarse para comprobar si se
	 * termina o no el juego, y si se gana o se pierde.
	 * 
	 * @return {@link Estado} del campo de minas.
	 * 
	 * @see Estado
	 */
	public Estado getEstado() {
		return this.estado;
	}

	/**
	 * Establece el estado de la casilla en la posición (<b>i</b>, <b>j</b>).
	 * 
	 * @param i      Fila de la casilla.
	 * @param j      Columna de la casilla.
	 * @param estado {@link EstadoDeCasilla} a establecer.
	 * 
	 * @see #estadoDeCasillas
	 */
	private void setEstadoDeCasilla(int i, int j, EstadoDeCasilla estado) {
		this.estadoDeCasillas.set(i, j, estado);
	}

	/**
	 * Devuelve el estado de la casilla en la posición (<b>i</b>, <b>j</b>).
	 * 
	 * @param i Fila de la casilla.
	 * @param j Columna de la casilla.
	 * @return {@link EstadoDeCasilla}.
	 * 
	 * @see #estadoDeCasillas
	 */
	public EstadoDeCasilla getEstadoDeCasilla(int i, int j) {
		int m = getNumFilas();
		int n = getNumColumnas();

		// si (i, j) esta por fuera de la matriz
		if (i < 0 || m <= i || j < 0 || n <= j) {
			throw new IndexOutOfBoundsException();
		}

		return (EstadoDeCasilla) this.estadoDeCasillas.get(i, j);
	}

	/**
	 * Retorna la {@link MatrizForma1} que contiene el estado de cada casilla del
	 * campo de minas, sea una casilla nula (con valor 0) o no.
	 * 
	 * @return {@link #estadoDeCasillas}.
	 */
	public MatrizForma1 getEstadoDeCasillas() {
		return this.estadoDeCasillas;
	}

	/**
	 * Imprime en la consola el campo de minas en forma de cuadrícula, incluyendo
	 * sus índices, tanto de las filas, como de las columnas. También imprime el
	 * número de posibles minas en una esquina.
	 */
	@Override
	public void showAsArrayOfArrays() {
		NodoDoble nodoFila, nodoP;
		Tripleta tripletaT, casillaX;
		int f, c, m, n;
		Object v;
		String output;

		m = getNumFilas();
		n = getNumColumnas();

		// se ubica en la primera fila
		nodoFila = getPrimerNodo();
		nodoP = nodoFila.getLd();

		// busca el primer nodo que no tenga valor nulo
		while (nodoFila != getNodoCabeza() && nodoP == nodoFila) {
			tripletaT = (Tripleta) nodoFila.getD();
			nodoFila = (NodoDoble) tripletaT.getValor();
			nodoP = nodoFila.getLd();
		}
		tripletaT = (Tripleta) nodoP.getD();
		f = tripletaT.getFila();
		c = tripletaT.getColumna();
		v = tripletaT.getValor();

		// espacios a añadir por cada linea para que quede organizado
		int spacesToAdd = (int) Math.floor(Math.log10(getNumFilas()));

		// se imprimen los indices de las columnas
		System.out.print(" ".concat("  0 "));
		for (int j = 1; j < n; j++) {
			if (j == n - 1) {
				output = "%s    ? - %d\n";
			} else {
				output = "%s ";
			}
			System.out.print(String.format(output, j, posiblesMinas));
		}

		for (int i = 0; i < m; i++) {

			// se añaden los espacios
			output = "%s ";
			for (int s = 0; s < spacesToAdd; s++) {
				output = " ".concat(output);
			}

			// se resta el numero de espacios a añadir si el número aumenta de dígitos
			if (i > 0 && Math.floor(Math.log10(i + 1)) > Math.floor(Math.log10(i))) {
				spacesToAdd--;
			}

			// índice de fila
			System.out.print(String.format(output, i));

			for (int j = 0; j < n; j++) {
				if (j == n - 1) {
					output = "%s\n";
				} else {
					output = "%s|";
				}

				// si el nodoP no es un nodo fila, y tampoco el nodo cabeza
				if (nodoFila != getNodoCabeza() && nodoP != nodoFila) {

					// si la fila y columna coinciden con el nodoP
					if (i == f && j == c) {
						casillaX = (Tripleta) tripletaT;

						// se imprime según sea mina o no, y según su estado
						if (casillaX.getValor().equals(-1)) {
							imprimirCasilla(i, j, output, "*");
						} else {
							imprimirCasilla(i, j, output, Integer.toString((int) v));
						}

						// se avanza al siguiente nodo
						nodoP = nodoP.getLd();

						// si no hay más nodos en la fila se avanza al siguiente nodo con valor
						// diferente de nulo
						while (nodoFila != getNodoCabeza() && nodoP == nodoFila) {
							tripletaT = (Tripleta) nodoFila.getD();
							nodoFila = (NodoDoble) tripletaT.getValor();
							nodoP = nodoFila.getLd();
						}

						// si se encontró un nodo antes de recorrer toda la matriz
						if (nodoP != null) {
							tripletaT = (Tripleta) nodoP.getD();
							f = tripletaT.getFila();
							c = tripletaT.getColumna();
							v = tripletaT.getValor();
						}
					} else { // si no se ha llegado al nodo con fila y columna (i,j)

						// se imprime nulo según su estado
						imprimirCasilla(i, j, output, " ");
					}
				} else { // no hay más nodos con valor diferente a nulo

					// se imprime nulo según su estado
					imprimirCasilla(i, j, output, " ");
				}
			}
		}
	}

	/**
	 * Imprime una casilla según su estado y valor.
	 * 
	 * @param i      Fila de la casilla.
	 * @param j      Columna de la casilla.
	 * @param output {@link String} a formatear con el string <b>v</b> si la
	 *               casilla esta descubierta.
	 * @param v      {@link String} a imprimir si la casilla esta descubierta.
	 */
	private void imprimirCasilla(int i, int j, String output, String v) {
		EstadoDeCasilla estadoC = getEstadoDeCasilla(i, j);
		switch (estadoC) {
		case posibleMina:
			System.out.print(String.format(output, "?"));
			break;

		case oculta:
			System.out.print(String.format(output, "O"));
			break;

		case descubierta:
			System.out.print(String.format(output, v));
			break;
		}
	}

	/**
	 * Para probar el juego en la consola.
	 * <p>
	 * Para despejar una casilla se usa la forma: <br>
	 * <i>xzy</i> <br>
	 * donde <b>x</b> y <b>y</b> son los índices de la casilla a depejar, y <b>z</b>
	 * es cualquier otro carácter exceptuando <i>'?'</i>
	 * <p>
	 * Para marcar o desmarcar una casilla como posible mina se usa la forma: <br>
	 * <i>xzyz?</i> <br>
	 * donde <b>x</b> y <b>y</b> son los índices de la casilla a marcar o desmarcar,
	 * y <b>z</b> es cualquier otro carácter exceptuando <i>'?'</i>. El signo de
	 * interrogación se usa para indicar que se quiere marcar o desmarcar una
	 * posible mina.
	 */
	public void loop() {
		Scanner sc = new Scanner(System.in);
		int fila, columna;
		String input;
		String inputSplit[];

		iniciar();
		System.out.println("----------Buscaminas----------");
		while (getEstado() == Estado.iniciado) {
			showAsArrayOfArrays();

			System.out.println();
			System.out.print("ingrese i, j:");
			input = sc.nextLine();
			inputSplit = input.split("[^0-9?]");

			try {
				fila = Integer.parseInt(inputSplit[0].trim());
				columna = Integer.parseInt(inputSplit[1].trim());

				if (inputSplit.length >= 3 && inputSplit[2].equals("?")) {
					posibleMina(fila, columna);
				} else {
					despejarCasilla(fila, columna);
				}
			} catch (IndexOutOfBoundsException e) {
				System.out.println();
				System.out.println("*Intentelo de nuevo*");
			}
			System.out.println();
			System.out.println();
		}
		sc.close();

		for (int i = 0; i < getNumFilas(); i++) {
			for (int j = 0; j < getNumColumnas(); j++) {
				setEstadoDeCasilla(i, j, EstadoDeCasilla.descubierta);
			}
		}
		showAsArrayOfArrays();

		if (getEstado() == Estado.ganado) {
			System.out.println("¡¡¡GANASTE!!!");
		} else {
			System.out.println("PERDISTE...");
		}
	}

//	public static void main(String[] args) {
//		CampoDeMinas cm = new CampoDeMinas(12, 10, 4);
//		cm.iniciar();
//		cm.loop();
//	}

}
