package se.uu.ub.cora.diva.tocorautils.importing;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.diva.tocorautils.FromDbToCoraConverterFactorySpy;
import se.uu.ub.cora.diva.tocorautils.doubles.FromDbToCoraConverterSpy;

public class FunderTransformerFromFileTest {

	private FromDbToCoraConverterFactorySpy converterFactory;
	private String pathToFile = "src/test/resources/funder.json";
	private FunderTransformerFromFile transformer;

	@BeforeMethod
	public void setUp() {
		converterFactory = new FromDbToCoraConverterFactorySpy();
		transformer = new FunderTransformerFromFile(pathToFile, converterFactory);
	}

	@Test
	public void testInit() {
		assertSame(transformer.getConverterFactory(), converterFactory);
	}

	@Test
	public void testConvert() {
		transformer.getConverted();
	}

	@Test
	public void testRowsAreConverted() {
		transformer.getConverted();
		assertEquals(converterFactory.type, "funder");
		List<FromDbToCoraConverterSpy> returnedConverterSpies = converterFactory.returnedConverterSpies;
		assertEquals(returnedConverterSpies.size(), 20);

		Map<String, Object> row = returnedConverterSpies.get(0).row;
		assertEquals(row.get("funder_id"), 3);

		// assertSame(returnedConverterSpies.get(0).row, rowsToReturn.get(0));
	}
}
