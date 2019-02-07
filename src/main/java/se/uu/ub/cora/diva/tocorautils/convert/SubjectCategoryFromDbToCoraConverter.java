/*
 * Copyright 2018 Uppsala University Library
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
import se.uu.ub.cora.tocorautils.convert.FromDbToCoraConverter;

public class SubjectCategoryFromDbToCoraConverter implements FromDbToCoraConverter {

	private static final String SUBJECT_CATEGORY_COLLECTION_ITEM = "subjectCategoryCollectionItem";
	protected JsonBuilderFactory jsonFactory;
	protected DataToJsonConverterFactory dataToJsonConverterFactory;
	protected List<RecordIdentifier> collectionItems;

	protected SubjectCategoryFromDbToCoraConverter(JsonBuilderFactory jsonFactory,
			DataToJsonConverterFactory dataToJsonConverterFactory) {
		this.jsonFactory = jsonFactory;
		this.dataToJsonConverterFactory = dataToJsonConverterFactory;
	}

	public static SubjectCategoryFromDbToCoraConverter usingJsonFactoryAndConverterFactory(
			JsonBuilderFactory jsonFactory, DataToJsonConverterFactory dataToJsonConverterFactory) {
		return new SubjectCategoryFromDbToCoraConverter(jsonFactory, dataToJsonConverterFactory);
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

		nationalSubjectCategory.addChild(ClientDataAtomic.withNameInDataAndValue(
				"nationalSubjectCategoryName", rowFromDb.get("default_name")));
		nationalSubjectCategory.addChild(ClientDataAtomic.withNameInDataAndValue("subjectCode",
				rowFromDb.get("subject_code")));
		DataToJsonConverter converter = getDataToJsonConverterFactory()
				.createForClientDataElement(jsonFactory, nationalSubjectCategory);
		String json = converter.toJson();

		CoraJsonRecord subjectCategoryRecord = CoraJsonRecord
				.withRecordTypeAndJson("nationalSubjectCategory", json);
		subjectCatagoryList.add(subjectCategoryRecord);
		return subjectCatagoryList;
	}

	private ClientDataGroup createDataGroupWithRecordInfo(Map<String, String> rowFromDb) {
		ClientDataGroup nationalSubjectCategory = ClientDataGroup
				.withNameInData("nationalSubjectCategory");
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

	// protected void handleRow(List<List<CoraJsonRecord>> convertedRows,
	// Map<String, String> rowFromDb) {
	// convertToJsonFromRow(convertedRows, rowFromDb);
	// }
	//
	// protected void convertToJsonFromRow(List<List<CoraJsonRecord>> convertedRows,
	// Map<String, String> rowFromDb) {
	// collectionItems = new ArrayList<>();
	// List<CoraJsonRecord> convertedRow = new ArrayList<>();
	// convertTexts(rowFromDb, convertedRow);
	// convertItem(rowFromDb, convertedRow);
	// convertedRows.add(convertedRow);
	// }

	// private void convertTexts(Map<String, String> rowFromDb, List<CoraJsonRecord>
	// convertedRow) {
	// List<ClientDataGroup> texts =
	// getConstructedTextDataGroupsToCreate(rowFromDb);
	// for (ClientDataGroup text : texts) {
	// String json = convertText(text);
	// convertedRow.add(CoraJsonRecord.withRecordTypeAndJson("coraText", json));
	// }
	// }

	protected List<ClientDataGroup> getConstructedTextDataGroupsToCreate(
			Map<String, String> rowFromDb) {
		// TextFromCountryConstructor textConstructor = new
		// TextFromCountryConstructor();
		// return textConstructor.constructFromDbRow(rowFromDb);
		List<ClientDataGroup> texts = new ArrayList<>();
		ClientDataGroup text = ClientDataGroup.withNameInData("text");
		texts.add(text);
		ClientDataGroup defText = ClientDataGroup.withNameInData("text");
		texts.add(defText);
		return texts;

	}

	private String convertText(ClientDataGroup text) {
		DataToJsonConverter converter = getDataToJsonConverterFactory()
				.createForClientDataElement(jsonFactory, text);
		return converter.toJson();
	}

	// protected ClientDataGroup getConstructedItemToCreate(Map<String, String>
	// rowFromDb) {
	// CollectionItemConstructor itemConstructor = new
	// CountryCollectionItemConstructor();
	// return itemConstructor.convert(rowFromDb);
	// }
	//
	// private void convertItem(Map<String, String> rowFromDb, List<CoraJsonRecord>
	// convertedRow) {
	// ClientDataGroup itemDataGroup = getConstructedItemToCreate(rowFromDb);
	// String id = itemDataGroup.getFirstGroupWithNameInData("recordInfo")
	// .getFirstAtomicValueWithNameInData("id");
	// collectionItems.add(RecordIdentifier.usingTypeAndId(getItemType(), id));
	// DataToJsonConverter converter = getDataToJsonConverterFactory()
	// .createForClientDataElement(jsonFactory, itemDataGroup);
	// String json = converter.toJson();
	// convertedRow.add(CoraJsonRecord.withRecordTypeAndJson(getItemType(), json));
	// }
	//
	// protected String getItemType() {
	// return SUBJECT_CATEGORY_COLLECTION_ITEM;
	// }
	//
	// private List<CoraJsonRecord> createItemCollectionForCreatedCollectionItems()
	// {
	// List<CoraJsonRecord> itemCollectionHolderList = new ArrayList<>();
	// String itemCollectionJson = createItemCollectionAsJson();
	//
	// CoraJsonRecord coraRecordJsonItemCollection = CoraJsonRecord
	// .withRecordTypeAndJson("metadataItemCollection", itemCollectionJson);
	// itemCollectionHolderList.add(coraRecordJsonItemCollection);
	// return itemCollectionHolderList;
	// }
	//
	// private String createItemCollectionAsJson() {
	// ItemCollectionConstructor itemCollectionConstructor =
	// ItemCollectionConstructor
	// .withDataDivider("cora");
	// ClientDataGroup itemCollectionDataGroup = createItemCollectionDataGroup(
	// itemCollectionConstructor);
	// DataToJsonConverter converter = dataToJsonConverterFactory
	// .createForClientDataElement(jsonFactory, itemCollectionDataGroup);
	// return converter.toJson();
	// }
	//
	// protected ClientDataGroup createItemCollectionDataGroup(
	// ItemCollectionConstructor itemCollectionConstructor) {
	// return
	// itemCollectionConstructor.constructUsingIdAndNameInDataAndCollectionItems(
	// "completeCountryCollection", "completeCountry", collectionItems);
	// }

	public DataToJsonConverterFactory getDataToJsonConverterFactory() {
		return dataToJsonConverterFactory;
	}

	public JsonBuilderFactory getJsonBuilderFactory() {
		// needed for tests
		return jsonFactory;
	}

}
