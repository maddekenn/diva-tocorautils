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
package se.uu.ub.cora.diva.tocorautils;

import se.uu.ub.cora.batchrunner.JsonToClientData;
import se.uu.ub.cora.clientdata.ClientData;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataList;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataConverterFactory;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataRecordConverterImp;
import se.uu.ub.cora.javaclient.cora.CoraClient;

public class UpdaterImp implements Updater {

	private CoraClient coraClient;
	private JsonToClientData jsonToClientData;
	private DataGroupChangerFactory changerFactory;
	private JsonToDataConverterFactory jsonToDataFactory;

	public static UpdaterImp usingCoraClientJsonToClientDataChangerFactoryAndConverterFactory(
			CoraClient coraClient, JsonToClientData jsonToClientData,
			DataGroupChangerFactory changerFactory, JsonToDataConverterFactory jsonToDataFactory) {
		return new UpdaterImp(coraClient, jsonToClientData, changerFactory, jsonToDataFactory);
	}

	private UpdaterImp(CoraClient coraClient, JsonToClientData jsonToClientData,
			DataGroupChangerFactory changerFactory, JsonToDataConverterFactory jsonToDataFactory) {
		this.coraClient = coraClient;
		this.jsonToClientData = jsonToClientData;
		this.changerFactory = changerFactory;
		this.jsonToDataFactory = jsonToDataFactory;
	}

	@Override
	public void update(String type) {
		String jsonListToConvert = coraClient.readList(type);
		JsonToDataRecordConverterImp jsonToDataRecordConverter = new JsonToDataRecordConverterImp(
				jsonToDataFactory);

		ClientDataList dataList = jsonToClientData
				.getJsonStringAsClientDataRecordList(jsonToDataRecordConverter, jsonListToConvert);
		updateDataGroups(type, dataList);
	}

	private void updateDataGroups(String type, ClientDataList dataList) {
		DataGroupChanger changer = changerFactory.factor(type);
		for (ClientData clientData : dataList.getDataList()) {
			updateDataGroup(changer, clientData, type);
		}
	}

	private void updateDataGroup(DataGroupChanger changer, ClientData clientData, String type) {
		ClientDataGroup clientDataGroup = getDataGroup(clientData);
		String id = extractId(clientDataGroup);
		ClientDataGroup modifiedGroup = changer.change(clientDataGroup);
		coraClient.update(type, id, modifiedGroup);
	}

	private ClientDataGroup getDataGroup(ClientData clientData) {
		ClientDataRecord clientDataRecord = (ClientDataRecord) clientData;
		return clientDataRecord.getClientDataGroup();
	}

	private String extractId(ClientDataGroup clientDataGroup) {
		ClientDataGroup recordInfo = clientDataGroup.getFirstGroupWithNameInData("recordInfo");
		return recordInfo.getFirstAtomicValueWithNameInData("id");
	}

}
