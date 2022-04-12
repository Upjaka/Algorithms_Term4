package Model;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

public class Model {
    private int width;
    private int height;
    private int[][] field = new int[0][0];
    List<List<Pair<Integer, Integer>>> solutions = new ArrayList<>();
    private boolean isSolved = false;

    /**
     * Нахождение всех решений
     */
    public void solve() {
        if (!isSolved) {
            List<Pair<Integer, Integer>> solution = new ArrayList<>();
            solution.add(new Pair<>(0, 0));
            findSolution(0, 0, solution);
            isSolved = true;
        }
    }

    /**
     * Рекурсивный метод поиска всех решений
     * i, j - начальная точка
     * solution - текущий путь из начальной точки в текущую
     */
    private void findSolution(final int i, final int j, List<Pair<Integer, Integer>> solution) {
        if (i == height - 1 && j == width - 1) {
            solution.add(new Pair<>(i, j));
            solutions.add(solution);
        }
        if (field[i][j] != 0 && i + field[i][j] < height) {
            List<Pair<Integer, Integer>> other = new ArrayList<>(solution);
            other.add(new Pair<>(i + field[i][j], j));
            findSolution(i + field[i][j], j, other);
        }
        if (field[i][j] != 0 && j + field[i][j] < width) {
            List<Pair<Integer, Integer>> other = new ArrayList<>(solution);
            other.add(new Pair<>(i, j + field[i][j]));
            findSolution(i, j + field[i][j], other);
        }
    }

    /**
     * Изменение размеров поля на случайные
     */
    public void changeSize() {
        width = (int) (random() * 7) + 2;
        height = (int) (random() * 7) + 2;
        updateField();
    }

    /**
     * Изменение размеров поля на заданные
     */
    public void changeSize(final int width, final int height) {
        this.width = width;
        this.height = height;
        updateField();
    }

    /**
     * Изменение поля в соответсвии с заданными width и height
     */
    private void updateField() {
        setNotSolved();
        int[][] newField = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (i < field.length && j < field[i].length) {
                    newField[i][j] = field[i][j];
                } else {
                    newField[i][j] = getRandomValue(i, j);
                }
            }
        }
        field = newField;
    }

    /**
     * Изменения статуса поля на нерешенное
     */
    private void setNotSolved() {
        isSolved = false;
        solutions.clear();
    }

    /**
     * Установка случайных значений в клетки поля
     */
    public void setRandomValues() {
        setNotSolved();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                field[i][j] = getRandomValue(i, j);
            }
        }
    }

    /**
     * Способ установки случайных значений.
     * В данной реализации находится максимальная расстояние из клетки до нижней или правой границы
     * и получается случайное число от 0 до до этого значения
     */
    private int getRandomValue(final int i, final int j) {
        return (int) (random() * (max(width - j, height - i)));
    }

    /**
     * Установка в заданной клетке заданного значения
     */
    public void set(final int i, final int j, final int value) {
        setNotSolved();
        field[i][j] = value;
    }

    /**
     * Геттер количества решений
     */
    public int getSolutionsSize() {
        return solutions.size();
    }

    /**
     * Геттер списка решений
     */
    public List<List<Pair<Integer, Integer>>> getSolutions() {
        return solutions;
    }

    /**
     * Геттер ширины поля
     */
    public int getWidth() {
        return width;
    }

    /**
     * Геттер высоты поля
     */
    public int getHeight() {
        return height;
    }

    /**
     * Геттер значения в клетке поля
     */
    public int get(final int i, final int j) {
        return field[i][j];
    }
}