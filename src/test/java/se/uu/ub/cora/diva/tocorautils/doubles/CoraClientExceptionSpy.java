/*
 * Copyright 2018 Uppsala University Library
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
package se.uu.ub.cora.diva.tocorautils.doubles;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.javaclient.cora.CoraClient;
import se.uu.ub.cora.javaclient.cora.CoraClientException;

public class CoraClientExceptionSpy implements CoraClient {

	public List<String> readRecordTypes = new ArrayList<>();
	public List<String> createdRecordTypes = new ArrayList<>();
	public List<String> updatedRecordTypes = new ArrayList<>();
	public List<String> updatedRecordIds = new ArrayList<>();
	public List<String> jsonStrings = new ArrayList<>();
	public List<ClientDataGroup> dataGroupsSentToUpdate = new ArrayList<>();
	public List<ClientDataGroup> dataGroupsSentToCreate = new ArrayList<>();
	public String jsonToReturn;
	public String recordTypeToList;
	public List<ClientDataRecord> listToReturn;
	public List<String> deletedRecordTypes = new ArrayList<>();
	public List<String> deletedRecordIds = new ArrayList<>();
	public int numberOfDeleted = 0;

	@Override
	public String create(String recordType, String json) {
		return "";
	}

	@Override
	public String read(String recordType, String recordId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String update(String recordType, String recordId, String json) {
		return null;
	}

	@Override
	public String delete(String recordType, String recordId) {
		numberOfDeleted++;
		deletedRecordTypes.add(recordType);
		deletedRecordIds.add(recordId);
		if (numberOfDeleted == 2) {
			throw new CoraClientException("Error deleting from spy");
		}
		return "delete response from spy, recordType: " + recordType + " recordId: " + recordId;
	}

	@Override
	public String readList(String recordType) {
		return "";
	}

	@Override
	public String readIncomingLinks(String recordType, String recordId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String create(String recordType, ClientDataGroup dataGroup) {
		return null;
	}

	@Override
	public ClientDataRecord readAsDataRecord(String recordType, String recordId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String update(String recordType, String recordId, ClientDataGroup dataGroup) {
		return null;
	}

	@Override
	public String indexData(ClientDataRecord arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String indexData(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ClientDataRecord> readListAsDataRecords(String recordType) {
		recordTypeToList = recordType;
		listToReturn = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			ClientDataRecord dataRecord = createDataRecord(i);
			listToReturn.add(dataRecord);
		}

		return listToReturn;
	}

	private ClientDataRecord createDataRecord(int index) {
		ClientDataGroup dataGroup = ClientDataGroup.withNameInData("someType");
		ClientDataGroup recordInfo = ClientDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(ClientDataAtomic.withNameInDataAndValue("id", "someId" + index));
		dataGroup.addChild(recordInfo);
		ClientDataRecord dataRecord = ClientDataRecord.withClientDataGroup(dataGroup);
		return dataRecord;
	}

	@Override
	public String removeFromIndex(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String indexDataWithoutExplicitCommit(String recordType, String recordId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String indexRecordsOfType(String recordType, String settingsAsJson) {
		// TODO Auto-generated method stub
		return null;
	}

}
