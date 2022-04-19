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

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.diva.tocorautils.convert.FromDbToCoraConverterFactory;
import se.uu.ub.cora.diva.tocorautils.doubles.CoraImporterSpy;
import se.uu.ub.cora.diva.tocorautils.doubles.FromDbToCoraConverterSpy;
import se.uu.ub.cora.diva.tocorautils.doubles.RecordReaderFactorySpy;
import se.uu.ub.cora.diva.tocorautils.doubles.RecordReaderSpy;
import se.uu.ub.cora.diva.tocorautils.importing.DatabaseFacadeSpy;
import se.uu.ub.cora.diva.tocorautils.importing.ImportResult;

public class FromDbToCoraSubjectCategoryTest {

	private FromDbToCoraConverterSpy toCoraConverter;
	private RecordReaderFactorySpy recordReaderFactory;
	private CoraImporterSpy importer;
	private FromDbToCoraSubjectCategory fromDbToCora;
	private RecordCompleterSpy recordCompleter;
	private DatabaseFacadeSpy databaseFacade;
	private FromDbToCoraConverterFactory toCoraConverterFactory;
	private JsonToDataConverterFactorySpy jsonToDataConverterFactory;

	// select sn.subject_name as default_name, sn2.subject_name as alternative_name from
	// subject_name sn left join subject_name sn2 on sn.subject_id = sn2.subject_id where sn.locale
	// ='sv' and sn2.locale ='en';
	@BeforeMethod
	public void setUp() {
		toCoraConverter = new FromDbToCoraConverterSpy();
		// recordReaderFactory = new RecordReaderFactorySpy();
		importer = new CoraImporterSpy();
		recordCompleter = new RecordCompleterSpy();
		jsonToDataConverterFactory = new JsonToDataConverterFactorySpy();

		databaseFacade = new DatabaseFacadeSpy();
		toCoraConverterFactory = new FromDbToCoraConverterFactorySpy();

		fromDbToCora = new FromDbToCoraSubjectCategory(databaseFacade, toCoraConverterFactory,
				toCoraConverter, recordCompleter, importer);

		// fromDbToCora = (FromDbToCoraSubjectCategory) FromDbToCoraSubjectCategory
		// .usingRecordReaderFactoryDbToCoraConverterRecordCompleterJsonToDataConverterFactoryAndImporter(
		// recordReaderFactory, toCoraConverter, recordCompleter,
		// jsonToDataConverterFactory, importer);
	}

	@Test
	public void testGetters() {
		assertEquals(fromDbToCora.getDatabaseFacade(), databaseFacade);
		assertEquals(fromDbToCora.getFromDbToCoraConverter(), toCoraConverter);
		assertEquals(fromDbToCora.getImporter(), importer);
		assertEquals(fromDbToCora.getRecordCompleter(), recordCompleter);
		assertEquals(fromDbToCora.getToCoraConverterFactory(), toCoraConverterFactory);
	}

	@Test
	public void testImportFromTableOneRow() {
		ImportResult importResult = fromDbToCora.importFromTable("someTableName");

		// RecordReaderSpy factoredReader = recordReaderFactory.factored;
		// assertEquals(factoredReader.usedTableNames.get(0), "someTableName");
		// assertEquals(factoredReader.usedTableNames.size(), 1);

		// assertEquals(toCoraConverter.returnedJsonRecords.size(), 1);
		// assertAllRecordsInReadListAreSentToConverter(factoredReader);
		// assertAllConvertedRecordsAreSentToImporter();
		//
		// assertEquals(importResult.listOfFails.get(0), "failure from CoraImporterSpy");
		// assertEquals(recordCompleter.completeMetadataCalledNumOfTimes, 1);
		//
		// assertCorrectDataIsSentFromReadRowToConverterToCompleterUsingIndex(0);
		//
		// List<CoraJsonRecord> listSentToConverter = importer.listOfConvertedRows.get(0);
		// assertEquals(listSentToConverter.size(), 1);
		// assertCorrectJsonRecordSentToUpdateUsingIndex(listSentToConverter, 0);
		//
		// assertEquals(importResult.noOfImportedOk, 1);
		// assertEquals(importResult.noOfUpdatedOk, 1);
	}

	private void assertCorrectJsonRecordSentToUpdateUsingIndex(
			List<CoraJsonRecord> listSentToConverter, int index) {
		CoraJsonRecord coraJsonRecordSentToCreate = listSentToConverter.get(index);
		CoraJsonRecord coraJsonRecordSentToUpdate = importer.listOfUpdatedRows.get(index);

		assertEquals(coraJsonRecordSentToUpdate.recordType, coraJsonRecordSentToCreate.recordType);
		assertEquals(coraJsonRecordSentToUpdate.json, "some dummy json from recordCompleterSpy");
		assertEquals(coraJsonRecordSentToUpdate.recordId, "someRecordId");
	}

	private void assertCorrectDataIsSentFromReadRowToConverterToCompleterUsingIndex(int index) {
		JsonToDataConverterSpy factoredConverter = jsonToDataConverterFactory.factoredConverters
				.get(index);
		assertEquals(factoredConverter.dataGroup, recordCompleter.dataGroups.get(index));
	}

	private void assertAllRecordsInReadListAreSentToConverter(RecordReaderSpy factoredReader) {
		assertEquals(factoredReader.returnedListForReadAll, toCoraConverter.rowsFromDb);
	}

	private void assertAllConvertedRecordsAreSentToImporter() {
		assertEquals(toCoraConverter.returnedJsonRecords, importer.listOfConvertedRows.get(0));
	}

	@Test
	public void testImportFromTableTwoRows() {
		recordReaderFactory.noOfRecordsToReturn = 2;
		ImportResult importResult = fromDbToCora.importFromTable("someTableName");

		RecordReaderSpy factoredReader = recordReaderFactory.factored;
		assertEquals(factoredReader.usedTableNames.get(0), "someTableName");
		assertEquals(factoredReader.usedTableNames.size(), 1);

		assertEquals(toCoraConverter.returnedJsonRecords.size(), 2);
		assertAllRecordsInReadListAreSentToConverter(factoredReader);
		assertAllConvertedRecordsAreSentToImporter();

		assertCorrectDataIsSentFromReadRowToConverterToCompleterUsingIndex(0);
		assertCorrectDataIsSentFromReadRowToConverterToCompleterUsingIndex(1);

		List<CoraJsonRecord> listSentToConverter = importer.listOfConvertedRows.get(0);
		assertEquals(listSentToConverter.size(), 2);
		assertCorrectJsonRecordSentToUpdateUsingIndex(listSentToConverter, 0);
		assertCorrectJsonRecordSentToUpdateUsingIndex(listSentToConverter, 1);
		assertEquals(importResult.noOfImportedOk, 2);
		assertEquals(importResult.noOfUpdatedOk, 2);
	}

}
