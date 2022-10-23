/**
 * Copyright 2022 josebarrueta
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.math.jb;

import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.lang.reflect.Array;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class Matrix<T extends Number> {

    private final T[][] innerArray;

    private final Class<T> matrixType;

    /**
     * Creates a matrix n x m with all elements initialized to 0.
     *
     * @param rows    the number of rows in this Matrix
     * @param columns the number of columns in this matrix
     */
    public Matrix(Class<T> tClass, int rows, int columns) {
        this(tClass, rows, columns, () -> tClass.cast(0));
    }

    public Matrix(Class<T> tClass, int rows, int columns, Supplier<T> initializer) {
        Assert.isTrue(rows > 0, "rows in matrix cannot be less than 1.");
        Assert.isTrue(columns > 0, "columns in matrix cannot be less than 1.");

        int[] size = {rows, columns};

        //noinspection unchecked
        this.innerArray = (T[][]) Array.newInstance(tClass, size);
        this.matrixType = tClass;
        initialize(initializer);
    }

    /**
     * Initializes this matrix
     *
     * @param matrix - T
     */
    public Matrix(T[][] matrix) {
        Assert.isTrue(matrix.length > 0, "rows in matrix cannot be less than 1.");
        Assert.isTrue(matrix[0].length > 0, "columns in matrix cannot be less than 1.");
        this.innerArray = matrix;

        //noinspection unchecked
        this.matrixType = (Class<T>) matrix.getClass().getComponentType().getComponentType();
    }

    public int getRowSize() {
        return this.innerArray.length;
    }

    public int getColumnSize() {
        return this.innerArray[0].length;
    }

    /**
     * Sets the value passed as argument to the Mij element.
     *
     * @param ithRow    the ith row of the matrix
     * @param jthColumn the jth column of the matrix
     * @param value     the value to assign
     */
    public void setValue(int ithRow, int jthColumn, T value) {
        assertElementInMatrixDimensions(ithRow, jthColumn);
        innerArray[ithRow - 1][jthColumn - 1] = value;
    }


    public T getValue(int ithRow, int jthColumn) {
        assertElementInMatrixDimensions(ithRow, jthColumn);
        return innerArray[ithRow - 1][jthColumn - 1];
    }

    /**
     * Initializes the matrix by generating values with the supplier
     */
    public void initialize(Supplier<T> supplier) {
        for (int i = 1; i <= innerArray.length; i++) {
            for (int j = 1; j <= innerArray[0].length; j++) {
                setValue(i, j, supplier.get());
            }
        }
    }

    /**
     * Multiplies {@code this} matrix of size m x n
     *
     * @param b - Matrix B of size  n x p
     * @return Matrix C of size m x p
     * @throws IllegalArgumentException if the {@code this} matrix column size is not equal to the b matrix row size.
     */
    public Matrix<T> multiply(@NonNull Matrix<T> b) {
        if (this.getColumnSize() != b.getRowSize()) {
            throw new IllegalArgumentException("Unable to multiply this matrix with the matrix passed as argument due to size mismatch");
        }

        int n = this.getColumnSize();
        int m = this.getRowSize();
        int p = b.getColumnSize();

        Matrix<T> result = new Matrix<>(matrixType, m, p);

        for (int i = 1; i <= result.getRowSize(); i++) {
            for (int j = 1; j <= result.getColumnSize(); j++) {
                for (int k = 1; k <= n; k++) {
                    T cij = result.getValue(i, j);

                    T aValue = this.getValue(i, k);
                    T bValue = b.getValue(k, j);

                    T kValue = multiplyValue(aValue, bValue);
                    result.setValue(i, j, addValue(cij, kValue));
                }
            }
        }
        return result;
    }

    /**
     * Add {@code this} matrix of size m x n to another matrix of the same dimensions
     *
     * @param other - Matrix B of size  m x n
     * @return Matrix C of size m x n
     * @throws IllegalArgumentException if the matrices don't have the same dimensions
     */
    public Matrix<T> add(Matrix<T> other) {
        assertEqualDimensions(other);
        Matrix<T> result = new Matrix<>(this.matrixType, this.getRowSize(), this.getColumnSize());

        for (int i = 1; i <= result.getRowSize(); i++) {
            for (int j = 1; j <= result.getColumnSize(); j++) {
                result.setValue(i, j, addValue(this.getValue(i, j), other.getValue(i, j)));
            }
        }

        return result;
    }

    /**
     * Add {@code this} matrix of size m x n to another matrix of the same dimensions
     *
     * @param other - Matrix B of size  m x n
     * @return Matrix C of size m x n
     * @throws IllegalArgumentException if the matrices don't have the same dimensions
     */
    public Matrix<T> subtract(Matrix<T> other) {
        assertEqualDimensions(other);

        Matrix<T> result = new Matrix<>(this.matrixType, this.getRowSize(), this.getColumnSize());

        for (int i = 1; i <= result.getRowSize(); i++) {
            for (int j = 1; j <= result.getColumnSize(); j++) {
                result.setValue(i, j, subtractValue(this.getValue(i, j), other.getValue(i, j)));
            }
        }
        return result;
    }


    /**
     * Returns the transpose of {@code this} matrix
     *
     * @return a matrix that is the transpose of {@code this} matrix.
     */
    public Matrix<T> transpose() {
        Matrix<T> transpose = new Matrix<>(this.matrixType, this.getColumnSize(), this.getRowSize());
        for (int i = 1; i <= this.getRowSize(); i++) {
            for (int j = 1; j <= this.getColumnSize(); j++) {
                transpose.setValue(j, i, this.getValue(i, j));
            }
        }
        return transpose;
    }

    public boolean isSymmetric() {
        return this.equals(this.transpose());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRowSize(), getColumnSize());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Matrix<?> other)) {
            return false;
        }

        if (this.getRowSize() != other.getRowSize() || this.getColumnSize() != other.getColumnSize()) {
            return false;
        }

        for (int i = 1; i <= this.getRowSize(); i++) {
            for (int j = 1; j <= this.getColumnSize(); j++) {
                if (!Objects.equals(this.getValue(i, j), other.getValue(i, j))) {
                    return false;
                }
            }
        }
        return true;
    }

    //    public void print() {
//        for (T[] row : innerArray) {
//            for (int j = 0; j < innerArray[0].length; j++) {
//                if (j == (innerArray[0].length - 1)) {
//                    System.out.print(row[j] + "\n");
//                } else {
//                    System.out.print(row[j] + "\t");
//                }
//            }
//            System.out.println();
//        }
//    }

    private T multiplyValue(T a, T b) {
        if (a instanceof Integer) {
            return matrixType.cast(a.intValue() * b.intValue());
        }
        if (a instanceof Long) {
            return matrixType.cast(a.longValue() * a.longValue());
        }
        if (a instanceof Double) {
            return matrixType.cast(a.doubleValue() * a.doubleValue());
        }
        if (a instanceof Float) {
            return matrixType.cast(a.floatValue() * a.floatValue());
        }
        throw new UnsupportedOperationException("Unable to multiply values of type: " + matrixType);
    }

    private T addValue(T a, T b) {
        if (a instanceof Integer) {
            return matrixType.cast(a.intValue() + b.intValue());
        }
        if (a instanceof Long) {
            return matrixType.cast(a.longValue() + a.longValue());
        }
        if (a instanceof Double) {
            return matrixType.cast(a.doubleValue() + a.doubleValue());
        }
        if (a instanceof Float) {
            return matrixType.cast(a.floatValue() + a.floatValue());
        }
        throw new UnsupportedOperationException("Unable to add values of type: " + matrixType);
    }

    private T subtractValue(T a, T b) {
        if (a instanceof Integer) {
            return matrixType.cast(a.intValue() - b.intValue());
        }
        if (a instanceof Long) {
            return matrixType.cast(a.longValue() - a.longValue());
        }
        if (a instanceof Double) {
            return matrixType.cast(a.doubleValue() - a.doubleValue());
        }
        if (a instanceof Float) {
            return matrixType.cast(a.floatValue() - a.floatValue());
        }
        throw new UnsupportedOperationException("Unable to substract values of type: " + matrixType);
    }

    private void assertElementInMatrixDimensions(int ithRow, int jthColumn) {
        if (ithRow < 1 || ithRow > this.getRowSize()) {
            throw new ArrayIndexOutOfBoundsException(String.format("ithRow=%d is out of the boundaries of the matrix row size=%d", ithRow, this.getRowSize()));
        }
        if (jthColumn < 1 || jthColumn > this.getColumnSize()) {
            throw new ArrayIndexOutOfBoundsException(String.format("jthColumn=%d is out of the boundaries of the matrix column size=%d", jthColumn, this.getColumnSize()));
        }
    }

    private void assertEqualDimensions(Matrix<T> other) {
        if (this.getRowSize() != other.getRowSize()) {
            throw new IllegalArgumentException("");
        }

        if (this.getColumnSize() != other.getColumnSize()) {
            throw new IllegalArgumentException("");
        }
    }

}
