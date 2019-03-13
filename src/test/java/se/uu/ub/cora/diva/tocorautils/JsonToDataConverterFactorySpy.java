package se.uu.ub.cora.diva.tocorautils;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataActionLinkConverter;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataConverter;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataConverterFactory;
import se.uu.ub.cora.json.parser.JsonValue;

public class JsonToDataConverterFactorySpy implements JsonToDataConverterFactory {

	public List<String> jsonStrings = new ArrayList<>();
	public List<JsonToDataConverterSpy> factoredConverters = new ArrayList<>();

	@Override
	public JsonToDataConverter createForJsonObject(JsonValue jsonValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonToDataConverter createForJsonString(String json) {
		jsonStrings.add(json);
		JsonToDataConverterSpy factoredConverter = new JsonToDataConverterSpy(json);
		factoredConverters.add(factoredConverter);
		return factoredConverter;
	}

	@Override
	public JsonToDataActionLinkConverter createActionLinksConverterForJsonString(String json) {
		return null;
	}

	@Override
	public JsonToDataActionLinkConverter createJsonToDataActionLinkConverterForJsonObject(
			JsonValue jsonValue) {
		// TODO Auto-generated method stub
		return null;
	}

}
