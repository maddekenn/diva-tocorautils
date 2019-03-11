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

import se.uu.ub.cora.diva.tocorautils.convert.FromDbToCoraConverter;
import se.uu.ub.cora.sqldatabase.RecordReader;
import se.uu.ub.cora.sqldatabase.RecordReaderFactory;
import se.uu.ub.cora.tocorautils.CoraJsonRecord;
import se.uu.ub.cora.tocorautils.FromDbToCora;
import se.uu.ub.cora.tocorautils.importing.ImportResult;
import se.uu.ub.cora.tocorautils.importing.Importer;

public class FromDbToCoraSubjectCategory implements FromDbToCora {

	private FromDbToCoraConverter fromDbToCoraConverter;
	private RecordReaderFactory recordReaderFactory;
	private Importer importer;

	public static FromDbToCora usingRecordReaderFactoryAndDbToCoraConverterAndImporter(
			RecordReaderFactory recordReaderFactory, FromDbToCoraConverter fromDbToCoraConverter,
			Importer importer) {
		return new FromDbToCoraSubjectCategory(recordReaderFactory, fromDbToCoraConverter,
				importer);
	}

	private FromDbToCoraSubjectCategory(RecordReaderFactory recordReaderFactory,
			FromDbToCoraConverter fromDbToCoraConverter, Importer importer) {
		this.recordReaderFactory = recordReaderFactory;
		this.fromDbToCoraConverter = fromDbToCoraConverter;
		this.importer = importer;
	}

	@Override
	public ImportResult importFromTable(String tableName) {
		List<Map<String, String>> readRows = readFromTable(tableName);

		List<List<CoraJsonRecord>> convertedRows = convertReadRows(readRows);
		return importer.createInCora(convertedRows);
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

}
