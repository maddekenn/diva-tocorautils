package se.uu.ub.cora.diva.tocorautils.convert;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

public class SubjectCategoryFromDbToCoraConverterFactoryTest {

	@Test
	public void testInit() {
		FromDbToCoraConverterFactory converterFactory = new SubjectCategoryFromDbToCoraConverterFactory();
		FromDbToCoraConverter converter = converterFactory.createConverter();
		assertTrue(converter instanceof FromDbToCoraSubjectCategoryConverter);

	}
}
