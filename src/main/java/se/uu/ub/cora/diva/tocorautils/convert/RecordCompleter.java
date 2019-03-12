package se.uu.ub.cora.diva.tocorautils.convert;

import se.uu.ub.cora.clientdata.ClientDataGroup;

public interface RecordCompleter {

	ClientDataGroup completeMetadata(ClientDataGroup dataGroup);
}
