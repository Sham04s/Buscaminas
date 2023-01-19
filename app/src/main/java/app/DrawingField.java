package app;

import game.CampoDeMinas;
import game.CampoDeMinas.Estado;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class DrawingField extends Canvas {

	private CampoDeMinas campoDeMinas;
	private GraphicsContext gc;
	private double baseWidth, baseHeight, cellWidth, cellHeight, scale;
	private int casillaSelected[];

	public DrawingField() {
		super();

		gc = getGraphicsContext2D();

		widthProperty().addListener(evt -> sizeChanged());
		heightProperty().addListener(evt -> sizeChanged());
	}

	private void draw() {
		if (campoDeMinas != null) {
			gc.clearRect(0, 0, getWidth(), getHeight());
			gc.setFont(Font.font("Consolas", FontWeight.BOLD, cellWidth * scale));
			gc.setTextAlign(TextAlignment.CENTER);
			gc.setTextBaseline(VPos.CENTER);
			gc.setGlobalAlpha(1);

			int m, n;
			double x, y, w, h, xText, yText;
			m = campoDeMinas.getNumFilas();
			n = campoDeMinas.getNumColumnas();

			w = cellWidth * scale;
			h = w;

			// ciclo para dibujar cada celda
			// TODO cambiar por el recorrido de nodos
			for (int i = 0; i < m; i++) {
				for (int j = 0; j < n; j++) {

					x = j * w + 1;
					y = i * h + 1;
					xText = x + w / 2;
					yText = y + h / 2;

					gc.setStroke(Color.BLACK);

					switch (campoDeMinas.getEstadoDeCasilla(i, j)) {

						case descubierta:
							gc.strokeRect(x, y, w - 2, h - 2);

							int casilla = (int) campoDeMinas.get(i, j);

							if (casilla == -1) {
								yText += 1 * h / 5;
								gc.setFill(Color.RED);
								gc.fillText("*", xText, yText, w - 2);
							} else if (casilla != 0) {
								gc.setFill(Color.AQUA);
								gc.fillText(String.format("%d", casilla), xText, yText, w - 2);
							}
							break;

						case posibleMina:
							gc.setFill(Color.DARKGRAY);
							gc.fillRect(x, y, w - 2, h - 2);
							gc.setFill(Color.RED);
							gc.fillText("?", xText, yText, w);
							break;

						default:
							gc.setFill(Color.DARKGRAY);
							gc.fillRect(x, y, w - 2, h - 2);
							break;
					}

				}
			}

			if (casillaSelected != null) {
				gc.setFont(Font.font("Consolas", FontWeight.BOLD, cellWidth * scale * 2));
				switch (getEstado()) {
					case perdido:
						x = casillaSelected[1] * w + 1;
						y = casillaSelected[0] * h + 1;
						gc.setFill(Color.LIGHTCORAL);
						gc.setGlobalAlpha(0.4);
						gc.fillRoundRect(x, y, w - 2, h - 2, 40, 40);

						xText = (n * w) / 2;
						yText = (m * h) / 2;
						gc.setGlobalAlpha(1);
						gc.fillText("Perdiste...", xText, yText, n * w);
						break;

					case ganado:
						xText = (n * w) / 2;
						yText = (m * h) / 2;
						gc.setGlobalAlpha(1);
						gc.setFill(Color.LIMEGREEN);
						gc.fillText("Ganaste!!!", xText, yText, n * w);
						break;

					default:
						x = casillaSelected[1] * w + 1;
						y = casillaSelected[0] * h + 1;
						gc.setFill(Color.MEDIUMAQUAMARINE);
						gc.setGlobalAlpha(0.4);
						gc.fillRoundRect(x, y, w - 2, h - 2, 40, 40);
						break;
				}
			}
		}
	}

	public void nuevoJuego(int m, int n, int minas) {
		campoDeMinas = new CampoDeMinas(m, n, minas);

		double min = Math.min(getWidth(), getHeight());
		double max = Math.max(m, n);
		cellWidth = min / max;
		cellHeight = cellWidth;

		this.baseWidth = cellWidth * n;
		this.baseHeight = cellHeight * m;

		if (getWidth() / getHeight() > baseWidth / baseHeight) {
			scale = getHeight() / baseHeight;
		} else {
			scale = getWidth() / baseWidth;
		}

		draw();
	}

	public void handle(MouseEvent evt) {
		if (campoDeMinas == null) {
			return;
		}

		double x, y, w, h;
		int casillaFil, casillaCol, m, n;

		m = campoDeMinas.getNumFilas();
		n = campoDeMinas.getNumColumnas();
		w = cellWidth * scale;
		h = w;

		x = evt.getX();
		y = evt.getY();

		if (getEstado() == Estado.perdido || getEstado() == Estado.ganado) {
			return;
		}

		if (x > w * n - 2 || y > h * m - 2 || evt.getEventType() == MouseEvent.MOUSE_EXITED) {
			casillaSelected = null;
			draw();
			return;
		}

		casillaFil = (int) (y / h);
		casillaCol = (int) (x / w);

		if (evt.getEventType() == MouseEvent.MOUSE_MOVED) {
			casillaSelected = new int[2];
			casillaSelected[0] = casillaFil;
			casillaSelected[1] = casillaCol;
		}
		if (evt.getEventType() == MouseEvent.MOUSE_CLICKED) {
			if (evt.getButton() == MouseButton.PRIMARY) {
				campoDeMinas.tocarCasilla(casillaFil, casillaCol);
			} else if (evt.getButton() == MouseButton.SECONDARY) {
				campoDeMinas.posibleMina(casillaFil, casillaCol);
			}
		}

		draw();
	}

	public void sizeChanged() {
		if (baseWidth != 0 && baseHeight != 0) {
			if (getWidth() / getHeight() > baseWidth / baseHeight) {
				scale = getHeight() / baseHeight;
			} else {
				scale = getWidth() / baseWidth;
			}
		} else {
			scale = 1;
		}
		draw();
	}

	public void iniciar() {
		if (campoDeMinas != null) {
			campoDeMinas.iniciar();
		}
	}

	public void reiniciar() {
		if (campoDeMinas != null) {
			campoDeMinas.reiniciar();
		}
		draw();
	}

	public Estado getEstado() {
		if (campoDeMinas != null) {
			return campoDeMinas.getEstado();
		}
		return null;
	}

	public int getMinasFaltantes() {
		if (campoDeMinas != null) {
			return campoDeMinas.getNumMinas() - campoDeMinas.getPosiblesMinas();
		}
		return 0;
	}

	@Override
	public boolean isResizable() {
		return true;
	}

	@Override
	public double prefWidth(double height) {
		return 1D;
	}

	@Override
	public double prefHeight(double width) {
		return 1D;
	}

}
