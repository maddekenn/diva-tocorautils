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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.diva.tocorautils.doubles.CoraClientSpy;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;
import se.uu.ub.cora.tocorautils.CoraJsonRecord;

public class SubjectCategoryListFromDbToCoraConverterTest {
	List<Map<String, String>> rowsFromDb = new ArrayList<Map<String, String>>();
	CoraClientSpy coraClient;
	private JsonBuilderFactory jsonFactory;
	private SubjectCategoryListFromDbToCoraConverter subjectCategoryListFromDbToCoraConverter;
	private DataToJsonConverterFactorySpy dataToJsonConverterFactory;

	@BeforeMethod
	public void beforeMethod() {
		rowsFromDb = new ArrayList<Map<String, String>>();
		Map<String, String> rowFromDb = new HashMap<>();
		rowFromDb.put("id", "406");
		rowFromDb.put("default_name", "Some subject category");
		rowFromDb.put("subject_code", "someSubjectCode");
		rowFromDb.put("alternative_name", "Some alternative name");

		rowsFromDb.add(rowFromDb);

		coraClient = new CoraClientSpy();
		jsonFactory = new OrgJsonBuilderFactoryAdapter();
		dataToJsonConverterFactory = new DataToJsonConverterFactorySpy();
		subjectCategoryListFromDbToCoraConverter = SubjectCategoryListFromDbToCoraConverter
				.usingJsonFactoryAndConverterFactory(jsonFactory, dataToJsonConverterFactory);

	}

	@Test
	public void testConvertSubjectCategoryOneRowMinimalRequiredValues() {
		List<List<CoraJsonRecord>> convertedRows = subjectCategoryListFromDbToCoraConverter
				.convertToJsonFromRowsFromDb(rowsFromDb);

		assertEquals(convertedRows.size(), 1);
		List<CoraJsonRecord> row = convertedRows.get(0);
		CoraJsonRecord coraJsonRecord = row.get(0);

		assertEquals(coraJsonRecord.recordType, "nationalSubjectCategory");

		ClientDataGroup groupSentToConverter = (ClientDataGroup) dataToJsonConverterFactory.dataElements
				.get(0);
		assertCorrectRecordInfo(groupSentToConverter, "406");
		assertCorrectGroupSentToConverterUsingGroupNameAlternativeNameAndCode(groupSentToConverter,
				"Some subject category", "Some alternative name", "someSubjectCode");
		assertFalse(
				groupSentToConverter.containsChildWithNameInData("nationalSubjectCategoryParent"));

		assertEquals(dataToJsonConverterFactory.calledNumOfTimes, 1);
		DataToJsonConverterSpy dataToJsonConverterSpy = dataToJsonConverterFactory.dataToJsonConverterSpies
				.get(0);
		assertTrue(dataToJsonConverterSpy.toJsonObjectBuilderWasCalled);

		assertEquals(coraJsonRecord.json,
				dataToJsonConverterSpy.jsonObjectBuilder.toJsonFormattedString());

	}

	@Test
	public void testConvertSubjectCategoryTwoRowsMinimalRequiredValues() {
		addSecondRow();

		List<List<CoraJsonRecord>> convertedRows = subjectCategoryListFromDbToCoraConverter
				.convertToJsonFromRowsFromDb(rowsFromDb);

		assertEquals(convertedRows.size(), 2);

		ClientDataGroup groupSentToConverter = (ClientDataGroup) dataToJsonConverterFactory.dataElements
				.get(0);
		assertCorrectRecordInfo(groupSentToConverter, "406");
		assertCorrectGroupSentToConverterUsingGroupNameAlternativeNameAndCode(groupSentToConverter,
				"Some subject category", "Some alternative name", "someSubjectCode");

		ClientDataGroup groupSentToConverter2 = (ClientDataGroup) dataToJsonConverterFactory.dataElements
				.get(1);
		assertCorrectRecordInfo(groupSentToConverter2, "400");
		assertCorrectGroupSentToConverterUsingGroupNameAlternativeNameAndCode(groupSentToConverter2,
				"Some other subject category", "Some other alternative name",
				"someOtherSubjectCode");

		assertEquals(dataToJsonConverterFactory.calledNumOfTimes, 2);

		List<CoraJsonRecord> firstRow = convertedRows.get(0);
		CoraJsonRecord coraJsonRecord = firstRow.get(0);
		DataToJsonConverterSpy dataToJsonConverterSpy = dataToJsonConverterFactory.dataToJsonConverterSpies
				.get(0);
		assertEquals(coraJsonRecord.json,
				dataToJsonConverterSpy.jsonObjectBuilder.toJsonFormattedString());

		List<CoraJsonRecord> secondRow = convertedRows.get(0);
		CoraJsonRecord coraJsonRecord2 = secondRow.get(0);
		DataToJsonConverterSpy dataToJsonConverterSpy2 = dataToJsonConverterFactory.dataToJsonConverterSpies
				.get(0);

		assertEquals(coraJsonRecord2.json,
				dataToJsonConverterSpy2.jsonObjectBuilder.toJsonFormattedString());

	}

	private void addSecondRow() {
		Map<String, String> rowFromDb = new HashMap<>();
		rowFromDb.put("id", "400");
		rowFromDb.put("default_name", "Some other subject category");
		rowFromDb.put("subject_code", "someOtherSubjectCode");
		rowFromDb.put("alternative_name", "Some other alternative name");
		rowsFromDb.add(rowFromDb);
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
	public void testConvertSubjectCategoryOneRowWithParentId() {
		rowsFromDb.get(0).put("parent_id", "1167");
		List<List<CoraJsonRecord>> convertedRows = subjectCategoryListFromDbToCoraConverter
				.convertToJsonFromRowsFromDb(rowsFromDb);

		assertConversionWasDoneCorrectly(convertedRows);

		ClientDataGroup groupSentToConverter = (ClientDataGroup) dataToJsonConverterFactory.dataElements
				.get(0);
		assertCorrectRecordInfo(groupSentToConverter, "406");
		assertCorrectGroupSentToConverterUsingGroupNameAlternativeNameAndCode(groupSentToConverter,
				"Some subject category", "Some alternative name", "someSubjectCode");

		ClientDataGroup parentGroup = groupSentToConverter
				.getFirstGroupWithNameInData("nationalSubjectCategoryParent");
		ClientDataGroup parentLink = parentGroup
				.getFirstGroupWithNameInData("nationalSubjectCategory");
		assertEquals(parentLink.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"nationalSubjectCategory");
		assertEquals(parentLink.getFirstAtomicValueWithNameInData("linkedRecordId"), "1167");
		assertEquals(parentGroup.getRepeatId(), "0");

	}

	private void assertConversionWasDoneCorrectly(List<List<CoraJsonRecord>> convertedRows) {
		assertEquals(convertedRows.size(), 1);
		List<CoraJsonRecord> row = convertedRows.get(0);
		CoraJsonRecord coraJsonRecord = row.get(0);

		assertEquals(coraJsonRecord.recordType, "nationalSubjectCategory");
		assertEquals(dataToJsonConverterFactory.calledNumOfTimes, 1);
		DataToJsonConverterSpy dataToJsonConverterSpy = dataToJsonConverterFactory.dataToJsonConverterSpies
				.get(0);
		assertTrue(dataToJsonConverterSpy.toJsonObjectBuilderWasCalled);

		assertEquals(coraJsonRecord.json,
				dataToJsonConverterSpy.jsonObjectBuilder.toJsonFormattedString());
	}

}
