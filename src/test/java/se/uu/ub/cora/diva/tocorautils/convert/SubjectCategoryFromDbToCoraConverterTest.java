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
import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.diva.tocorautils.doubles.CoraClientSpy;
import se.uu.ub.cora.diva.tocorautils.doubles.RecordReaderFactorySpy;
import se.uu.ub.cora.diva.tocorautils.doubles.RecordReaderSpy;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;
import se.uu.ub.cora.tocorautils.CoraJsonRecord;

public class SubjectCategoryFromDbToCoraConverterTest {
	CoraClientSpy coraClient;
	private JsonBuilderFactory jsonFactory;
	private SubjectCategoryFromDbToCoraConverter subjectCategoryFromDbToCoraConverter;
	private DataToJsonConverterFactorySpy dataToJsonConverterFactory;
	private RecordReaderFactorySpy recordReaderFactory;

	private Map<String, String> rowFromDb = new HashMap<>();

	@BeforeMethod
	public void beforeMethod() {
		rowFromDb.put("subject_id", "406");
		rowFromDb.put("default_name", "Some subject category");
		rowFromDb.put("subject_code", "someSubjectCode");
		rowFromDb.put("alternative_name", "Some alternative name");

		coraClient = new CoraClientSpy();
		jsonFactory = new OrgJsonBuilderFactoryAdapter();
		dataToJsonConverterFactory = new DataToJsonConverterFactorySpy();
		recordReaderFactory = new RecordReaderFactorySpy();
		subjectCategoryFromDbToCoraConverter = SubjectCategoryFromDbToCoraConverter
				.usingJsonFactoryConverterFactoryAndReaderFactory(jsonFactory,
						dataToJsonConverterFactory, recordReaderFactory);

	}

	@Test
	public void testConvertSubjectCategoryOneRowMinimalRequiredValuesNoParent() {
		CoraJsonRecord coraJsonRecord = subjectCategoryFromDbToCoraConverter
				.convertToJsonFromRowFromDb(rowFromDb);

		assertEquals(coraJsonRecord.recordType, "nationalSubjectCategory");

		ClientDataGroup groupSentToConverter = (ClientDataGroup) dataToJsonConverterFactory.dataElements
				.get(0);
		assertCorrectRecordInfo(groupSentToConverter, "406");
		assertCorrectGroupSentToConverterUsingGroupNameAlternativeNameAndCode(groupSentToConverter,
				"Some subject category", "Some alternative name", "someSubjectCode");

		assertEquals(dataToJsonConverterFactory.calledNumOfTimes, 1);
		DataToJsonConverterSpy dataToJsonConverterSpy = dataToJsonConverterFactory.dataToJsonConverterSpies
				.get(0);
		assertTrue(dataToJsonConverterSpy.toJsonObjectBuilderWasCalled);

		assertEquals(coraJsonRecord.json,
				dataToJsonConverterSpy.jsonObjectBuilder.toJsonFormattedString());

		RecordReaderSpy factored = recordReaderFactory.factored;
		Map<String, String> usedConditions = factored.usedConditions;
		assertEquals(usedConditions.size(), 1);
		assertEquals(usedConditions.get("subject_id"), rowFromDb.get("subject_id"));

		assertFalse(
				groupSentToConverter.containsChildWithNameInData("nationalSubjectCategoryParent"));

	}

	private void assertCorrectGroupSentToConverterUsingGroupNameAlternativeNameAndCode(
			ClientDataGroup groupSentToConverter, String name, String alternativeName,
			String expected) {
		assertEquals(groupSentToConverter.getNameInData(), "nationalSubjectCategory");
		assertEquals(groupSentToConverter
				.getFirstAtomicValueWithNameInData("nationalSubjectCategoryName"), name);
		assertEquals(groupSentToConverter.getFirstAtomicValueWithNameInData(
				"nationalSubjectCategoryAlternativeName"), alternativeName);
		assertEquals(groupSentToConverter.getFirstAtomicValueWithNameInData("subjectCode"),
				expected);
	}

	private void assertCorrectRecordInfo(ClientDataGroup groupSentToConverter, String expectedId) {
		ClientDataGroup recordInfo = groupSentToConverter.getFirstGroupWithNameInData("recordInfo");
		String id = recordInfo.getFirstAtomicValueWithNameInData("id");
		assertEquals(id, expectedId);

		ClientDataGroup dataDivider = recordInfo.getFirstGroupWithNameInData("dataDivider");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordType"), "system");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordId"), "diva");
	}

	@Test
	public void testConvertSubjectCategoryOneRowMinimalRequiredValuesWithParent() {
		recordReaderFactory.idsToReturnParent.add("406");
		CoraJsonRecord coraJsonRecord = subjectCategoryFromDbToCoraConverter
				.convertToJsonFromRowFromDb(rowFromDb);

		assertEquals(coraJsonRecord.recordType, "nationalSubjectCategory");

		ClientDataGroup groupSentToConverter = (ClientDataGroup) dataToJsonConverterFactory.dataElements
				.get(0);
		assertCorrectRecordInfo(groupSentToConverter, "406");
		assertCorrectGroupSentToConverterUsingGroupNameAlternativeNameAndCode(groupSentToConverter,
				"Some subject category", "Some alternative name", "someSubjectCode");

		assertEquals(dataToJsonConverterFactory.calledNumOfTimes, 1);
		DataToJsonConverterSpy dataToJsonConverterSpy = dataToJsonConverterFactory.dataToJsonConverterSpies
				.get(0);
		assertTrue(dataToJsonConverterSpy.toJsonObjectBuilderWasCalled);

		assertEquals(coraJsonRecord.json,
				dataToJsonConverterSpy.jsonObjectBuilder.toJsonFormattedString());

		RecordReaderSpy factored = recordReaderFactory.factored;
		Map<String, String> usedConditions = factored.usedConditions;
		assertEquals(usedConditions.size(), 1);
		assertEquals(usedConditions.get("subject_id"), rowFromDb.get("subject_id"));

		ClientDataGroup parentGroup = groupSentToConverter
				.getFirstGroupWithNameInData("nationalSubjectCategoryParent");
		ClientDataGroup parentLink = parentGroup
				.getFirstGroupWithNameInData("nationalSubjectCategory");
		assertEquals(parentLink.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"nationalSubjectCategory");
		assertEquals(parentLink.getFirstAtomicValueWithNameInData("linkedRecordId"), "someParent0");
		assertEquals(parentGroup.getRepeatId(), "0");

	}

}
