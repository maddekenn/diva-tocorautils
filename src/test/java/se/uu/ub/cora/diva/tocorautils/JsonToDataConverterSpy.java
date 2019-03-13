package se.uu.ub.cora.diva.tocorautils;

import se.uu.ub.cora.clientdata.ClientDataElement;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataConverter;

public class JsonToDataConverterSpy implements JsonToDataConverter {

	public ClientDataGroup dataGroup;

	public JsonToDataConverterSpy(String json) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public ClientDataElement toInstance() {
		dataGroup = ClientDataGroup.withNameInData("someDataGroupFromSpy");
		return dataGroup;
	}

}
