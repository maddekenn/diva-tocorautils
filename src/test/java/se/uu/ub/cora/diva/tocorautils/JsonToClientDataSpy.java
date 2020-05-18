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
import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataList;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataConverterFactory;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataRecordConverter;
import se.uu.ub.cora.json.parser.JsonParser;

public class JsonToClientDataSpy implements JsonToClientData {

	public String jsonListToConvert;
	public int numToReturn = 2;
	public ClientDataList dataList;
	public JsonParser jsonParser;
	public JsonToDataConverterFactory converterFactory;
	public JsonToDataRecordConverter jsonToDataRecordConverter;

	public JsonToClientDataSpy(JsonParser jsonParser) {
		this.jsonParser = jsonParser;
	}

	public JsonToClientDataSpy() {
		// TODO Auto-generated constructor stub
	}

	public static JsonToClientDataSpy usingJsonParser(JsonParser jsonParser) {
		return new JsonToClientDataSpy(jsonParser);
	}

	@Override
	public ClientDataList getJsonStringAsClientDataRecordList(
			JsonToDataRecordConverter jsonToDataRecordConverter, String jsonListToConvert) {
		this.jsonToDataRecordConverter = jsonToDataRecordConverter;
		this.jsonListToConvert = jsonListToConvert;
		dataList = ClientDataList.withContainDataOfType("spyType");
		for (int i = 0; i < numToReturn; i++) {
			ClientDataGroup dataGroup = createDataGroup(i);
			ClientDataRecord dataRecord = ClientDataRecord.withClientDataGroup(dataGroup);
			dataList.addData(dataRecord);
		}

		return dataList;
	}

	private ClientDataGroup createDataGroup(int i) {
		ClientDataGroup dataGroup = ClientDataGroup.withNameInData("spyDataGroupAddedToList");
		ClientDataGroup recordInfo = ClientDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(ClientDataAtomic.withNameInDataAndValue("id", "idFromSpy" + i));
		dataGroup.addChild(recordInfo);
		return dataGroup;
	}

}
