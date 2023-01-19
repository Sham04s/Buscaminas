package app;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class WindowController implements Initializable {

	private DrawingField drawingField;
	private Timeline timer;
	private int mins, secs, bestMins, bestSecs;

	@FXML
	private TextField txtMinas;

	@FXML
	private Label lblBestTime;

	@FXML
	private VBox vbBestTime;

	@FXML
	private VBox vbGameControl;

	@FXML
	private AnchorPane sidePane;

	@FXML
	private VBox vbTime;

	@FXML
	private Label lblTime;

	@FXML
	private AnchorPane root;

	@FXML
	private HBox hbGameProperties;

	@FXML
	private VBox vbTxtProperties;

	@FXML
	private TextField txtFilas;

	@FXML
	private Button btnNewGame;

	@FXML
	private TextField txtColumnas;

	@FXML
	private ComboBox<String> cbBxDifficulty;

	@FXML
	private Button btnReset;

	@FXML
	private Button btnExit;

	@FXML
	private AnchorPane drawingPane;

	@FXML
	private Label lblMines;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		drawingField = new DrawingField();
		drawingField.widthProperty().bind(drawingPane.widthProperty().subtract(2));
		drawingField.heightProperty().bind(drawingPane.heightProperty().subtract(2));
		drawingPane.getChildren().add(drawingField);

		cbBxDifficulty.getItems().addAll("Principiante", "Intermedio", "Experto", "Personalizado");
		cbBxDifficulty.valueProperty().addListener(listener -> difficultyChanged());
		cbBxDifficulty.setValue("Intermedio");

		timer = new Timeline(new KeyFrame(Duration.seconds(1), crono -> crono()));
		timer.setCycleCount(Animation.INDEFINITE);
	}

	private void crono() {
		if (secs + 1 == 60) {
			secs = 0;
			mins++;
		} else {
			secs++;
		}
		String time = String.format("%d%d:%d%d", mins / 10, mins % 10, secs / 10, secs % 10);
		lblTime.setText(time);
	}

	private void difficultyChanged() {
		int m, n, minas;

		txtFilas.setDisable(true);
		txtColumnas.setDisable(true);
		txtMinas.setDisable(true);

		switch (cbBxDifficulty.getValue()) {
		case "Principiante":
			m = 8;
			n = 8;
			minas = 10;
			break;

		case "Intermedio":
			m = 16;
			n = 16;
			minas = 40;
			break;

		case "Experto":
			m = 16;
			n = 30;
			minas = 99;
			break;

		default:
			txtFilas.setDisable(false);
			txtColumnas.setDisable(false);
			txtMinas.setDisable(false);
			return;
		}
		txtFilas.setText(Integer.toString(m));
		txtColumnas.setText(Integer.toString(n));
		txtMinas.setText(Integer.toString(minas));
	}

	@FXML
	void mouseHandler(MouseEvent evt) {
		if (drawingField.getEstado() == null) {
			return;
		}

		switch (drawingField.getEstado()) {
		case noIniciado:
			if (evt.getEventType() == MouseEvent.MOUSE_CLICKED) {
				drawingField.iniciar();
				secs = 0;
				mins = 0;
				timer.play();
			}
			break;

		case iniciado:
			break;

		case ganado:
			timer.stop();
			if ((bestMins == 0 && bestSecs == 0) || (mins * 60 + secs < bestMins * 60 + bestSecs)) {
				bestMins = mins;
				bestSecs = secs;
				String bestTime = String.format("%d%d:%d%d", bestMins / 10, bestMins % 10, bestSecs / 10,
						bestSecs % 10);
				lblBestTime.setText(bestTime);
			}
			break;

		case perdido:
			timer.stop();
			break;
		}
		drawingField.handle(evt);

		if (evt.getEventType() == MouseEvent.MOUSE_CLICKED) {
			lblMines.setText(Integer.toString(drawingField.getMinasFaltantes()));
		}
	}

	@FXML
	void newGame(ActionEvent event) {
		int m, n, minas;

		Dialog<String> error = new Dialog<String>();
		error.setTitle("Error");
		error.getDialogPane().getButtonTypes().add(ButtonType.OK);
		error.getDialogPane().setMinWidth(600);
		error.setResultConverter(ButtonType::getText);

		try {
			m = Integer.parseInt(txtFilas.getText().trim());
			n = Integer.parseInt(txtColumnas.getText().trim());
			minas = Integer.parseInt(txtMinas.getText().trim());
			if (m < 1 || n < 1 || minas < 0) {
				error.setContentText("El tamaño del campo de minas debe ser mayor a 0");
				error.showAndWait();
				return;
			}
			if (minas >= m * n) {
				error.setContentText("El número de minas debe ser menor al tama�o del campo de minas");
				error.showAndWait();
				return;
			}
			if (m > 50 || n > 50) {
				error.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
				error.setTitle("¡Advertencia!");
				error.setContentText("Se recomienda que el número de filas y el número de columnas sea menor a 50,"
						+ " valores mayores pueden causar problemas de visualización o de procesamiento. "
						+ "\n ¿Desea continuar con estos valores?");
				error.showAndWait();;
				if(error.getResult() == ButtonType.CANCEL.getText()) {
					return;
				}
			}
		} catch (NumberFormatException e) {
			error.setContentText("Ingrese solo números enteros para iniciar un nuevo juego");
			error.showAndWait();
			return;
		}

		drawingField.nuevoJuego(m, n, minas);
		lblMines.setText(Integer.toString(drawingField.getMinasFaltantes()));
	}

	@FXML
	void reset(ActionEvent event) {
		timer.stop();
		drawingField.reiniciar();
		lblMines.setText(Integer.toString(drawingField.getMinasFaltantes()));
	}

	@FXML
	void close(ActionEvent event) {
		Platform.exit();
	}

}
