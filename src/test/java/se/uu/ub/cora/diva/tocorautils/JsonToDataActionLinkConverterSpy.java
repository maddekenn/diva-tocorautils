package se.uu.ub.cora.diva.tocorautils;

import se.uu.ub.cora.clientdata.Action;
import se.uu.ub.cora.clientdata.ActionLink;
import se.uu.ub.cora.clientdata.ClientData;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataActionLinkConverter;

public class JsonToDataActionLinkConverterSpy implements JsonToDataActionLinkConverter {

	@Override
	public ClientData toInstance() {
		return ActionLink.withAction(Action.READ);
	}

}
