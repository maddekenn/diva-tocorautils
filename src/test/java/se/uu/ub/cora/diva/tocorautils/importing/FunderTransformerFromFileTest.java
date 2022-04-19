package se.uu.ub.cora.diva.tocorautils.importing;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;

import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.diva.tocorautils.FromDbToCoraConverterFactorySpy;
import se.uu.ub.cora.diva.tocorautils.convert.ConverterException;
import se.uu.ub.cora.diva.tocorautils.doubles.FromDbToCoraConverterSpy;

public class FunderTransformerFromFileTest {

	private FromDbToCoraConverterFactorySpy converterFactory;
	private String pathToFile = "src/test/resources/funder.json";
	private FunderTransformerFromFile transformer;
	private String type = "funder";

	@BeforeMethod
	public void setUp() {
		converterFactory = new FromDbToCoraConverterFactorySpy();
		transformer = FunderTransformerFromFile.usingFilePathConverterFactoryAndType(pathToFile,
				converterFactory, type);
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
		assertEquals(row.get("funder_name"), "Formas");
		assertFalse(row.containsKey("orgnumber"));

		Map<String, Object> row2 = returnedConverterSpies.get(7).row;
		assertEquals(row2.get("funder_id"), 80);
		assertEquals(row2.get("funder_name"), "The Middle East in the Contemporary World");
		assertFalse(row2.containsKey("closed_date"));

		Map<String, Object> row3 = returnedConverterSpies.get(18).row;
		assertEquals(row3.get("funder_id"), 1);
		assertEquals(row3.get("funder_name"), "EU, Europeiska forskningsrådet");
		assertEquals(row3.get("closed_date"), "2021-09-01");
		assertEquals(row3.get("funder_name_locale"), "sv");
		assertEquals(row3.get("acronym"), "EUF");
		assertEquals(row3.get("orgnumber"), "EU2020");
		assertEquals(row3.get("doi"), "101.901.09");
		assertEquals(row3.get("alternative_name"), "EU, European Research Council");
		assertEquals(row3.get("alternative_name_locale"), "en");
	}

	@Test
	public void testConvertedGroupsAreReturned() {
		List<ClientDataGroup> convertedFunders = transformer.getConverted();
		List<FromDbToCoraConverterSpy> returnedConverterSpies = converterFactory.returnedConverterSpies;

		assertSame(convertedFunders.get(0), returnedConverterSpies.get(0).dataGroupToReturn);
		assertSame(convertedFunders.get(6), returnedConverterSpies.get(6).dataGroupToReturn);
		assertSame(convertedFunders.get(19), returnedConverterSpies.get(19).dataGroupToReturn);
	}

	@Test(expectedExceptions = ConverterException.class, expectedExceptionsMessageRegExp = ""
			+ "Unable to parse json string using path: someInvalidPath")
	public void testErrorReadingFile() {
		transformer = FunderTransformerFromFile.usingFilePathConverterFactoryAndType(
				"someInvalidPath", converterFactory, "funder");
		transformer.getConverted();
	}

	@Test
	public void testTypeIsUsed() {
		transformer = FunderTransformerFromFile.usingFilePathConverterFactoryAndType(pathToFile,
				converterFactory, "otherType");
		transformer.getConverted();
		assertEquals(converterFactory.type, "otherType");
	}
}
