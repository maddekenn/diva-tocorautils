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
package se.uu.ub.cora.diva.tocorautils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import importing.ImportResult;
import importing.Importer;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataConverterFactory;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataRecordConverter;
import se.uu.ub.cora.diva.tocorautils.convert.FromDbToCoraConverter;
import se.uu.ub.cora.diva.tocorautils.convert.RecordCompleter;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;
import se.uu.ub.cora.sqldatabase.RecordReader;
import se.uu.ub.cora.sqldatabase.RecordReaderFactory;

public class FromDbToCoraSubjectCategory implements FromDbToCora {

	private FromDbToCoraConverter fromDbToCoraConverter;
	private RecordReaderFactory recordReaderFactory;
	private Importer importer;
	private RecordCompleter recordCompleter;
	private JsonToDataConverterFactory jsonToDataConverterFactory;

	public static FromDbToCora usingRecordReaderFactoryDbToCoraConverterRecordCompleterJsonToDataConverterFactoryAndImporter(
			RecordReaderFactory recordReaderFactory, FromDbToCoraConverter fromDbToCoraConverter,
			RecordCompleter recordCompleter, JsonToDataConverterFactory jsonToDataConverterFactory,
			Importer importer) {
		return new FromDbToCoraSubjectCategory(recordReaderFactory, fromDbToCoraConverter,
				recordCompleter, jsonToDataConverterFactory, importer);
	}

	private FromDbToCoraSubjectCategory(RecordReaderFactory recordReaderFactory,
			FromDbToCoraConverter fromDbToCoraConverter, RecordCompleter recordCompleter,
			JsonToDataConverterFactory jsonToDataConverterFactory, Importer importer) {
		this.recordReaderFactory = recordReaderFactory;
		this.fromDbToCoraConverter = fromDbToCoraConverter;
		this.recordCompleter = recordCompleter;
		this.jsonToDataConverterFactory = jsonToDataConverterFactory;
		this.importer = importer;
	}

	@Override
	public ImportResult importFromTable(String tableName) {
		List<Map<String, String>> readRows = readFromTable(tableName);

		List<List<CoraJsonRecord>> convertedRows = convertReadRows(readRows);
		ImportResult importResult = importer.createInCora(convertedRows);

		List<CoraJsonRecord> completedData = completeCreatedRecords(
				importResult.returnedJsonRecords);
		ImportResult importResultForUpdate = importer.updateInCora(completedData);

		return updateImportResult(importResult, importResultForUpdate);
	}

	private List<Map<String, String>> readFromTable(String tableName) {
		RecordReader recordReader = recordReaderFactory.factor();
		return recordReader.readAllFromTable(tableName);
	}

	private List<List<CoraJsonRecord>> convertReadRows(List<Map<String, String>> readAllFromTable) {
		List<List<CoraJsonRecord>> convertedRows = new ArrayList<>();
		List<CoraJsonRecord> convertedInnerRows = new ArrayList<>();
		for (Map<String, String> rowFromDb : readAllFromTable) {
			convertRow(convertedInnerRows, rowFromDb);
		}
		convertedRows.add(convertedInnerRows);
		return convertedRows;
	}

	private void convertRow(List<CoraJsonRecord> convertedInnerRows,
			Map<String, String> rowFromDb) {
		CoraJsonRecord converterRecord = fromDbToCoraConverter
				.convertToJsonFromRowFromDb(rowFromDb);
		convertedInnerRows.add(converterRecord);
	}

	private List<CoraJsonRecord> completeCreatedRecords(List<CoraJsonRecord> returnedJsonRecords) {
		List<CoraJsonRecord> completedData = new ArrayList<>();
		for (CoraJsonRecord jsonRecord : returnedJsonRecords) {
			CoraJsonRecord completedJsonRecord = completeCreatedRecord(jsonRecord.recordType,
					jsonRecord.json);
			completedData.add(completedJsonRecord);
		}
		return completedData;
	}

	private CoraJsonRecord completeCreatedRecord(String recordType, String jsonString) {
		ClientDataGroup dataGroup = convertJsonToDataGroup(jsonString);
		String recordId = getRecordId(dataGroup);
		return completeDataAndCreateJsonRecordForUpdate(recordType, dataGroup, recordId);
	}

	private ClientDataGroup convertJsonToDataGroup(String json) {
		OrgJsonParser jsonParser = new OrgJsonParser();
		JsonObject jsonObject = (JsonObject) jsonParser.parseString(json);

		JsonToDataRecordConverter recordConverter = JsonToDataRecordConverter
				.forJsonObjectUsingConverterFactory(jsonObject, jsonToDataConverterFactory);
		ClientDataRecord record = recordConverter.toInstance();
		return record.getClientDataGroup();
	}

	private ImportResult updateImportResult(ImportResult importResult,
			ImportResult importResultForUpdate) {
		importResult.listOfFails.addAll(importResultForUpdate.listOfFails);
		importResult.noOfUpdatedOk = importResultForUpdate.noOfUpdatedOk;

		return importResult;
	}

	private CoraJsonRecord completeDataAndCreateJsonRecordForUpdate(String recordType,
			ClientDataGroup dataGroup, String recordId) {
		String completedMetadataJson = recordCompleter.completeMetadata(dataGroup);
		return CoraJsonRecord.withRecordTypeAndIdAndJson(recordType, recordId,
				completedMetadataJson);
	}

	private String getRecordId(ClientDataGroup dataGroup) {
		ClientDataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");
		return recordInfo.getFirstAtomicValueWithNameInData("id");
	}

	public RecordReaderFactory getRecordReaderFactory() {
		// needed for test
		return recordReaderFactory;
	}

	public FromDbToCoraConverter getFromDbToCoraConverter() {
		// needed for test
		return fromDbToCoraConverter;
	}

	public Importer getImporter() {
		// needed for test
		return importer;
	}

	public RecordCompleter getRecordCompleter() {
		// needed for test
		return recordCompleter;
	}

	public JsonToDataConverterFactory getJsonToDataConverterFactory() {
		// needed for test
		return jsonToDataConverterFactory;
	}
}
