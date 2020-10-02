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

import se.uu.ub.cora.diva.tocorautils.CoraJsonRecord;
import se.uu.ub.cora.diva.tocorautils.convert.FromDbToCoraJsonConverter;
import se.uu.ub.cora.sqldatabase.RecordCreator;
import se.uu.ub.cora.sqldatabase.RecordReader;

public class EducationalProgramImporter implements DivaImporter {

	private static final int EDUCATIONAL_PROGRAM_TYPE_CODE = 56;
	private RecordReader recordReader;
	private FromDbToCoraJsonConverter converter;
	private RecordCreator recordCreator;

	public EducationalProgramImporter(RecordReader recordReader, FromDbToCoraJsonConverter converter,
			RecordCreator recordCreator) {
		this.recordReader = recordReader;
		this.converter = converter;
		this.recordCreator = recordCreator;
	}

	@Override
	public void importData() {
		List<Map<String, Object>> rowsFromDb = readRowsFromDb();
		for (Map<String, Object> rowFromDb : rowsFromDb) {
			Map<String, Object> values = createValuesForInsert(rowFromDb);
			recordCreator.insertIntoTableUsingNameAndColumnsWithValues("educationalProgram",
					values);
		}
	}

	private List<Map<String, Object>> readRowsFromDb() {
		Map<String, Object> conditions = new HashMap<>();
		conditions.put("subject_type", EDUCATIONAL_PROGRAM_TYPE_CODE);
		return recordReader.readFromTableUsingConditions("subjectview", conditions);
	}

	private Map<String, Object> createValuesForInsert(Map<String, Object> rowFromDb) {
		CoraJsonRecord jsonRecord = converter.convertToJsonFromRowFromDb(rowFromDb);
		Map<String, Object> values = new HashMap<>();
		values.put("record_type", "educationalProgram");
		values.put("record_id", "educationalProgram:" + rowFromDb.get("subject_id"));
		values.put("record", jsonRecord.json);
		return values;
	}

	RecordReader getRecordReader() {
		// needed for test
		return recordReader;
	}

	public FromDbToCoraJsonConverter getFromDbToCoraConverter() {
		// needed for test
		return converter;
	}

	public RecordCreator getRecordCreator() {
		// needed for test
		return recordCreator;
	}

}
