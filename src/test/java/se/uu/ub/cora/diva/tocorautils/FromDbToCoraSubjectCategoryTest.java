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
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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

	@BeforeMethod
	public void setUp() {
		toCoraConverter = new FromDbToCoraConverterSpy();
		recordReaderFactory = new RecordReaderFactorySpy();
		importer = new CoraImporterSpy();
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
	public void testImportFromTableOneRowNoParent() {
		ImportResult importResult = fromDbToCora.importFromTable("someTableName");

		RecordReaderSpy factoredReader = recordReaderFactory.factored;
		assertEquals(factoredReader.usedTableNames.get(0), "someTableName");
		assertEquals(factoredReader.usedTableNames.get(1), "subject_parent");

		List<Map<String, String>> returnedList = factoredReader.returnedListForReadAll;
		Map<String, String> map = returnedList.get(0);

		Map<String, String> usedConditions = factoredReader.usedConditions;
		assertEquals(usedConditions.size(), 1);
		assertEquals(usedConditions.get("subject_id"), map.get("subject_id"));

		assertEquals(factoredReader.returnedListForReadAll, toCoraConverter.rowsFromDb);
		assertEquals(toCoraConverter.rowsFromDb.size(), 1);
		assertEquals(toCoraConverter.returnedList, importer.listOfConvertedRows);

		assertEquals(importResult.listOfFails.get(0), "failure from CoraImporterSpy");
	}

	@Test
	public void testImportFromTableOneRowWithOneParent() {
		recordReaderFactory.idsToReturnParent.add("someId0");
		ImportResult importResult = fromDbToCora.importFromTable("someTableName");

		RecordReaderSpy factoredReader = recordReaderFactory.factored;
		assertEquals(factoredReader.usedTableNames.get(0), "someTableName");
		assertEquals(factoredReader.usedTableNames.get(1), "subject_parent");

		List<Map<String, String>> returnedList = factoredReader.returnedListForReadAll;
		Map<String, String> map = returnedList.get(0);

		Map<String, String> usedConditions = factoredReader.usedConditions;
		assertEquals(usedConditions.size(), 1);
		assertEquals(usedConditions.get("subject_id"), map.get("subject_id"));

		assertEquals(factoredReader.returnedListForReadAllWithConditions.size(), 1);

		assertEquals(factoredReader.returnedListForReadAll, toCoraConverter.rowsFromDb);
		assertEquals(toCoraConverter.rowsFromDb.size(), 1);
		assertEquals(toCoraConverter.returnedList, importer.listOfConvertedRows);

		assertEquals(importResult.listOfFails.get(0), "failure from CoraImporterSpy");
	}

	@Test
	public void testImportFromTableTwoRowsWithOneParent() {
		recordReaderFactory.noOfRecordsToReturn = 2;
		recordReaderFactory.idsToReturnParent.add("someValue0");
		recordReaderFactory.idsToReturnParent.add("someValue1");
		ImportResult importResult = fromDbToCora.importFromTable("someTableName");

		RecordReaderSpy factoredReader = recordReaderFactory.factored;
		assertEquals(factoredReader.usedTableNames.get(0), "someTableName");
		assertEquals(factoredReader.usedTableNames.get(1), "subject_parent");
		assertEquals(factoredReader.usedTableNames.get(2), "subject_parent");

		// List<Map<String, String>> returnedList =
		// factoredReader.returnedListForReadAll;
		// Map<String, String> map = returnedList.get(0);
		// Entry<String, String> onlyEntryInMap = map.entrySet().iterator().next();
		//
		// Map<String, String> usedConditions = factoredReader.usedConditions;
		// assertEquals(usedConditions.size(), 1);
		// assertEquals(usedConditions.get("subject_id"), onlyEntryInMap.getValue());
		//
		// assertEquals(factoredReader.returnedListForReadAll,
		// toCoraConverter.rowsFromDb);
		// assertEquals(toCoraConverter.rowsFromDb.size(), 1);
		// assertEquals(toCoraConverter.returnedList, importer.listOfConvertedRows);
		//
		// assertEquals(importResult.listOfFails.get(0), "failure from
		// CoraImporterSpy");
	}

}
