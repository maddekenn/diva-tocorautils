/*
 * Copyright 2019 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.diva.tocorautils.convert;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.diva.tocorautils.NotImplementedException;
import se.uu.ub.cora.diva.tocorautils.doubles.CoraClientSpy;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;

public class FromDbToCoraSubjectCategoryConverterTest {
	se.uu.ub.cora.diva.tocorautils.doubles.CoraClientSpy coraClient;
	private JsonBuilderFactory jsonFactory;
	private FromDbToCoraSubjectCategoryConverter subjectCategoryConverter;
	private DataToJsonConverterFactorySpy dataToJsonConverterFactory;

	private Map<String, Object> rowFromDb = new HashMap<>();

	@BeforeMethod
	public void beforeMethod() {
		rowFromDb.put("subject_id", 406);
		rowFromDb.put("default_name", "Some subject category");
		rowFromDb.put("subject_code", "110022");
		rowFromDb.put("alternative_name", "Some alternative name");

		coraClient = new CoraClientSpy();
		jsonFactory = new OrgJsonBuilderFactoryAdapter();
		dataToJsonConverterFactory = new DataToJsonConverterFactorySpy();
		subjectCategoryConverter = FromDbToCoraSubjectCategoryConverter
				.usingJsonFactoryAndConverterFactory(jsonFactory, dataToJsonConverterFactory);

	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "Convert to Json not implemented for subject category")
	public void testConvertSubjectCategoryToJsonNotImplemented() {
		subjectCategoryConverter.convertToJsonFromRowFromDb(rowFromDb);
	}

	@Test
	public void testConvertSubjectCategoryOneRow() {
		ClientDataGroup subjectCategory = subjectCategoryConverter
				.convertToClientDataGroupFromRowFromDb(rowFromDb);

		assertEquals(subjectCategory.getNameInData(), "nationalSubjectCategory");

		assertCorrectRecordInfo(subjectCategory);

		assertCorrectName(subjectCategory, "name", "Some subject category", "sv");
		assertCorrectName(subjectCategory, "alternativeName", "Some alternative name", "en");
		assertEquals(subjectCategory.getFirstAtomicValueWithNameInData("subjectCode"), "110022");
		assertFalse(subjectCategory.containsChildWithNameInData("nationalSubjectCategoryParent"));
	}

	private void assertCorrectRecordInfo(ClientDataGroup subjectCategory) {
		ClientDataGroup recordInfo = subjectCategory.getFirstGroupWithNameInData("recordInfo");
		assertFalse(recordInfo.containsChildWithNameInData("id"));
		ClientDataGroup dataDivider = recordInfo.getFirstGroupWithNameInData("dataDivider");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordType"), "system");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordId"), "diva");
	}

	private void assertCorrectName(ClientDataGroup subjectCategory, String nameInData, String name,
			String locale) {
		ClientDataGroup nameGroup = subjectCategory.getFirstGroupWithNameInData(nameInData);
		assertEquals(nameGroup.getFirstAtomicValueWithNameInData("nationalSubjectCategoryName"),
				name);
		assertEquals(nameGroup.getFirstAtomicValueWithNameInData("language"), locale);
	}
}
