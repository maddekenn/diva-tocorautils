package se.uu.ub.cora.diva.tocorautils;

import se.uu.ub.cora.batchrunner.JsonToClientData;
import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataList;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataConverterFactory;
import se.uu.ub.cora.json.parser.JsonParser;

public class JsonToClientDataSpy implements JsonToClientData {

	public String jsonListToConvert;
	public int numToReturn = 2;
	public ClientDataList dataList;
	public JsonParser jsonParser;
	public JsonToDataConverterFactory converterFactory;

	public JsonToClientDataSpy(JsonParser jsonParser, JsonToDataConverterFactory converterFactory) {
		this.jsonParser = jsonParser;
		this.converterFactory = converterFactory;
	}

	public JsonToClientDataSpy() {
		// TODO Auto-generated constructor stub
	}

	public static JsonToClientDataSpy usingJsonParserAndConverterFactory(JsonParser jsonParser,
			JsonToDataConverterFactory converterFactory) {
		return new JsonToClientDataSpy(jsonParser, converterFactory);
	}

	@Override
	public ClientDataList getJsonStringAsClientDataRecordList(String jsonListToConvert) {
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
