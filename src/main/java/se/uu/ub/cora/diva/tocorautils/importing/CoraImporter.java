/*
 * Copyright 2018, 2019 Uppsala University Library
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

import java.util.List;

import se.uu.ub.cora.diva.tocorautils.CoraJsonRecord;
import se.uu.ub.cora.javaclient.cora.CoraClient;

public final class CoraImporter implements Importer {

	private CoraClient coraClient;

	public static CoraImporter usingCoraClient(CoraClient coraClient) {
		return new CoraImporter(coraClient);
	}

	private CoraImporter(CoraClient coraClient) {
		this.coraClient = coraClient;
	}

	@Override
	public ImportResult createInCora(List<List<CoraJsonRecord>> listOfConvertedRows) {
		ImportResult importResult = new ImportResult();
		createRecordsForRows(listOfConvertedRows, importResult);
		return importResult;
	}

	private void createRecordsForRows(List<List<CoraJsonRecord>> listOfConvertedRows,
			ImportResult importResult) {
		for (List<CoraJsonRecord> listWithConvertedRow : listOfConvertedRows) {
			createRecordForRow(listWithConvertedRow, importResult);
		}
	}

	private void createRecordForRow(List<CoraJsonRecord> listWithConvertedRow,
			ImportResult importResult) {
		for (CoraJsonRecord coraJsonRecord : listWithConvertedRow) {
			tryToCreateRecordForJson(coraJsonRecord, importResult);
		}
	}

	private void tryToCreateRecordForJson(CoraJsonRecord coraJsonRecord,
			ImportResult importResult) {
		try {
			String returnedJson = createRecordForJson(coraJsonRecord.recordType,
					coraJsonRecord.json);
			CoraJsonRecord updatedCoraJsonRecord = CoraJsonRecord
					.withRecordTypeAndJson(coraJsonRecord.recordType, returnedJson);
			importResult.returnedJsonRecords.add(updatedCoraJsonRecord);
			importResult.noOfImportedOk++;
		} catch (Exception e) {
			String message = createErrorImportResult(coraJsonRecord.json, e);
			importResult.listOfFails.add(message);
		}
	}

	private String createRecordForJson(String recordType, String jsonText) {
		return coraClient.create(recordType, jsonText);
	}

	private String createErrorImportResult(String jsonText, Exception e) {
		String message = e.getMessage();
		message += " json that failed: ";
		message += jsonText;
		return message;
	}

	@Override
	public ImportResult updateInCora(List<CoraJsonRecord> listOfConvertedRows) {
		ImportResult importResult = new ImportResult();
		for (CoraJsonRecord coraJsonRecord : listOfConvertedRows) {
			coraClient.update(coraJsonRecord.recordType, coraJsonRecord.recordId,
					coraJsonRecord.json);
			importResult.noOfImportedOk++;
		}
		return importResult;
	}

	public CoraClient getCoraClient() {
		// needed for test
		return coraClient;
	}
}
