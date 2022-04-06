/*
 * Copyright 2019 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or @Override
	FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.diva.tocorautils.convert;

import java.util.Map;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.diva.tocorautils.CoraJsonRecord;

/**
 * FromDbToCoraConverter convertes a row from db to a Cora representation of the data. An instance
 * of FromDbToCoraConverter is expected to only handle one row from database. If multiple rows are
 * to be converted, multiple FromDbToCoraConverters should be used.
 * 
 */
public interface FromDbToCoraConverter {

	CoraJsonRecord convertToJsonFromRowFromDb(Map<String, Object> rowFromDb);

	ClientDataGroup convertToClientDataGroupFromRowFromDb(Map<String, Object> row);

}
