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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataElement;
import se.uu.ub.cora.clientdata.ClientDataGroup;

public class EducationalProgramImporterTest {

	private RecordReaderSubjectSpy recordReader;
	private EducationalProgramImporter divaImporter;
	private FromDbToCoraConverterSpy fromClassicToCoraConverter;
	private RecordCreatorSpy recordCreator;
	private DataToJsonConverterFactorySpy toJsonConverterFactory;

	@BeforeMethod
	public void setUp() {
		recordReader = new RecordReaderSubjectSpy();
		fromClassicToCoraConverter = new FromDbToCoraConverterSpy();
		recordCreator = new RecordCreatorSpy();
		toJsonConverterFactory = new DataToJsonConverterFactorySpy();
		divaImporter = new EducationalProgramImporter(recordReader, fromClassicToCoraConverter,
				recordCreator, toJsonConverterFactory);

	}

	@Test
	public void testCorrectReadFromDb() {
		divaImporter.importData();
		assertTrue(recordReader.readFromTableUsingConditionsWasCalled);
		assertEquals(recordReader.usedTableName, "subjectview");
		int subjectTypeUsedToSelect = (int) recordReader.usedConditions.get("subject_type");
		assertEquals(subjectTypeUsedToSelect, 56);
	}

	@Test
	public void testCorrectCallsToCoraDataConverter() {
		recordReader.numOfResultToReturn = 3;

		divaImporter.importData();
		assertEquals(fromClassicToCoraConverter.rowsFromDb.size(), 3);
		assertSame(fromClassicToCoraConverter.rowsFromDb.get(0),
				recordReader.returnedRowsFromDb.get(0));
		assertSame(fromClassicToCoraConverter.rowsFromDb.get(1),
				recordReader.returnedRowsFromDb.get(1));
		assertSame(fromClassicToCoraConverter.rowsFromDb.get(2),
				recordReader.returnedRowsFromDb.get(2));
	}

	@Test
	public void testCorrectCallToJsonConverter() {
		recordReader.numOfResultToReturn = 3;
		divaImporter.importData();

		List<ClientDataElement> elementsSentToJsonConverter = toJsonConverterFactory.clientDataElements;

		List<ClientDataGroup> returnedGroupsFromClassicConverter = fromClassicToCoraConverter.returnedDataGroups;
		assertSame(elementsSentToJsonConverter.get(0), returnedGroupsFromClassicConverter.get(0));
		assertSame(elementsSentToJsonConverter.get(1), returnedGroupsFromClassicConverter.get(1));
		assertSame(elementsSentToJsonConverter.get(2), returnedGroupsFromClassicConverter.get(2));
	}

	@Test
	public void testCorrectCallsToCreator() {
		recordReader.numOfResultToReturn = 3;
		divaImporter.importData();
		assertCorrectCallToCreatorUsingIndex(0);
		assertCorrectCallToCreatorUsingIndex(1);
		assertCorrectCallToCreatorUsingIndex(2);
	}

	private void assertCorrectCallToCreatorUsingIndex(int index) {
		assertEquals(recordCreator.usedTableNames.get(index), "educationalProgram");

		Map<String, Object> valuesForFirstInsert = recordCreator.usedValues.get(index);
		assertEquals(valuesForFirstInsert.get("record_type"), "educationalProgram");
		assertEquals(valuesForFirstInsert.get("record_id"), "educationalProgram:" + index);
		DataToJsonConverterSpy jsonConverter = (DataToJsonConverterSpy) toJsonConverterFactory.factoredConverters
				.get(index);
		JsonObjectBuilderSpy jsonObjectBuilderSpy = jsonConverter.returnedJsonObjectBuilderSpy;
		assertEquals(valuesForFirstInsert.get("record"), jsonObjectBuilderSpy.returnedJsonObject);
	}

}
