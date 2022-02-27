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

    public void solve() {
        if (!isSolved) {
            List<Pair<Integer, Integer>> solution = new ArrayList<>();
            solution.add(new Pair<>(0, 0));
            findSolution(0, 0, solution);
            isSolved = true;
        }
    }

    private void findSolution(int i, int j, List<Pair<Integer, Integer>> solution) {
        if (i == height - 1 && j == width - 1)  {
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

    public void changeSize() {
        width = (int) (random() * 7) + 2;
        height = (int) (random() * 7) + 2;
        updateField();
    }

    public void changeSize(int width, int height) {
        this.width = width;
        this.height = height;
        updateField();
    }

    private void updateField() {
        for (List<Pair<Integer, Integer>> solution : solutions) {
            solutions.remove(solution);
        }
        int[][] newField = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (i < field.length && j < field[i].length)  {
                    newField[i][j] = field[i][j];
                } else {
                    newField[i][j] = getRandomValue(i, j);
                }
            }
        }
        field = newField;
    }

    public void setRandomValues() {
        for (List<Pair<Integer, Integer>> solution : solutions) {
            solutions.remove(solution);
        }
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                field[i][j] = getRandomValue(i, j);
            }
        }
    }

    private int getRandomValue(int i, int j) {
        return (int) (random() * (max(width - j, height - i)));
    }

    public void set(int i, int j, int value) {
        if (i >= 0 && i < height && j >= 0 && j < width) field[i][j] = value;
    }

    public int getSolutionsSize() {
        return solutions.size();
    }

    public List<List<Pair<Integer, Integer>>> getSolutions() {
        return solutions;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int get(int i, int j) {
        return field[i][j];
    }
}