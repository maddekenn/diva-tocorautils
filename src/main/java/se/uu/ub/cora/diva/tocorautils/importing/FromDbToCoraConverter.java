package se.uu.ub.cora.diva.tocorautils.importing;

import java.util.Map;

import se.uu.ub.cora.clientdata.ClientDataGroup;

public interface FromDbToCoraConverter {

	ClientDataGroup convertToDataGroupFromRowFromDb(Map<String, Object> rowFromDb);

}
