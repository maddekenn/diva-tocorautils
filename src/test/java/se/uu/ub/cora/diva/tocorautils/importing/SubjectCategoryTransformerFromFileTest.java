package se.uu.ub.cora.diva.tocorautils.importing;

import static org.testng.Assert.assertSame;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.diva.tocorautils.FromDbToCoraConverterFactorySpy;

public class SubjectCategoryTransformerFromFileTest {

	private FromDbToCoraConverterFactorySpy converterFactory;
	private String pathToFile = "src/test/resources/subjectCategory.json";
	private SubjectCategoryTransformerFromFile transformer;

	@BeforeMethod
	public void setUp() {
		converterFactory = new FromDbToCoraConverterFactorySpy();
		transformer = SubjectCategoryTransformerFromFile
				.usingFilenameAndConverterFactory(pathToFile, converterFactory);
	}

	@Test
	public void testInit() {
		assertSame(transformer.getConverterFactory(), converterFactory);
	}

}
