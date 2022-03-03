package se.uu.ub.cora.diva.tocorautils.convert;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.diva.tocorautils.NotImplementedException;

public class FromDbToCoraFunderConverterTest {
	private Map<String, Object> rowFromDb = new HashMap<>();
	private FromDbToCoraFunderConverter funderConverter;

	@BeforeMethod
	public void beforeMethod() {
		rowFromDb.put("funder_id", 123);
		rowFromDb.put("funder_name", "RAA");
		rowFromDb.put("funder_name_locale", "sv");
		funderConverter = new FromDbToCoraFunderConverter();
	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "Convert to Json not implemented for funder")
	public void testConvertFunderToJsonNotImplemented() {
		funderConverter.convertToJsonFromRowFromDb(rowFromDb);
	}

	@Test
	public void testConvertFunderMinimal() {
		ClientDataGroup funder = funderConverter.convertToClientDataGroupFromRowFromDb(rowFromDb);
		assertEquals(funder.getNameInData(), "funder");
		assertCorrectRecordInfo(funder);
		ClientDataGroup nameGroup = funder.getFirstGroupWithNameInData("name");
		assertEquals(nameGroup.getFirstAtomicValueWithNameInData("funderName"), "RAA");
		assertEquals(nameGroup.getFirstAtomicValueWithNameInData("language"), "sv");

		assertFalse(funder.containsChildWithNameInData("funderAcronym"));
		assertFalse(funder.containsChildWithNameInData("funderRegistrationNumber"));
		assertFalse(funder.containsChildWithNameInData("funderDOI"));
		assertFalse(funder.containsChildWithNameInData("2010-01-01"));
	}

	private void assertCorrectRecordInfo(ClientDataGroup funder) {
		ClientDataGroup recordInfo = funder.getFirstGroupWithNameInData("recordInfo");
		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), "funder:123");
		ClientDataGroup dataDivider = recordInfo.getFirstGroupWithNameInData("dataDivider");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordType"), "system");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordId"), "diva");
	}

	@Test
	public void testConvertFunderWithAlternativeName() {
		rowFromDb.put("alternative_name", "A different name for RAA");
		rowFromDb.put("alternative_name_locale", "en");
		ClientDataGroup funder = funderConverter.convertToClientDataGroupFromRowFromDb(rowFromDb);
		ClientDataGroup alternativeName = funder.getFirstGroupWithNameInData("alternativeName");
		assertEquals(alternativeName.getFirstAtomicValueWithNameInData("funderName"),
				"A different name for RAA");
		assertEquals(alternativeName.getFirstAtomicValueWithNameInData("language"), "en");
	}

	@Test
	public void testConvertFunderWithOtherNonMandatoryVariables() {
		rowFromDb.put("acronym", "Also RAA");
		rowFromDb.put("orgnumber", "34567");
		rowFromDb.put("doi", "213.456546.574757");
		rowFromDb.put("closed_date", "2022-01-01");
		ClientDataGroup funder = funderConverter.convertToClientDataGroupFromRowFromDb(rowFromDb);
		assertEquals(funder.getFirstAtomicValueWithNameInData("funderAcronym"), "Also RAA");
		assertEquals(funder.getFirstAtomicValueWithNameInData("funderRegistrationNumber"), "34567");
		assertEquals(funder.getFirstAtomicValueWithNameInData("funderDOI"), "213.456546.574757");
		assertEquals(funder.getFirstAtomicValueWithNameInData("funderClosed"), "2022-01-01");
	}
	// select f.funder_id, f.closed_date, f.funder_name, f.funder_name_locale, f.acronym,
	// f.orgnumber, f.doi, fn.funder_name as alternative_name, fn.locale as alternative_name_locale
	// from funder f left join funder_name fn ON f.funder_id = fn.funder_id;

}
