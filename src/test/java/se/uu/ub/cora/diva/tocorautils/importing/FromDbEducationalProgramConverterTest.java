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

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataGroup;

public class FromDbEducationalProgramConverterTest {

	// ska också innehålla id
	// {
	// "name": "educationalProgram",
	// "children": [
	// {
	// "name": "recordInfo",
	// "children": [
	// {
	// "name": "dataDivider",
	// "children": [
	// {
	// "name": "linkedRecordType",
	// "value": "system"
	// },
	// {
	// "name": "linkedRecordId",
	// "value": "diva"
	// }
	// ]
	// }
	// ]
	// },
	// {
	// "name": "name",
	// "children": [
	// {
	// "name": "educationalProgramName",
	// "value": ""
	// },
	// {
	// "name": "language",
	// "value": "sv"
	// }
	// ]
	// },
	// {
	// "name": "alternativeName",
	// "children": [
	// {
	// "name": "educationalProgramName",
	// "value": ""
	// },
	// {
	// "name": "language",
	// "value": "en"
	// }
	// ]
	// },
	// {
	// "name": "domain",
	// "value": ""
	// },
	// {
	// "name": "eligible",
	// "value": ""
	// }
	// ]
	// }

	private String expectedJson = "{\"name\":\"educationalProgram\",\"children\":[{\"name\":\"recordInfo\",\"children\":[{\"name\":\"dataDivider\",\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"diva\"}]},{\"name\":\"id\",\"value\":\"educationalProgram:200\"}]}]}";

	@Test
	public void testInit() {
		FromDbToCoraConverter converter = new FromDbEducationalProgramConverter();
		Map<String, Object> rowFromDb = new HashMap<>();

		rowFromDb.put("subject_id", 200);
		ClientDataGroup dataGroup = converter.convertToDataGroupFromRowFromDb(rowFromDb);
		assertEquals(dataGroup.getNameInData(), "educationalProgram");
		ClientDataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");
		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), "educationalProgram:200");

		assertContainsCorrectLink(recordInfo, "type", "recordType", "educationalProgram");
		assertContainsCorrectLink(recordInfo, "dataDivider", "system", "diva");
		assertContainsCorrectLink(recordInfo, "createdBy", "user", "coraUser:44129824028536262");

		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("tsCreated"),
				"2020-10-01 00:00:00.000");

		ClientDataGroup updatedGroup = recordInfo.getFirstGroupWithNameInData("updated");
		assertContainsCorrectLink(updatedGroup, "updatedBy", "user", "coraUser:44129824028536262");
		assertEquals(updatedGroup.getFirstAtomicValueWithNameInData("tsUpdated"),
				"2020-10-01 00:00:00.000");

		// assertEquals(jsonRecord.recordType, "educationalProgram");
		// assertEquals(jsonRecord.recordId, "educationalProgram:200");

	}

	private void assertContainsCorrectLink(ClientDataGroup recordInfo, String linkName,
			String linkedRecordType, String linkedRecordId) {
		ClientDataGroup link = recordInfo.getFirstGroupWithNameInData(linkName);
		assertEquals(link.getFirstAtomicValueWithNameInData("linkedRecordType"), linkedRecordType);
		assertEquals(link.getFirstAtomicValueWithNameInData("linkedRecordId"), linkedRecordId);
	}

}
