package matricesDispersas;

/**
 * Clase representativa de la estructura de una Tripleta.
 * 
 * @author sneyd
 *
 */
public class Tripleta {

	private int fila;
	private int columna;
	private Object valor;

	/**
	 * Contructor. Inicia la clase 'Tripleta' estableciendo el valor de los campos
	 * privados por los valores que se entregan en los parámetros.
	 * 
	 * @param f Valor del campo privado 'fila'.
	 * @param c Valor del campo privado 'columna'.
	 * @param v Valor del campo privado 'valor'.
	 */
	public Tripleta(int f, int c, Object v) {
		this.fila = f;
		this.columna = c;
		this.valor = v;
	}

	/**
	 * Getter del campo {@link #fila}.
	 * 
	 * @return {@link #fila}.
	 */
	public int getFila() {
		return fila;
	}

	/**
	 * Setter del campo {@link #fila}.
	 * 
	 * @param fila Valor a asignar al campo {@link #fila}.
	 */
	public void setFila(int fila) {
		this.fila = fila;
	}

	/**
	 * Getter del campo {@link #columna}.
	 * 
	 * @return {@link #columna}.
	 */
	public int getColumna() {
		return columna;
	}

	/**
	 * Setter del campo {@link #columna}.
	 * 
	 * @param columna Valor a asignar al campo {@link #columna}.
	 */
	public void setColumna(int columna) {
		this.columna = columna;
	}

	/**
	 * Getter del campo {@link #valor}.
	 * 
	 * @return {@link #valor}.
	 */
	public Object getValor() {
		return valor;
	}

	/**
	 * Setter del campo {@link #valor}.
	 * 
	 * @param valor Valor a asignar al campo {@link #valor}.
	 */
	public void setValor(Object valor) {
		this.valor = valor;
	}

	/**
	 * Crea una copia de la {@link Tripleta}.
	 * Si el {@link #valor} es un objeto de tipo inmutable, se devuelve una copia de
	 * este, de lo contrario se devuelve su referencia.
	 * 
	 * @return Una copia de esta {@link Tripleta}.
	 */
	public Tripleta copiar() {
		return new Tripleta(getFila(), getColumna(), getValor());
	}

}