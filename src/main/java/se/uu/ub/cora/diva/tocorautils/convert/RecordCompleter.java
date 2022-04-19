package se.uu.ub.cora.diva.tocorautils.convert;

import java.util.List;

import se.uu.ub.cora.clientdata.ClientDataGroup;

public interface RecordCompleter {

	List<ClientDataGroup> completeMetadata(List<ClientDataGroup> dataGroups, String pathToFile);
}
