package com.currency.converter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import com.currency.util.CSVReader;

public class CurrencyConvertor {
	public static int dataIndex = 0;
	public static int matrixRow = 0, matrixCol = 0;
	static String fromCountry, toCountry;
	static double transferAmount;
	static String[] data;
	static String[][] matrix;
	static String input;
	static HashMap<Integer, String> hmap;

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
		hmap = new HashMap<Integer, String>();
		try {
			loadCSVinMatrixData(scan);
		} catch (IOException e) {
			e.printStackTrace();
		}
		takeUserInput(scan);
		findRateOrIntermediateCountry(matrix);
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

	private static void loadCSVinMatrixData(Scanner scan) throws IOException {
		String[] data;
		CSVReader readCSV = new CSVReader();
		data = readCSV.readCSV();
		matrix = new String[matrixRow][matrixCol];

		int index = 0;
		for (int i = 0; i < matrixRow; i++) {
			// add countries in hmap for getting index
			CurrencyConvertor.hmap.put(i, data[i]);
			for (int j = 0; j < matrixCol; j++) {
				matrix[i][j] = data[index++].toString();
			}
		}
	}

	private static void findRateOrIntermediateCountry(String[][] matrix) {
		int fromRowIndex = findIndex(fromCountry);
		int toColIndex = findIndex(toCountry);
		Double amount = 1.0;
		String transferAmt = "";
		if (Pattern.compile("[0-9]").matcher(matrix[fromRowIndex][toColIndex]).find()) {
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
			interimCntry1 = matrix[findIndex(fromCountry)][findIndex(toCountry)];

			fromRowIndex = findIndex(fromCountry);
			int internContryIndex = findIndex(interimCntry1);
			if (!Pattern.compile("[0-9]").matcher(matrix[fromRowIndex][internContryIndex]).find()) {
				String interCountry2 = matrix[fromRowIndex][internContryIndex];
				fromRowIndex = findIndex(interimCntry1);
				internContryIndex = findIndex(interCountry2);
				amount = calculate(matrix[fromRowIndex][internContryIndex], transferAmount);
			} else {
				amount = calculate(matrix[fromRowIndex][internContryIndex], transferAmount);
			}

			if (!Pattern.compile("[0-9]").matcher(matrix[internContryIndex][toColIndex]).find()) {
				String interCountry3 = matrix[internContryIndex][toColIndex];
				fromRowIndex = findIndex(interimCntry1);
				internContryIndex = findIndex(interCountry3);
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

	private static int findIndex(String country) {
		try {
			Set<Integer> index = getKeysByValue(hmap, country);
			return index.iterator().next();
		} catch (Exception e) {
			System.out.println("Enter valid currency");
			System.exit(1);
		}
		return 0;
	}

	private static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
		return map.entrySet().stream().filter(entry -> Objects.equals(entry.getValue(), value)).map(Map.Entry::getKey)
				.collect(Collectors.toSet());
	}

	public static double calculate(String matrix, double transferAmount2) {
		return (transferAmount2 * Double.parseDouble(matrix));
	}

}
