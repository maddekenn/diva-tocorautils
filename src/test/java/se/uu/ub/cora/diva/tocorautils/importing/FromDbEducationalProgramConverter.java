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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;

public class FromDbEducationalProgramConverter implements FromDbToCoraConverter {

	@Override
	public ClientDataGroup convertToDataGroupFromRowFromDb(Map<String, Object> rowFromDb) {
		ClientDataGroup dataGroup = ClientDataGroup.withNameInData("educationalProgram");
		createAndAddRecordInfo(rowFromDb, dataGroup);
		createAndAddName(rowFromDb, dataGroup);
		createAndAddDomain(rowFromDb, dataGroup);
		createAndAddEligible(rowFromDb, dataGroup);
		return dataGroup;
	}

	private void createAndAddRecordInfo(Map<String, Object> rowFromDb, ClientDataGroup dataGroup) {
		int subjectId = (int) rowFromDb.get("subject_id");
		ClientDataGroup recordInfo = createBasicRecordInfo(subjectId);
		addCreateInfo(recordInfo);
		addUpdatedInfo(recordInfo);
		dataGroup.addChild(recordInfo);
	}

	private ClientDataGroup createBasicRecordInfo(int subjectId) {
		ClientDataGroup recordInfo = ClientDataGroup.withNameInData("recordInfo");
		createAndAddId(subjectId, recordInfo);
		createAndAddType(recordInfo);
		createAndAddDataDivider(recordInfo);
		return recordInfo;
	}

	private void createAndAddId(int subjectId, ClientDataGroup recordInfo) {
		ClientDataAtomic id = ClientDataAtomic.withNameInDataAndValue("id",
				"educationalProgram:" + String.valueOf(subjectId));
		recordInfo.addChild(id);
	}

	private void createAndAddType(ClientDataGroup recordInfo) {
		ClientDataGroup type = ClientDataGroup.asLinkWithNameInDataAndTypeAndId("type",
				"recordType", "educationalProgram");
		recordInfo.addChild(type);
	}

	private void createAndAddDataDivider(ClientDataGroup recordInfo) {
		ClientDataGroup dataDivider = ClientDataGroup
				.asLinkWithNameInDataAndTypeAndId("dataDivider", "system", "diva");
		recordInfo.addChild(dataDivider);
	}

	private void addCreateInfo(ClientDataGroup recordInfo) {
		ClientDataGroup createdBy = createCreatedByUsingUserId();
		recordInfo.addChild(createdBy);
		recordInfo.addChild(createTsCreated());
	}

	private static ClientDataGroup createCreatedByUsingUserId() {
		return ClientDataGroup.asLinkWithNameInDataAndTypeAndId("createdBy", "user",
				"coraUser:4412982402853626");
	}

	public static ClientDataAtomic createTsCreated() {
		return ClientDataAtomic.withNameInDataAndValue("tsCreated",
				getPredefinedTimestampAsString());
	}

	private static String getPredefinedTimestampAsString() {
		LocalDateTime localDateTime = LocalDateTime.of(2020, 10, 01, 00, 00, 00, 0);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
		return localDateTime.format(formatter);
	}

	public static void addUpdatedInfo(ClientDataGroup recordInfo) {
		ClientDataGroup updated = ClientDataGroup.withNameInData("updated");
		updated.setRepeatId("0");
		createAndAddUpdatedBy(updated);
		updated.addChild(ClientDataAtomic.withNameInDataAndValue("tsUpdated",
				getPredefinedTimestampAsString()));
		recordInfo.addChild(updated);
	}

	private static void createAndAddUpdatedBy(ClientDataGroup updated) {
		ClientDataGroup updatedBy = ClientDataGroup.asLinkWithNameInDataAndTypeAndId("updatedBy",
				"user", "coraUser:4412982402853626");
		updated.addChild(updatedBy);
	}

	private void createAndAddDomain(Map<String, Object> rowFromDb, ClientDataGroup dataGroup) {
		dataGroup.addChild(ClientDataAtomic.withNameInDataAndValue("domain",
				(String) rowFromDb.get("domain")));
	}

	private void createAndAddName(Map<String, Object> rowFromDb, ClientDataGroup dataGroup) {
		createAndAddDefaultNameGroup(rowFromDb, dataGroup);
		createAndAddAlternativeNameGroup(rowFromDb, dataGroup);
	}

	private void createAndAddEligible(Map<String, Object> rowFromDb, ClientDataGroup dataGroup) {
		boolean invertedNotEligible = "false".equals(rowFromDb.get("not_eligible")) ? true : false;
		dataGroup.addChild(ClientDataAtomic.withNameInDataAndValue("eligible",
				String.valueOf(invertedNotEligible)));
	}

	private void createAndAddDefaultNameGroup(Map<String, Object> rowFromDb,
			ClientDataGroup dataGroup) {
		ClientDataGroup nameGroup = ClientDataGroup.withNameInData("name");
		nameGroup.addChild(ClientDataAtomic.withNameInDataAndValue("language", "sv"));
		String name = (String) rowFromDb.get("defaultname");
		nameGroup.addChild(ClientDataAtomic.withNameInDataAndValue("educationalProgramName", name));
		dataGroup.addChild(nameGroup);
	}

	private void createAndAddAlternativeNameGroup(Map<String, Object> rowFromDb,
			ClientDataGroup dataGroup) {
		ClientDataGroup nameGroup = ClientDataGroup.withNameInData("alternativeName");
		nameGroup.addChild(ClientDataAtomic.withNameInDataAndValue("language", "en"));
		String name = (String) rowFromDb.get("alternativename");
		nameGroup.addChild(ClientDataAtomic.withNameInDataAndValue("educationalProgramName", name));
		dataGroup.addChild(nameGroup);
	}

}
