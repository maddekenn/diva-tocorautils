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
import se.uu.ub.cora.diva.tocorautils.CoraJsonRecord;
import se.uu.ub.cora.diva.tocorautils.doubles.CoraClientSpy;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;

public class FromDbToCoraSubjectCategoryConverterTest {
	se.uu.ub.cora.diva.tocorautils.doubles.CoraClientSpy coraClient;
	private JsonBuilderFactory jsonFactory;
	private FromDbToCoraSubjectCategoryConverter fromDbToCoraSubjectCategoryConverter;
	private DataToJsonConverterFactorySpy dataToJsonConverterFactory;

	private Map<String, Object> rowFromDb = new HashMap<>();

	@BeforeMethod
	public void beforeMethod() {
		rowFromDb.put("subject_id", 406);
		rowFromDb.put("default_name", "Some subject category");
		rowFromDb.put("subject_code", "someSubjectCode");
		rowFromDb.put("alternative_name", "Some alternative name");

		coraClient = new CoraClientSpy();
		jsonFactory = new OrgJsonBuilderFactoryAdapter();
		dataToJsonConverterFactory = new DataToJsonConverterFactorySpy();
		fromDbToCoraSubjectCategoryConverter = FromDbToCoraSubjectCategoryConverter
				.usingJsonFactoryAndConverterFactory(jsonFactory, dataToJsonConverterFactory);

	}

	@Test
	public void testConvertSubjectCategoryOneRowMinimalRequiredValuesNoParent() {
		CoraJsonRecord coraJsonRecord = fromDbToCoraSubjectCategoryConverter
				.convertToJsonFromRowFromDb(rowFromDb);
		assertEquals(coraJsonRecord.recordType, "nationalSubjectCategory");

		ClientDataGroup groupSentToConverter = assertCorrectGroupSentToConverter();

		assertEquals(dataToJsonConverterFactory.calledNumOfTimes, 1);
		assertCorrectConversion(coraJsonRecord);
		assertFalse(
				groupSentToConverter.containsChildWithNameInData("nationalSubjectCategoryParent"));

	}

	private ClientDataGroup assertCorrectGroupSentToConverter() {
		ClientDataGroup groupSentToConverter = (ClientDataGroup) dataToJsonConverterFactory.dataElements
				.get(0);
		assertCorrectRecordInfo(groupSentToConverter, "406");
		assertCorrectGroupSentToConverterUsingGroupNameAlternativeNameAndCode(groupSentToConverter,
				"Some subject category", "Some alternative name", "someSubjectCode");
		return groupSentToConverter;
	}

	private void assertCorrectConversion(CoraJsonRecord coraJsonRecord) {
		DataToJsonConverterSpy dataToJsonConverterSpy = dataToJsonConverterFactory.dataToJsonConverterSpies
				.get(0);
		assertTrue(dataToJsonConverterSpy.toJsonObjectBuilderWasCalled);

		assertEquals(coraJsonRecord.json,
				dataToJsonConverterSpy.jsonObjectBuilder.toJsonFormattedString());
	}

	private void assertCorrectGroupSentToConverterUsingGroupNameAlternativeNameAndCode(
			ClientDataGroup groupSentToConverter, String name, String alternativeName,
			String expected) {
		assertEquals(groupSentToConverter.getNameInData(), "nationalSubjectCategory");

		ClientDataGroup nameGroup = groupSentToConverter.getFirstGroupWithNameInData("name");
		assertEquals(nameGroup.getFirstAtomicValueWithNameInData("nationalSubjectCategoryName"),
				name);
		assertEquals(nameGroup.getFirstAtomicValueWithNameInData("language"), "sv");

		ClientDataGroup alternativeNameGroup = groupSentToConverter
				.getFirstGroupWithNameInData("alternativeName");
		assertEquals(alternativeNameGroup.getFirstAtomicValueWithNameInData(
				"nationalSubjectCategoryAlternativeName"), alternativeName);
		assertEquals(alternativeNameGroup.getFirstAtomicValueWithNameInData("language"), "en");

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

	// @Test
	// public void
	// testConvertSubjectCategoryOneRowMinimalRequiredValuesWithOneParent() {
	// recordReaderFactory.idsToReturnParent.add("406");
	// CoraJsonRecord coraJsonRecord = fromDbToCoraSubjectCategoryConverter
	// .convertToJsonFromRowFromDb(rowFromDb);
	//
	// assertEquals(coraJsonRecord.recordType, "nationalSubjectCategory");
	//
	// ClientDataGroup groupSentToConverter = (ClientDataGroup)
	// dataToJsonConverterFactory.dataElements
	// .get(0);
	// assertCorrectRecordInfo(groupSentToConverter, "406");
	// assertCorrectGroupSentToConverterUsingGroupNameAlternativeNameAndCode(groupSentToConverter,
	// "Some subject category", "Some alternative name", "someSubjectCode");
	//
	// assertEquals(dataToJsonConverterFactory.calledNumOfTimes, 1);
	// assertCorrectConversion(coraJsonRecord);
	//
	// assertParentTableWasReadCorrectly();
	//
	// assertCorrectParentUsingSubjectParentIndexIdAndRepeatId(groupSentToConverter,
	// 0,
	// "someParent0", "0");
	//
	// }

	// private void assertCorrectParentUsingSubjectParentIndexIdAndRepeatId(
	// ClientDataGroup groupSentToConverter, int index, String linkedRecordId,
	// String repeatId) {
	// ClientDataGroup parentGroup = groupSentToConverter
	// .getAllGroupsWithNameInData("nationalSubjectCategoryParent").get(index);
	// ClientDataGroup parentLink = parentGroup
	// .getFirstGroupWithNameInData("nationalSubjectCategory");
	// assertEquals(parentLink.getFirstAtomicValueWithNameInData("linkedRecordType"),
	// "nationalSubjectCategory");
	// assertEquals(parentLink.getFirstAtomicValueWithNameInData("linkedRecordId"),
	// linkedRecordId);
	// assertEquals(parentGroup.getRepeatId(), repeatId);
	// }

	// @Test
	// public void
	// testConvertSubjectCategoryOneRowMinimalRequiredValuesWithTwoParents() {
	// recordReaderFactory.noOfParentsToReturn = 2;
	// recordReaderFactory.idsToReturnParent.add("406");
	// CoraJsonRecord coraJsonRecord = fromDbToCoraSubjectCategoryConverter
	// .convertToJsonFromRowFromDb(rowFromDb);
	//
	// assertEquals(coraJsonRecord.recordType, "nationalSubjectCategory");
	//
	// ClientDataGroup groupSentToConverter = (ClientDataGroup)
	// dataToJsonConverterFactory.dataElements
	// .get(0);
	// assertCorrectRecordInfo(groupSentToConverter, "406");
	// assertCorrectGroupSentToConverterUsingGroupNameAlternativeNameAndCode(groupSentToConverter,
	// "Some subject category", "Some alternative name", "someSubjectCode");
	//
	// assertEquals(dataToJsonConverterFactory.calledNumOfTimes, 1);
	// assertCorrectConversion(coraJsonRecord);
	//
	// assertParentTableWasReadCorrectly();
	//
	// assertEquals(groupSentToConverter
	// .getAllGroupsWithNameInData("nationalSubjectCategoryParent").size(), 2);
	//
	// assertCorrectParentUsingSubjectParentIndexIdAndRepeatId(groupSentToConverter,
	// 0,
	// "someParent0", "0");
	//
	// assertCorrectParentUsingSubjectParentIndexIdAndRepeatId(groupSentToConverter,
	// 1,
	// "someParent1", "1");
	// }

}
