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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.diva.tocorautils.convert.RecordCompleter;
import se.uu.ub.cora.diva.tocorautils.doubles.CoraImporterSpy;
import se.uu.ub.cora.diva.tocorautils.doubles.FromDbToCoraConverterSpy;
import se.uu.ub.cora.diva.tocorautils.doubles.RecordReaderFactorySpy;
import se.uu.ub.cora.diva.tocorautils.doubles.RecordReaderSpy;
import se.uu.ub.cora.tocorautils.importing.ImportResult;

public class FromDbToCoraSubjectCategoryTest {

	private FromDbToCoraConverterSpy toCoraConverter;
	private RecordReaderFactorySpy recordReaderFactory;
	private CoraImporterSpy importer;
	private FromDbToCoraSubjectCategory fromDbToCora;
	private RecordCompleter recordCompleter;

	@BeforeMethod
	public void setUp() {
		toCoraConverter = new FromDbToCoraConverterSpy();
		recordReaderFactory = new RecordReaderFactorySpy();
		importer = new CoraImporterSpy();
		recordCompleter = new RecordCompleterSpy();
		fromDbToCora = (FromDbToCoraSubjectCategory) FromDbToCoraSubjectCategory
				.usingRecordReaderFactoryAndDbToCoraConverterAndImporter(recordReaderFactory,
						toCoraConverter, importer);
	}

	@Test
	public void testGetters() {
		assertEquals(fromDbToCora.getRecordReaderFactory(), recordReaderFactory);
		assertEquals(fromDbToCora.getFromDbToCoraConverter(), toCoraConverter);
		assertEquals(fromDbToCora.getImporter(), importer);
	}

	@Test
	public void testImportFromTableOneRow() {
		ImportResult importResult = fromDbToCora.importFromTable("someTableName");

		RecordReaderSpy factoredReader = recordReaderFactory.factored;
		assertEquals(factoredReader.usedTableNames.get(0), "someTableName");
		assertEquals(factoredReader.usedTableNames.size(), 1);

		assertEquals(toCoraConverter.returnedJsonRecords.size(), 1);
		assertAllRecordsInReadListAreSentToConverter(factoredReader);
		assertAllConvertedRecordsAreSentToImporter();

		assertEquals(importResult.noOfImportedOk, 1);

		assertEquals(importResult.listOfFails.get(0), "failure from CoraImporterSpy");
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

		assertEquals(importResult.noOfImportedOk, 2);
	}

}
