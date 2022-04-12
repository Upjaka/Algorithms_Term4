package Controller;

import Model.Model;
import View.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
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

    /**
     * Обработчик нажатия кнопки выхода из приложения
     */
    public void exitButtonClicked() {
        Platform.exit();
    }

    /**
     * Обработчик нажатия кнопки "Решить"
     */
    public void startButtonClicked() throws IOException {
        if (isSizeCorrect()) {
            Pattern pattern = Pattern.compile("[^0-9]");
            for (TextField[] textFields1 : textFields) {
                for (TextField textField : textFields1) {
                    Matcher matcher = pattern.matcher(textField.getText());
                    if (matcher.find()) {
                        showAlert("Можно вводить только неотрицательные числа");
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
            solutionWindow.getIcons().add(new Image("game_controller_icon.png"));
            solution = solutionWindow;
            solutionWindow.show();
        }
    }

    /**
     * Обработчик нажатия кнопки возвращения с экрана с решений к экрану задания ввходных параметров
     */
    public void newGameButtonClicked() {
        solution.close();
    }

    /**
     * Возможность установить размер поля, если нажать Enter в одном из двух текстовых полей размера
     */
    public void sizeChanged(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            setSizeButtonClicked();
        }
    }

    /**
     * Обработчик нажатия кнопки установки заданного пользователем размера поля
     */
    public void setSizeButtonClicked() {
        if (isSizeCorrect()) {
            model.changeSize(Integer.parseInt(widthTextField.getText()), Integer.parseInt(heightTextField.getText()));
            changeField();
        }
    }

    /**
     * Обработчик нажатия кнопки случайного размера поля
     */
    public void randomSizeButtonClicked() {
        model.changeSize();
        widthTextField.setText(String.valueOf(model.getWidth()));
        heightTextField.setText(String.valueOf(model.getHeight()));
        changeField();
    }

    /**
     * Обработчик нажатия кнопки случайного содержимого поля
     */
    public void randomContentButtonClicked() {
        if (gridPane.getColumnCount() != 0 && gridPane.getRowCount() != 0) {
            model.setRandomValues();
            changeField();
        } else {
            showAlert("Сначала установите размеры поля");
        }
    }

    /**
     * Обработчик нажатия кнопки "Показать решения"
     */
    public void showButtonClicked() {
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

        controlButtons.getChildren().remove(showButton);

        if (model.getSolutionsSize() == 0) {
            solutionsNumberLabel.setText("Нет решений");
            nextButton.setVisible(false);
            previousButton.setVisible(false);
        } else {
            if (model.getSolutionsSize() != 1) {
                previousButton.setVisible(true);
                previousButton.getStyleClass().add("controlButton");
                nextButton.setVisible(true);
                nextButton.getStyleClass().add("controlButton");
            }
            labels[0][0].getStyleClass().add("borderCell");
            labels[model.getHeight() - 1][model.getWidth() - 1].getStyleClass().add("borderCell");
            updateField();
        }
    }

    /**
     * Обработчик нажатия кнопки показа следующего решения
     */
    public void nextButtonClicked() {
        solutionNumber = solutionNumber == model.getSolutionsSize() ? 1 : solutionNumber + 1;
        solutionsNumberLabel.setText(solutionNumber + " из " + model.getSolutionsSize());
        updateField();
    }

    /**
     * Обработчик нажатия кнопки показа предыдущего решения
     */
    public void previousButtonClicked() {
        solutionNumber = solutionNumber == 1 ? model.getSolutionsSize() : solutionNumber - 1;
        solutionsNumberLabel.setText(solutionNumber + " из " + model.getSolutionsSize());
        updateField();
    }

    /**
     * Изменяет отображамое поле в соответсвии с состоянием модели
     */
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
        gridPane.getChildren().clear();

        for (int i = 0; i < model.getHeight(); i++) {
            for (int j = 0; j < model.getWidth(); j++) {
                textFields[i][j].setText(String.valueOf(model.get(i, j)));
                gridPane.add(textFields[i][j], i, j);
            }
        }

        while (gridPane.getColumnConstraints().size() < model.getWidth()) {
            gridPane.getColumnConstraints().add(new ColumnConstraints());
        }
        while (gridPane.getColumnConstraints().size() > model.getWidth()) {
            gridPane.getColumnConstraints().remove(0);
        }
        while (gridPane.getRowConstraints().size() < model.getHeight()) {
            gridPane.getRowConstraints().add(new RowConstraints());
        }
        while (gridPane.getRowConstraints().size() > model.getHeight()) {
            gridPane.getRowConstraints().remove(0);
        }
    }

    /**
     * Метод проверки корректности введеных размеров игрового поля
     */
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

    /**
     * Метод для ототбражения выбранного решения
     */
    private void updateField() {
        for (int i = 0; i < model.getHeight(); i++) {
            for (int j = 0; j < model.getWidth(); j++) {
                labels[i][j].getStyleClass().remove("solutionCell");
            }
        }
        for (Pair<Integer, Integer> pair : model.getSolutions().get(solutionNumber - 1)) {
            labels[pair.getKey()][pair.getValue()].getStyleClass().add("solutionCell");
        }
    }

    /**
     * Создание всплывающего окна для информационных сообщений
     */
    private void showAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.NONE, text, ButtonType.OK);
        alert.setTitle("Внимание");
        alert.showAndWait();
    }
}