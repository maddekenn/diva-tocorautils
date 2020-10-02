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
		int subjectId = (int) rowFromDb.get("subject_id");
		// return new CoraJsonRecord("educationalProgram",
		// "educationalProgram:" + String.valueOf(subjectId), "");
		ClientDataGroup dataGroup = ClientDataGroup.withNameInData("educationalProgram");
		createRecordInfo(subjectId, dataGroup);
		return dataGroup;
	}

	private void createRecordInfo(int subjectId, ClientDataGroup dataGroup) {
		ClientDataGroup recordInfo = ClientDataGroup.withNameInData("recordInfo");
		ClientDataAtomic id = ClientDataAtomic.withNameInDataAndValue("id",
				"educationalProgram:" + String.valueOf(subjectId));
		recordInfo.addChild(id);

		ClientDataGroup type = ClientDataGroup.asLinkWithNameInDataAndTypeAndId("type",
				"recordType", "educationalProgram");
		recordInfo.addChild(type);

		ClientDataGroup dataDivider = ClientDataGroup
				.asLinkWithNameInDataAndTypeAndId("dataDivider", "system", "diva");
		ClientDataGroup createdBy = createCreatedByUsingUserId();
		recordInfo.addChild(createdBy);
		recordInfo.addChild(dataDivider);
		recordInfo.addChild(createTsCreated());
		recordInfo.addChild(createUpdatedInfoUsingUserId());
		dataGroup.addChild(recordInfo);
	}

	private static ClientDataGroup createCreatedByUsingUserId() {
		return ClientDataGroup.asLinkWithNameInDataAndTypeAndId("createdBy", "user",
				"coraUser:44129824028536262");
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

	public static ClientDataGroup createUpdatedInfoUsingUserId() {
		ClientDataGroup updated = ClientDataGroup.withNameInData("updated");
		updated.setRepeatId("0");
		ClientDataGroup updatedBy = ClientDataGroup.asLinkWithNameInDataAndTypeAndId("updatedBy",
				"user", "coraUser:44129824028536262");
		updated.addChild(updatedBy);
		updated.addChild(ClientDataAtomic.withNameInDataAndValue("tsUpdated",
				getPredefinedTimestampAsString()));
		return updated;
	}
}
