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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.sqldatabase.RecordReader;
import se.uu.ub.cora.sqldatabase.RecordReaderFactory;
import se.uu.ub.cora.tocorautils.CoraJsonRecord;
import se.uu.ub.cora.tocorautils.FromDbToCora;
import se.uu.ub.cora.tocorautils.convert.ListFromDbToCoraConverter;
import se.uu.ub.cora.tocorautils.importing.ImportResult;
import se.uu.ub.cora.tocorautils.importing.Importer;

public class FromDbToCoraSubjectCategory implements FromDbToCora {

	private ListFromDbToCoraConverter fromDbToCoraConverter;
	private RecordReaderFactory recordReaderFactory;
	private Importer importer;

	public static FromDbToCora usingRecordReaderFactoryAndDbToCoraConverterAndImporter(
			RecordReaderFactory recordReaderFactory, ListFromDbToCoraConverter fromDbToCoraConverter,
			Importer importer) {
		return new FromDbToCoraSubjectCategory(recordReaderFactory, fromDbToCoraConverter,
				importer);
	}

	private FromDbToCoraSubjectCategory(RecordReaderFactory recordReaderFactory,
			ListFromDbToCoraConverter fromDbToCoraConverter, Importer importer) {
		this.recordReaderFactory = recordReaderFactory;
		this.fromDbToCoraConverter = fromDbToCoraConverter;
		this.importer = importer;
	}

	@Override
	public ImportResult importFromTable(String tableName) {
		RecordReader recordReader = recordReaderFactory.factor();
		List<Map<String, String>> readAllFromTable = recordReader.readAllFromTable(tableName);
		for (Map<String, String> row : readAllFromTable) {
			Map<String, String> conditions = new HashMap<>();
			String subjectId = row.get("subject_id");
			conditions.put("subject_id", subjectId);
			List<Map<String, String>> parents = recordReader
					.readFromTableUsingConditions("subject_parent", conditions);
		}
		// Entry<String, String> next =
		// readAllFromTable.get(0).entrySet().iterator().next();
		// TODO: läs in eventuella parents för varje subject

		List<List<CoraJsonRecord>> convertedRows = fromDbToCoraConverter
				.convertToJsonFromRowsFromDb(readAllFromTable);
		return importer.createInCora(convertedRows);
	}

	public RecordReaderFactory getRecordReaderFactory() {
		// needed for test
		return recordReaderFactory;
	}

	public ListFromDbToCoraConverter getFromDbToCoraConverter() {
		// needed for test
		return fromDbToCoraConverter;
	}

	public Importer getImporter() {
		// needed for test
		return importer;
	}
	//
	//
	//
	// @Override
	// public ImportResult importFromTable(String tableName) {
	// // TODO Auto-generated method stub
	// return null;
	// }

}
