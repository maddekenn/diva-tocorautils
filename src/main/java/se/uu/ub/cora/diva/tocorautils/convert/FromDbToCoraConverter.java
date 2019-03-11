package se.uu.ub.cora.diva.tocorautils.convert;

import java.util.Map;

import se.uu.ub.cora.tocorautils.CoraJsonRecord;

public interface FromDbToCoraConverter {

	CoraJsonRecord convertToJsonFromRowFromDb(Map<String, String> rowFromDb);

}
