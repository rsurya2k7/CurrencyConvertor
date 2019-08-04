package com.currency.converter;

import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.currency.util.CSVReader;

public class CurrencyConvertor {
	public static int dataIndex = 0;
	public static int matrixRow = 0, matrixCol = 0;
	static String fromCountry, toCountry;
	static double transferAmount;
	static String[] data;
	static String[][] matrix;
	static String input;

	public static int getDataIndex() {
		return dataIndex;
	}

	public static void setDataIndex(int dataIndex) {
		CurrencyConvertor.dataIndex = dataIndex;
	}

	public static int getMatrixRow() {
		return matrixRow;
	}

	public static void setMatrixRow(int matrixRow) {
		CurrencyConvertor.matrixRow = matrixRow;
	}

	public static int getMatrixCol() {
		return matrixCol;
	}

	public static void setMatrixCol(int matrixCol) {
		CurrencyConvertor.matrixCol = matrixCol;
	}

	public static String getFromCountry() {
		return fromCountry;
	}

	public static void setFromCountry(String fromCountry) {
		CurrencyConvertor.fromCountry = fromCountry;
	}

	public static String getToCountry() {
		return toCountry;
	}

	public static void setToCountry(String toCountry) {
		CurrencyConvertor.toCountry = toCountry;
	}

	public static double getTransferAmount() {
		return transferAmount;
	}

	public static void setTransferAmount(double transferAmount) {
		CurrencyConvertor.transferAmount = transferAmount;
	}

	public static String[] getData() {
		return data;
	}

	public static void setData(String[] data) {
		CurrencyConvertor.data = data;
	}

	public static String[][] getMatrix() {
		return matrix;
	}

	public static void setMatrix(String[][] matrix) {
		CurrencyConvertor.matrix = matrix;
	}

	public static void main(String args[]) {
		Scanner scan = new Scanner(System.in);
		try {
			loadCSVinMatrixData(scan);
		} catch (IOException e) {
			e.printStackTrace();
		}

		takeUserInput(scan);
		findRateOrIntermediateCountry(matrix);

	}

	private static void loadCSVinMatrixData(Scanner scan) throws IOException {
		String[] data;
		CSVReader readCSV = new CSVReader();
		data = readCSV.readCSV();
		matrix = new String[matrixRow][matrixCol];

		int index = 0;
		for (int i = 0; i < matrixRow; i++) {
			for (int j = 0; j < matrixCol; j++) {
				matrix[i][j] = data[index++].toString();
			}
		}
	}

	private static void findRateOrIntermediateCountry(String[][] matrix) {
		int fromRowIndex = findIndex(matrix, fromCountry, true);
		int toColIndex = findIndex(matrix, toCountry, false);
		Double amount = 1.0;
		String transferAmt = "";
		if (Pattern.compile("[0-9]").matcher(matrix[fromRowIndex][toColIndex]).find()) {
			// System.out.println("Direct Transfer is Possible");
			amount = calculate(matrix[fromRowIndex][toColIndex], transferAmount);
			if (toCountry.equals("JPY")) {
				transferAmt = Integer.toString(amount.intValue());
			} else {
				transferAmt = String.format("%1.2f", amount);
			}
			System.out.println(fromCountry + " " + transferAmount + " = " + toCountry + " " + transferAmt);

		} else {
			Double finalAmountAfterInterTransfer = 0.0;
			String interimCntry1 = "";
			interimCntry1 = searchTransitFromMatrix(matrix, fromCountry, toCountry);
			// first country
			fromRowIndex = findIndex(matrix, fromCountry, true);
			int internContryIndex = findIndex(matrix, interimCntry1, false);
			// amount = calculate(matrix[fromRowIndex][internContryIndex], transferAmount);

			if (!Pattern.compile("[0-9]").matcher(matrix[fromRowIndex][internContryIndex]).find()) {
				String interCountry2 = matrix[fromRowIndex][internContryIndex];
				fromRowIndex = findIndex(matrix, interimCntry1, true);
				internContryIndex = findIndex(matrix, interCountry2, false);
				amount = calculate(matrix[fromRowIndex][internContryIndex], transferAmount);
				// finalAmountAfterInterTransfer =
				// calculate(matrix[internContryIndex][toColIndex], amount);
			} else {
				amount = calculate(matrix[fromRowIndex][internContryIndex], transferAmount);

			}

			if (!Pattern.compile("[0-9]").matcher(matrix[internContryIndex][toColIndex]).find()) {
				String interCountry3 = matrix[internContryIndex][toColIndex];
				fromRowIndex = findIndex(matrix, interimCntry1, true);
				internContryIndex = findIndex(matrix, interCountry3, false);
				amount = calculate(matrix[fromRowIndex][internContryIndex], amount);
				finalAmountAfterInterTransfer = calculate(matrix[internContryIndex][toColIndex], amount);
			} else {
				finalAmountAfterInterTransfer = calculate(matrix[internContryIndex][toColIndex], amount);
			}
			if (toCountry.equalsIgnoreCase("JPY")) {
				transferAmt = Integer.toString(finalAmountAfterInterTransfer.intValue());
			} else {
				transferAmt = String.format("%1.2f", finalAmountAfterInterTransfer);
			}
			System.out.println(fromCountry + " " + transferAmount + " = " + toCountry + " " + transferAmt);
		}
	}

	private static void takeUserInput(Scanner scan) {
		try {
			System.out.println("Enter input in the following format -> <cc1><amount1> in <cc2>");
			scan = new Scanner(System.in);
			input = scan.nextLine();
			String[] inputs = input.split(" ");

			fromCountry = inputs[0];
			transferAmount = Double.parseDouble(inputs[1]);
			toCountry = inputs[3];
			scan.close();
		} catch (NumberFormatException e) {
			System.out.println("Enter proper amount!");
		}
	}

	public static double calculate(String matrix, double transferAmount2) {
		return (transferAmount2 * Double.parseDouble(matrix));
	}

	static int findIndex(String[][] matrix, String dataForIndex, boolean rowSearch) {
		// System.out.println("matrix ::" + matrix[1][0]);
		try {
			if (rowSearch) {
				for (int i = 0; i < matrixRow; i++) {
					if (matrix[i][0].contains(dataForIndex.trim())) {
						// System.out.println("in row search: " + i);
						return i;
					}
				}
			} else {
				for (int i = 0; i < matrixRow; i++) {
					if (matrix[0][i].contains(dataForIndex.trim())) {
						// System.out.println("in col search: " + i);
						return i;
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Enter a valid country!");
			System.exit(0);
		}
		return 0;
	}

	static String searchTransitFromMatrix(String[][] matrix, String fromContry, String toCountry) {
		for (int i = 0; i < matrixRow; i++) {
			if (matrix[i][0].contains(fromContry.trim())) {
				for (int j = 0; j < matrixCol; j++) {
					if (matrix[0][j].contains(toCountry.trim())) {
						// System.out.print("Transfer will be done via : " + matrix[i][j] + "\t");
						return matrix[i][j];
					}
				}
			}
		}
		return null;
	}
}
