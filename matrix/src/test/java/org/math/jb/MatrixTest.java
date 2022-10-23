package org.math.jb;

import org.junit.jupiter.api.Test;

public class MatrixTest {

    @Test
    public void testTransposeOperation() {
        Integer[][] values = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {10, 11, 12}};

        Matrix<Integer> matrix = new Matrix<>(values);
        Matrix<Integer> result = matrix.transpose();

        Integer[][] expected = {{1, 4, 7, 10}, {2, 5, 8, 11}, {3, 6, 9, 12}};
        assertArray(new Matrix<>(expected), result);
    }

    @Test
    public void testAddOperation() {
        Integer[][] mOneValues = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {10, 11, 12}};

        Integer[][] mTwoValues = {{11, 12, 23}, {14, 15, 16}, {17, 18, 19}, {20, 21, 22}};

        Matrix<Integer> matrixOne = new Matrix<>(mOneValues);
        Matrix<Integer> matrixTwo = new Matrix<>(mTwoValues);

        Integer[][] expected = {{12, 14, 26}, {18, 20, 22}, {24, 26, 28}, {30, 32, 34}};
        assertArray(new Matrix<>(expected), matrixOne.add(matrixTwo));
    }

    @Test
    public void testSubtractOperation() {
        Integer[][] mOneValues = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {10, 11, 12}};

        Integer[][] mTwoValues = {{11, 12, 23}, {2, 1, 3}, {3, 7, 2}, {5, 7, 9}};

        Matrix<Integer> matrixOne = new Matrix<>(mOneValues);
        Matrix<Integer> matrixTwo = new Matrix<>(mTwoValues);

        Integer[][] expected = {{-10, -10, -20}, {2, 4, 3}, {4, 1, 7}, {5, 4, 3}};
        assertArray(new Matrix<>(expected), matrixOne.subtract(matrixTwo));
    }

    @Test
    public void testMultiplyOperation() {
        Integer[][] mOneValues = {{1, 0, 1}, {2, 1, 1}, {0, 1, 1}, {1, 1, 2}};

        Integer[][] mTwoValues = {{1, 2, 1}, {2, 3, 1}, {4, 2, 2}};

        Matrix<Integer> matrixOne = new Matrix<>(mOneValues);
        Matrix<Integer> matrixTwo = new Matrix<>(mTwoValues);

        Integer[][] expected = {{5, 4, 3}, {8, 9, 5}, {6, 5, 3}, {11, 9, 6}};

        assertArray(new Matrix<>(expected), matrixOne.multiply(matrixTwo));
    }

    private void assertArray(Matrix<Integer> expected, Matrix<Integer> actual) {
        if (expected.getRowSize() != actual.getRowSize()) {
            throw new AssertionError(String.format("Invalid number of rows expected=[%d], actual=[%d]", expected.getRowSize(), actual.getRowSize()));
        }
        if (expected.getColumnSize() != actual.getColumnSize()) {
            throw new AssertionError(String.format("Invalid number of columns expected=[%d], actual=[%d]", expected.getColumnSize(), actual.getColumnSize()));
        }

        for (int i = 1; i <= expected.getRowSize(); i++) {
            for (int j = 1; j <= expected.getColumnSize(); j++) {
                if (!expected.getValue(i, j).equals(actual.getValue(i, j))) {
                    throw new AssertionError(String.format("Invalid value at position (%d, %d), expected=%d, actual=%d", i, j,
                            expected.getValue(i, j), actual.getValue(i, j)));
                }
            }
        }
    }
}
