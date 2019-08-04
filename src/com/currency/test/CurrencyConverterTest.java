package com.currency.test;

import static org.junit.Assert.*;
import org.junit.Test;
import com.currency.converter.CurrencyConvertor;

public class CurrencyConverterTest {

	CurrencyConvertor convertor = new CurrencyConvertor();

	@Test
	public void testCalculate() {
		String matrix = "1.456";
		double transferAmount = 400.00;

		double result = CurrencyConvertor.calculate(matrix, transferAmount);
		assertEquals(582.40, result, 0.0);
	}
}
