package se.uu.ub.cora.diva.tocorautils;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.diva.tocorautils.convert.RecordCompleter;

public class RecordCompleterSpy implements RecordCompleter {
	public int completeMetadataCalledNumOfTimes = 0;
	public List<ClientDataGroup> dataGroups = new ArrayList<>();
	public String jsonToReturn = "some dummy json from recordCompleterSpy";
	public List<ClientDataGroup> completedToReturn = new ArrayList<>();
	public String pathToFile;

	public static RecordCompleterSpy usingPathToFile(String pathToFile) {
		return new RecordCompleterSpy(pathToFile);
	}

	private RecordCompleterSpy(String pathToFile) {
		this.pathToFile = pathToFile;
	}

	@Override
	public List<ClientDataGroup> completeMetadata(List<ClientDataGroup> dataGroups) {
		completeMetadataCalledNumOfTimes++;
		this.dataGroups = dataGroups;
		return dataGroups;
	}

}
