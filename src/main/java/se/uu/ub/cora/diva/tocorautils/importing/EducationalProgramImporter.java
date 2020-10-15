/*
 * Copyright 2020 Uppsala University Library
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
package se.uu.ub.cora.diva.tocorautils.importing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverter;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverterFactory;
import se.uu.ub.cora.json.builder.JsonObjectBuilder;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.sqldatabase.RecordCreator;
import se.uu.ub.cora.sqldatabase.RecordReader;

public class EducationalProgramImporter implements DivaImporter {

	private static final int EDUCATIONAL_PROGRAM_TYPE_CODE = 56;
	private RecordReader recordReader;
	private FromDbToCoraConverter toCoraDataConverter;
	private RecordCreator recordCreator;
	private DataToJsonConverterFactory toJsonConverterFactory;

	public EducationalProgramImporter(RecordReader recordReader,
			FromDbToCoraConverter toCoraDataConverter, RecordCreator recordCreator,
			DataToJsonConverterFactory toJsonConverterFactory) {
		this.recordReader = recordReader;
		this.toCoraDataConverter = toCoraDataConverter;
		this.recordCreator = recordCreator;
		this.toJsonConverterFactory = toJsonConverterFactory;
	}

	@Override
	public void importData() {
		List<Map<String, Object>> rowsFromDb = readRowsFromDb();
		for (Map<String, Object> rowFromDb : rowsFromDb) {
			convertRowAndCreate(rowFromDb);
		}
	}

	private List<Map<String, Object>> readRowsFromDb() {
		Map<String, Object> conditions = new HashMap<>();
		conditions.put("subject_type_id", EDUCATIONAL_PROGRAM_TYPE_CODE);
		return recordReader.readFromTableUsingConditions("subjectview", conditions);
	}

	private void convertRowAndCreate(Map<String, Object> rowFromDb) {
		JsonObject jsonObject = convertRowToJson(rowFromDb);
		Map<String, Object> values = createValuesForInsert(rowFromDb, jsonObject);
		recordCreator.insertIntoTableUsingNameAndColumnsWithValues("educationalProgram", values);
	}

	private JsonObject convertRowToJson(Map<String, Object> rowFromDb) {
		ClientDataGroup dataGroup = toCoraDataConverter.convertToDataGroupFromRowFromDb(rowFromDb);
		DataToJsonConverter converter = toJsonConverterFactory
				.createForClientDataElement(dataGroup);
		JsonObjectBuilder jsonObjectBuilder = converter.toJsonObjectBuilder();
		return jsonObjectBuilder.toJsonObject();

	}

	private Map<String, Object> createValuesForInsert(Map<String, Object> rowFromDb,
			JsonObject jsonObject) {
		Map<String, Object> values = new HashMap<>();
		values.put("record_type", "educationalProgram");
		values.put("record_id", "educationalProgram:" + rowFromDb.get("subject_id"));
		values.put("record", jsonObject);
		return values;
	}

	RecordReader getRecordReader() {
		// needed for test
		return recordReader;
	}

	public FromDbToCoraConverter getFromDbToCoraConverter() {
		// needed for test
		return toCoraDataConverter;
	}

	public RecordCreator getRecordCreator() {
		// needed for test
		return recordCreator;
	}

}
