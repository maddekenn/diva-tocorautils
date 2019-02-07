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
 */package se.uu.ub.cora.diva.tocorautils.convert;

import static org.testng.Assert.assertEquals;
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

public class SubjectCategoryFromDbToCoraConverterTest {
	List<Map<String, String>> rowsFromDb = new ArrayList<Map<String, String>>();
	CoraClientSpy coraClient;
	private JsonBuilderFactory jsonFactory;
	private SubjectCategoryFromDbToCoraConverter subjectCategoryFromDbToCoraConverter;
	private DataToJsonConverterFactorySpy dataToJsonConverterFactory;

	@BeforeMethod
	public void beforeMethod() {
		rowsFromDb = new ArrayList<Map<String, String>>();
		Map<String, String> rowFromDb = new HashMap<>();
		rowFromDb.put("id", "406");
		rowFromDb.put("default_name", "Some subject category");
		rowFromDb.put("subject_code", "someSubjectCode");

		rowsFromDb.add(rowFromDb);

		coraClient = new CoraClientSpy();
		jsonFactory = new OrgJsonBuilderFactoryAdapter();
		dataToJsonConverterFactory = new DataToJsonConverterFactorySpy();
		subjectCategoryFromDbToCoraConverter = SubjectCategoryFromDbToCoraConverter
				.usingJsonFactoryAndConverterFactory(jsonFactory, dataToJsonConverterFactory);

	}

	@Test
	public void testConvertsubjectCategoryOneRowMinimalRequiredValues() {
		List<List<CoraJsonRecord>> convertedRows = subjectCategoryFromDbToCoraConverter
				.convertToJsonFromRowsFromDb(rowsFromDb);

		assertEquals(convertedRows.size(), 1);
		List<CoraJsonRecord> row = convertedRows.get(0);
		CoraJsonRecord coraJsonRecord = row.get(0);

		assertEquals(coraJsonRecord.recordType, "nationalSubjectCategory");

		ClientDataGroup groupSentToConverter = (ClientDataGroup) dataToJsonConverterFactory.dataElements
				.get(0);
		assertCorrectRecordInfo(groupSentToConverter, "406");

		assertEquals(groupSentToConverter.getNameInData(), "nationalSubjectCategory");
		assertEquals(groupSentToConverter.getFirstAtomicValueWithNameInData(
				"nationalSubjectCategoryName"), "Some subject category");
		assertEquals(groupSentToConverter.getFirstAtomicValueWithNameInData("subjectCode"),
				"someSubjectCode");

		assertEquals(dataToJsonConverterFactory.calledNumOfTimes, 1);
		DataToJsonConverterSpy dataToJsonConverterSpy = dataToJsonConverterFactory.dataToJsonConverterSpies
				.get(0);
		assertTrue(dataToJsonConverterSpy.toJsonObjectBuilderWasCalled);

		assertEquals(coraJsonRecord.json,
				dataToJsonConverterSpy.jsonObjectBuilder.toJsonFormattedString());

	}

	@Test
	public void testConvertSubjectCategoryTwoRowsMinimalRequiredValues() {
		Map<String, String> rowFromDb = new HashMap<>();
		rowFromDb.put("id", "400");
		rowFromDb.put("default_name", "Some other subject category");
		rowFromDb.put("subject_code", "someOtherSubjectCode");

		rowsFromDb.add(rowFromDb);

		List<List<CoraJsonRecord>> convertedRows = subjectCategoryFromDbToCoraConverter
				.convertToJsonFromRowsFromDb(rowsFromDb);

		assertEquals(convertedRows.size(), 2);

		ClientDataGroup groupSentToConverter = (ClientDataGroup) dataToJsonConverterFactory.dataElements
				.get(0);
		assertCorrectRecordInfo(groupSentToConverter, "406");
		assertEquals(groupSentToConverter.getNameInData(), "nationalSubjectCategory");
		assertEquals(groupSentToConverter.getFirstAtomicValueWithNameInData(
				"nationalSubjectCategoryName"), "Some subject category");
		assertEquals(groupSentToConverter.getFirstAtomicValueWithNameInData("subjectCode"),
				"someSubjectCode");

		ClientDataGroup groupSentToConverter2 = (ClientDataGroup) dataToJsonConverterFactory.dataElements
				.get(1);
		assertCorrectRecordInfo(groupSentToConverter2, "400");
		assertEquals(groupSentToConverter2.getNameInData(), "nationalSubjectCategory");
		assertEquals(groupSentToConverter2.getFirstAtomicValueWithNameInData(
				"nationalSubjectCategoryName"), "Some other subject category");
		assertEquals(groupSentToConverter2.getFirstAtomicValueWithNameInData("subjectCode"),
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

	private void assertCorrectRecordInfo(ClientDataGroup groupSentToConverter, String expectedId) {
		ClientDataGroup recordInfo = groupSentToConverter.getFirstGroupWithNameInData("recordInfo");
		String id = recordInfo.getFirstAtomicValueWithNameInData("id");
		assertEquals(id, expectedId);

		ClientDataGroup dataDivider = recordInfo.getFirstGroupWithNameInData("dataDivider");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordType"), "system");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordId"), "diva");
	}

	private void assertCorrectGroupSentToConverterFactory(ClientDataGroup group, String expectedId,
			int numOfChildren) {
		String id = getIdFromDataGroup(group);
		assertEquals(id, expectedId);
		assertEquals(group.getChildren().size(), numOfChildren);
	}

	private String getIdFromDataGroup(ClientDataGroup group) {
		ClientDataGroup recordInfo = group.getFirstGroupWithNameInData("recordInfo");
		return recordInfo.getFirstAtomicValueWithNameInData("id");
	}

	private void assertCorrectExtraData(ClientDataGroup langItem, String partValue) {
		ClientDataGroup extraData = langItem.getFirstGroupWithNameInData("extraData");
		ClientDataGroup extraDataPart = extraData.getFirstGroupWithNameInData("extraDataPart");
		assertEquals(extraDataPart.getFirstAtomicValueWithNameInData("value"), partValue);
		assertEquals(extraDataPart.getAttributes().get("type"), "iso31661Alpha2");
	}

	private void assertCorrectCollectionWithOneRefSentToFactory(ClientDataGroup langCollection,
			String expectedId, int numOfChildren, String nameInData) {
		assertCorrectGroupSentToConverterFactory(langCollection, expectedId, numOfChildren);
		assertEquals(langCollection.getFirstAtomicValueWithNameInData("nameInData"), nameInData);
		String expectedItemId = "seSubjectCategoryItem";
		assertCorrectChildReferencesWithOneItem(langCollection, expectedItemId);
	}

	private void assertCorrectChildReferencesWithOneItem(ClientDataGroup langCollection,
			String expectedItemId) {
		ClientDataGroup childReferences = langCollection
				.getFirstGroupWithNameInData("collectionItemReferences");
		assertEquals(childReferences.getAllChildrenWithNameInData("ref").size(), 1);
		ClientDataGroup ref = childReferences.getFirstGroupWithNameInData("ref");
		assertEquals(ref.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"subjectCategoryCollectionItem");
		assertEquals(ref.getFirstAtomicValueWithNameInData("linkedRecordId"), expectedItemId);
	}

	// @Test
	// public void testConvertSubjectCategoryTwoRow() {
	// Map<String, String> rowFromDb = new HashMap<>();
	// rowFromDb.put("alpha2code", "NO");
	// rowFromDb.put("svText", "Norge");
	// rowsFromDb.add(rowFromDb);
	//
	// List<List<CoraJsonRecord>> convertedRows =
	// subjectCategoryFromDbToCoraConverter
	// .convertToJsonFromRowsFromDb(rowsFromDb);
	//
	// assertEquals(dataToJsonConverterFactory.calledNumOfTimes, 7);
	// assertEquals(convertedRows.size(), 3);
	// List<CoraJsonRecord> row = convertedRows.get(0);
	// CoraJsonRecord coraJsonRecordText = row.get(0);
	// assertEquals(coraJsonRecordText.recordType, "coraText");
	// String textJsonFromSpy = "{\"name\":\"text\"}";
	// assertEquals(coraJsonRecordText.json, textJsonFromSpy);
	//
	// CoraJsonRecord coraJsonRecordDefText = row.get(1);
	// assertEquals(coraJsonRecordDefText.recordType, "coraText");
	// assertEquals(coraJsonRecordDefText.json, textJsonFromSpy);
	//
	// CoraJsonRecord coraJsonRecordItem = row.get(2);
	// assertEquals(coraJsonRecordItem.recordType, "subjectCategoryCollectionItem");
	// String collectionItemJsonFromSpy = "{\"name\":\"metadata\"}";
	// assertEquals(coraJsonRecordItem.json, collectionItemJsonFromSpy);
	//
	// List<CoraJsonRecord> row2 = convertedRows.get(1);
	// CoraJsonRecord coraJsonRecordText2 = row2.get(0);
	// assertEquals(coraJsonRecordText2.recordType, "coraText");
	// assertEquals(coraJsonRecordText2.json, textJsonFromSpy);
	//
	// CoraJsonRecord coraJsonRecordDefText2 = row2.get(1);
	// assertEquals(coraJsonRecordDefText2.recordType, "coraText");
	// assertEquals(coraJsonRecordDefText2.json, textJsonFromSpy);
	//
	// CoraJsonRecord coraJsonRecordItem2 = row2.get(2);
	// assertEquals(coraJsonRecordItem2.recordType,
	// "subjectCategoryCollectionItem");
	// assertEquals(coraJsonRecordItem2.json, collectionItemJsonFromSpy);
	// assertCorrectFirstTextsAndItem();
	// assertCorrectSecondTextsAndItem();
	//
	// }
	//
	// private void assertCorrectSecondTextsAndItem() {
	// ClientDataGroup text = (ClientDataGroup)
	// dataToJsonConverterFactory.dataElements.get(3);
	// assertCorrectTextGroupSentToConverterFactory(text,
	// "noSubjectCategoryItemText", 1, 2);
	//
	// ClientDataGroup defText = (ClientDataGroup)
	// dataToJsonConverterFactory.dataElements.get(4);
	// assertCorrectTextGroupSentToConverterFactory(defText,
	// "noSubjectCategoryItemDefText", 1, 2);
	//
	// ClientDataGroup langItem = (ClientDataGroup)
	// dataToJsonConverterFactory.dataElements.get(5);
	// assertCorrectGroupSentToConverterFactory(langItem, "noSubjectCategoryItem",
	// 3);
	// assertEquals(langItem.getFirstAtomicValueWithNameInData("nameInData"), "NO");
	// assertCorrectExtraData(langItem, "NO");
	// }
}
