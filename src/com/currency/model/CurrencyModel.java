package com.currency.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import com.currency.util.CSVReader;

public class CurrencyModel {
	public static int dataIndex = 0;
	public static HashMap<Integer, String> hmap = new HashMap<Integer, String>();
	public static int matrixRow = 0, matrixCol = 0;
	public static String[][] matrix;

	public HashMap<Integer, String> getHmap() {
		return hmap;
	}

	public void setHmap(HashMap<Integer, String> hmap) {
		CurrencyModel.hmap = hmap;
	}

	public int getMatrixRow() {
		return matrixRow;
	}

	public void setMatrixRow(int matrixRow) {
		CurrencyModel.matrixRow = matrixRow;
	}

	public int getMatrixCol() {
		return matrixCol;
	}

	public void setMatrixCol(int matrixCol) {
		CurrencyModel.matrixCol = matrixCol;
	}

	public static String[][] getMatrix() {
		return matrix;
	}

	public static void setMatrix(String[][] matrix) {
		CurrencyModel.matrix = matrix;
	}

	public int getDataIndex() {
		return dataIndex;
	}

	public void setDataIndex(int dataIndex) {
		CurrencyModel.dataIndex = dataIndex;
	}

	public String[][] loadCSVinMatrixData(Scanner scan) throws IOException {
		String[] data;
		CSVReader readCSV = new CSVReader();
		data = readCSV.readCSV();
		matrix = new String[matrixRow][matrixCol];

		int index = 0;
		for (int i = 0; i < matrixRow; i++) {
			// add countries in hmap for getting index
			getHmap().put(i, data[i]);
			for (int j = 0; j < matrixCol; j++) {
				matrix[i][j] = data[index++].toString();
			}
		}
		return matrix;
	}

}
