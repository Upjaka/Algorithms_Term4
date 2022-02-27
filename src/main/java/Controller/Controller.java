package Controller;

import Model.Model;
import View.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {
    private static final int maxSize = 8;
    private static final Model model = new Model();
    private static final TextField[][] textFields = new TextField[maxSize][maxSize];
    private static Label[][] labels;
    private static Stage solution;
    private int solutionNumber;

    @FXML
    private HBox controlButtons;
    @FXML
    private Button showButton;
    @FXML
    private Button previousButton;
    @FXML
    private Button nextButton;
    @FXML
    private Label solutionsNumberLabel;
    @FXML
    private GridPane solutionGrid;
    @FXML
    private GridPane gridPane;
    @FXML
    private TextField widthTextField;
    @FXML
    private TextField heightTextField;

    public void exitButtonClicked() {
        Platform.exit();
    }

    public void startButtonClicked() throws IOException {
        if (isSizeCorrect()) {
            Pattern pattern = Pattern.compile("[^0-9]");
            for (TextField[] textFields1 : textFields) {
                for (TextField textField : textFields1) {
                    Matcher matcher = pattern.matcher(textField.getText());
                    if (matcher.find()) {
                        showAlert("Можно вводить только числа");
                        return;
                    }
                }
            }
            for (int i = 0; i < model.getHeight(); i++) {
                for (int j = 0; j < model.getWidth(); j++) {
                    model.set(i, j, Integer.parseInt(textFields[i][j].getText()));
                }
            }
            Parent root = FXMLLoader.load(getClass().getResource("/SolutionScreen.fxml"));
            Scene solutionScene = new Scene(root, 450, 700);
            Stage solutionWindow = new Stage();
            solutionWindow.setTitle("Решение");
            solutionWindow.setScene(solutionScene);
            solutionWindow.initOwner(Main.getPrimary());
            solutionWindow.initModality(Modality.WINDOW_MODAL);
            solution = solutionWindow;
            solutionWindow.show();
        }
    }

    public void newGameButtonClicked() {
        solution.close();
    }

    public void sizeChanged(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            if (isSizeCorrect()) changeField();
        }
    }

    public void setSizeButtonClicked() {
        if (isSizeCorrect()) changeField();
    }

    public void randomSizeButtonClicked() {
        model.changeSize();
        widthTextField.setText(String.valueOf(model.getWidth()));
        heightTextField.setText(String.valueOf(model.getHeight()));
        changeField();
    }

    public void randomContentButtonClicked() {
        if (gridPane.getColumnCount() != 0 && gridPane.getRowCount() != 0) {
            model.setRandomValues();
            changeField();
        } else {
            showAlert("Сначала установите размеры поля");
        }
    }

    public void showButtonClicked() {
        showSolution();
        if (model.getSolutionsSize() == 0) noSolutions();
    }

    public void nextButtonClicked() {
        solutionNumber = solutionNumber == model.getSolutionsSize() ? 1 : solutionNumber + 1;
        solutionsNumberLabel.setText(solutionNumber + " из " + model.getSolutionsSize());
        updateField();
    }

    public void previousButtonClicked() {
        solutionNumber = solutionNumber == 1 ? model.getSolutionsSize() : solutionNumber - 1;
        solutionsNumberLabel.setText(solutionNumber + " из " + model.getSolutionsSize());
        updateField();
    }

    private void changeField() {
        if (gridPane.getColumnCount() == 0 && gridPane.getRowCount() == 0) {
            for (int i = 0; i < maxSize; i++) {
                for (int j = 0; j < maxSize; j++) {
                    TextField textField = new TextField();
                    textField.getStyleClass().add("textField");
                    textFields[i][j] = textField;
                }
            }
        }
        gridPane.getChildren().removeAll(gridPane.getChildren());

        int width = Integer.parseInt(widthTextField.getText());
        int height = Integer.parseInt(heightTextField.getText());
        model.changeSize(width, height);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                textFields[i][j].setText(String.valueOf(model.get(i, j)));
                gridPane.add(textFields[i][j], i, j);
            }
        }

        while (gridPane.getColumnConstraints().size() < width) {
            gridPane.getColumnConstraints().add(new ColumnConstraints());
        }
        while (gridPane.getColumnConstraints().size() > width) {
            gridPane.getColumnConstraints().remove(0);
        }
        while (gridPane.getRowConstraints().size() < height) {
            gridPane.getRowConstraints().add(new RowConstraints());
        }
        while (gridPane.getRowConstraints().size() > height) {
            gridPane.getRowConstraints().remove(0);
        }
    }

    private boolean isSizeCorrect() {
        if (widthTextField.getText().isEmpty() || heightTextField.getText().isEmpty()) {
            showAlert("Сначала установите размеры поля");
            return false;
        }
        if (widthTextField.getText().equals("0") || heightTextField.getText().equals("0")) {
            showAlert("Размер может быть числом от 1 до 8");
            return false;
        }
        Pattern pattern = Pattern.compile("[^1-8]");
        Matcher matcher = pattern.matcher(widthTextField.getText());
        Matcher matcher1 = pattern.matcher(heightTextField.getText());
        if (matcher.find() || matcher1.find()) {
            showAlert("Размер может быть числом от 1 до 8");
            return false;
        }
        return true;
    }

    private void showSolution() {
        model.solve();
        labels = new Label[model.getHeight()][model.getWidth()];

        for (int i = 0; i < model.getHeight(); i++) {
            for (int j = 0; j < model.getWidth(); j++) {
                Label label = new Label(textFields[i][j].getText());
                label.getStyleClass().add("cell");
                labels[i][j] = label;
                solutionGrid.add(label, i, j);
            }
        }

        while (solutionGrid.getColumnCount() < model.getWidth()) {
            solutionGrid.getColumnConstraints().add(new ColumnConstraints());
        }
        while (solutionGrid.getRowCount() < model.getHeight()) {
            solutionGrid.getRowConstraints().add(new RowConstraints());
        }

        solutionsNumberLabel.setText("1 из " + model.getSolutionsSize());
        solutionNumber = 1;

        previousButton.setVisible(true);
        previousButton.getStyleClass().add("controlButton");
        nextButton.setVisible(true);
        nextButton.getStyleClass().add("controlButton");
        controlButtons.getChildren().remove(showButton);

        updateField();
    }

    private void noSolutions() {
        solutionsNumberLabel.setText("Нет решений");
        nextButton.setVisible(false);
        previousButton.setVisible(false);
    }

    private void updateField() {
        for (int i = 0; i < model.getHeight(); i++) {
            for (int j = 0; j < model.getWidth(); j++) {
                labels[i][j].getStyleClass().remove("solutionCell");
            }
        }
        labels[0][0].getStyleClass().add("borderCell");
        labels[model.getHeight() - 1][model.getWidth() - 1].getStyleClass().add("borderCell");
        for (Pair<Integer, Integer> pair : model.getSolutions().get(solutionNumber - 1)) {
            labels[pair.getKey()][pair.getValue()].getStyleClass().add("solutionCell");
        }
    }

    private void showAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.NONE, text, ButtonType.OK);
        alert.setTitle("Внимание");
        alert.showAndWait();
    }
}