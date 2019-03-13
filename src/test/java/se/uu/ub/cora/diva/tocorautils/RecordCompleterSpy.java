package se.uu.ub.cora.diva.tocorautils;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.diva.tocorautils.convert.RecordCompleter;

public class RecordCompleterSpy implements RecordCompleter {

	public int completeMetadataCalledNumOfTimes = 0;
	public List<ClientDataGroup> dataGroups = new ArrayList<>();

	@Override
	public String completeMetadata(ClientDataGroup dataGroup) {
		completeMetadataCalledNumOfTimes++;
		dataGroups.add(dataGroup);
		return null;
	}

}
