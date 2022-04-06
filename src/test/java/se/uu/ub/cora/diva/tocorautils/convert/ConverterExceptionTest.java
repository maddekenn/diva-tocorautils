package se.uu.ub.cora.diva.tocorautils.convert;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

public class ConverterExceptionTest {

	@Test
	public void testException() {
		ConverterException converterException = new ConverterException("someMessage");
		assertTrue(converterException instanceof RuntimeException);
	}

	@Test
	public void testMessage() {
		ConverterException converterException = new ConverterException("someMessage");
		assertEquals(converterException.getMessage(), "someMessage");
	}
}
