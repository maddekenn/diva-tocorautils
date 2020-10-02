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

import java.util.List;
import java.util.Map;

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.RecordIdentifier;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverter;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverterFactory;
import se.uu.ub.cora.diva.tocorautils.CoraJsonRecord;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;

public class FromDbToCoraSubjectCategoryConverter implements FromDbToCoraJsonConverter {

	private static final String SUBJECT_ID = "subject_id";
	private static final String NATIONAL_SUBJECT_CATEGORY = "nationalSubjectCategory";
	protected JsonBuilderFactory jsonFactory;
	protected DataToJsonConverterFactory dataToJsonConverterFactory;
	protected List<RecordIdentifier> collectionItems;

	protected FromDbToCoraSubjectCategoryConverter(JsonBuilderFactory jsonFactory,
			DataToJsonConverterFactory dataToJsonConverterFactory) {
		this.jsonFactory = jsonFactory;
		this.dataToJsonConverterFactory = dataToJsonConverterFactory;
	}

	public static FromDbToCoraSubjectCategoryConverter usingJsonFactoryAndConverterFactory(
			JsonBuilderFactory jsonFactory, DataToJsonConverterFactory dataToJsonConverterFactory) {
		return new FromDbToCoraSubjectCategoryConverter(jsonFactory, dataToJsonConverterFactory);
	}

	@Override
	public CoraJsonRecord convertToJsonFromRowFromDb(Map<String, Object> rowFromDb) {
		ClientDataGroup nationalSubjectCategory = createDataGroupWithRecordInfo(rowFromDb);
		addMandatoryChildren(rowFromDb, nationalSubjectCategory);
		String json = convertToJson(nationalSubjectCategory);
		return CoraJsonRecord.withRecordTypeAndJson(NATIONAL_SUBJECT_CATEGORY, json);
	}

	private String convertToJson(ClientDataGroup nationalSubjectCategory) {
		DataToJsonConverter converter = getDataToJsonConverterFactory()
				.createForClientDataElement(jsonFactory, nationalSubjectCategory);
		return converter.toJson();
	}

	private ClientDataGroup createDataGroupWithRecordInfo(Map<String, Object> rowFromDb) {
		ClientDataGroup nationalSubjectCategory = ClientDataGroup
				.withNameInData(NATIONAL_SUBJECT_CATEGORY);
		ClientDataGroup recordInfo = createRecordInfo(rowFromDb);

		nationalSubjectCategory.addChild(recordInfo);
		return nationalSubjectCategory;
	}

	private ClientDataGroup createRecordInfo(Map<String, Object> rowFromDb) {
		ClientDataGroup recordInfo = ClientDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(ClientDataAtomic.withNameInDataAndValue("id",
				String.valueOf(rowFromDb.get(SUBJECT_ID))));
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

	private void addMandatoryChildren(Map<String, Object> rowFromDb,
			ClientDataGroup nationalSubjectCategory) {
		nationalSubjectCategory.addChild(createDefaultNameChild(rowFromDb));
		nationalSubjectCategory.addChild(createAlternativeNameChild(rowFromDb));
		nationalSubjectCategory.addChild(createSubjectCodeChild(rowFromDb));
	}

	private ClientDataGroup createDefaultNameChild(Map<String, Object> rowFromDb) {
		ClientDataGroup nameGroup = ClientDataGroup.withNameInData("name");
		ClientDataAtomic name = ClientDataAtomic.withNameInDataAndValue(
				"nationalSubjectCategoryName", (String) rowFromDb.get("default_name"));
		nameGroup.addChild(ClientDataAtomic.withNameInDataAndValue("language", "sv"));
		nameGroup.addChild(name);
		return nameGroup;
	}

	private ClientDataGroup createAlternativeNameChild(Map<String, Object> rowFromDb) {
		ClientDataGroup alternativeNameGroup = ClientDataGroup.withNameInData("alternativeName");
		ClientDataAtomic alternativeName = ClientDataAtomic.withNameInDataAndValue(
				"nationalSubjectCategoryAlternativeName",
				(String) rowFromDb.get("alternative_name"));
		alternativeNameGroup.addChild(alternativeName);
		alternativeNameGroup.addChild(ClientDataAtomic.withNameInDataAndValue("language", "en"));
		return alternativeNameGroup;
	}

	private ClientDataAtomic createSubjectCodeChild(Map<String, Object> rowFromDb) {
		return ClientDataAtomic.withNameInDataAndValue("subjectCode",
				(String) rowFromDb.get("subject_code"));
	}

	public DataToJsonConverterFactory getDataToJsonConverterFactory() {
		return dataToJsonConverterFactory;
	}

	public JsonBuilderFactory getJsonBuilderFactory() {
		// needed for tests
		return jsonFactory;
	}

}
