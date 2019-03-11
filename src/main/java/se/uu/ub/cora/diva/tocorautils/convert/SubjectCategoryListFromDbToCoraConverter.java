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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.RecordIdentifier;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverter;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverterFactory;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.tocorautils.CoraJsonRecord;
import se.uu.ub.cora.tocorautils.convert.ListFromDbToCoraConverter;

public class SubjectCategoryListFromDbToCoraConverter implements ListFromDbToCoraConverter {

	private static final String NATIONAL_SUBJECT_CATEGORY = "nationalSubjectCategory";
	protected JsonBuilderFactory jsonFactory;
	protected DataToJsonConverterFactory dataToJsonConverterFactory;
	protected List<RecordIdentifier> collectionItems;

	protected SubjectCategoryListFromDbToCoraConverter(JsonBuilderFactory jsonFactory,
			DataToJsonConverterFactory dataToJsonConverterFactory) {
		this.jsonFactory = jsonFactory;
		this.dataToJsonConverterFactory = dataToJsonConverterFactory;
	}

	public static SubjectCategoryListFromDbToCoraConverter usingJsonFactoryAndConverterFactory(
			JsonBuilderFactory jsonFactory, DataToJsonConverterFactory dataToJsonConverterFactory) {
		return new SubjectCategoryListFromDbToCoraConverter(jsonFactory,
				dataToJsonConverterFactory);
	}

	@Override
	public List<List<CoraJsonRecord>> convertToJsonFromRowsFromDb(
			List<Map<String, String>> rowsFromDb) {
		List<List<CoraJsonRecord>> convertedRows = new ArrayList<>();

		for (Map<String, String> rowFromDb : rowsFromDb) {
			List<CoraJsonRecord> subjectCatagoryList = handleRow(rowFromDb);
			convertedRows.add(subjectCatagoryList);
		}
		return convertedRows;
	}

	private List<CoraJsonRecord> handleRow(Map<String, String> rowFromDb) {
		List<CoraJsonRecord> subjectCatagoryList = new ArrayList<>();

		ClientDataGroup nationalSubjectCategory = createDataGroupWithRecordInfo(rowFromDb);

		addMandatoryChildren(rowFromDb, nationalSubjectCategory);

		possiblyAddParentGroup(rowFromDb, nationalSubjectCategory);
		String json = convertToJson(nationalSubjectCategory);

		CoraJsonRecord subjectCategoryRecord = CoraJsonRecord
				.withRecordTypeAndJson(NATIONAL_SUBJECT_CATEGORY, json);
		subjectCatagoryList.add(subjectCategoryRecord);
		return subjectCatagoryList;
	}

	private String convertToJson(ClientDataGroup nationalSubjectCategory) {
		DataToJsonConverter converter = getDataToJsonConverterFactory()
				.createForClientDataElement(jsonFactory, nationalSubjectCategory);
		return converter.toJson();
	}

	private ClientDataGroup createDataGroupWithRecordInfo(Map<String, String> rowFromDb) {
		ClientDataGroup nationalSubjectCategory = ClientDataGroup
				.withNameInData(NATIONAL_SUBJECT_CATEGORY);
		ClientDataGroup recordInfo = createRecordInfo(rowFromDb);

		nationalSubjectCategory.addChild(recordInfo);
		return nationalSubjectCategory;
	}

	private ClientDataGroup createRecordInfo(Map<String, String> rowFromDb) {
		ClientDataGroup recordInfo = ClientDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(ClientDataAtomic.withNameInDataAndValue("id", rowFromDb.get("id")));
		ClientDataGroup dataDivider = createDataDivider();
		recordInfo.addChild(dataDivider);
		return recordInfo;
	}

	protected ClientDataGroup createDataDivider() {
		ClientDataGroup dataDivider = ClientDataGroup.withNameInData("dataDivider");
		dataDivider.addChild(ClientDataAtomic.withNameInDataAndValue("linkedRecordType", "system"));
		dataDivider.addChild(ClientDataAtomic.withNameInDataAndValue("linkedRecordId", "diva"));
		return dataDivider;
	}

	private void addMandatoryChildren(Map<String, String> rowFromDb,
			ClientDataGroup nationalSubjectCategory) {
		nationalSubjectCategory.addChild(createDefaultNameChild(rowFromDb));
		nationalSubjectCategory.addChild(createAlternativeNameChild(rowFromDb));
		nationalSubjectCategory.addChild(createSubjectCodeChild(rowFromDb));
	}

	private void possiblyAddParentGroup(Map<String, String> rowFromDb,
			ClientDataGroup nationalSubjectCategory) {
		if (rowFromDb.containsKey("parent_subject_id")) {
			addParentGroup(rowFromDb, nationalSubjectCategory);
		}
	}

	private void addParentGroup(Map<String, String> rowFromDb,
			ClientDataGroup nationalSubjectCategory) {
		ClientDataGroup parentGroup = ClientDataGroup
				.withNameInData("nationalSubjectCategoryParent");
		createAndAddParentLink(rowFromDb, parentGroup);
		nationalSubjectCategory.addChild(parentGroup);
		parentGroup.setRepeatId("0");
	}

	private void createAndAddParentLink(Map<String, String> rowFromDb,
			ClientDataGroup parentGroup) {
		ClientDataGroup parentLink = ClientDataGroup.withNameInData("nationalSubjectCategoryLink");
		parentLink.addChild(ClientDataAtomic.withNameInDataAndValue("linkedRecordType",
				NATIONAL_SUBJECT_CATEGORY));
		parentLink.addChild(ClientDataAtomic.withNameInDataAndValue("linkedRecordId",
				rowFromDb.get("parent_subject_id")));
		parentGroup.addChild(parentLink);
	}

	private ClientDataAtomic createDefaultNameChild(Map<String, String> rowFromDb) {
		return ClientDataAtomic.withNameInDataAndValue("nationalSubjectCategoryName",
				rowFromDb.get("default_name"));
	}

	private ClientDataAtomic createAlternativeNameChild(Map<String, String> rowFromDb) {
		return ClientDataAtomic.withNameInDataAndValue("nationalSubjectCategoryAlternativeName",
				rowFromDb.get("alternative_name"));
	}

	private ClientDataAtomic createSubjectCodeChild(Map<String, String> rowFromDb) {
		return ClientDataAtomic.withNameInDataAndValue("subjectCode",
				rowFromDb.get("subject_code"));
	}

	public DataToJsonConverterFactory getDataToJsonConverterFactory() {
		return dataToJsonConverterFactory;
	}

	public JsonBuilderFactory getJsonBuilderFactory() {
		// needed for tests
		return jsonFactory;
	}

}
