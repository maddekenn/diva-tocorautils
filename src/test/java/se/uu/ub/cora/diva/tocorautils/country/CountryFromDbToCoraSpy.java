/*
 * Copyright 2018 Uppsala University Library
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
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.diva.tocorautils.country;

import se.uu.ub.cora.tocorautils.FromDbToCora;
import se.uu.ub.cora.tocorautils.importing.ImportResult;

public class CountryFromDbToCoraSpy implements FromDbToCora {

	public boolean importCountriesHasBeenCalled = false;
	public boolean returnErrors = false;
	public String usedTableName;

	@Override
	public ImportResult importFromTable(String tableName) {
		importCountriesHasBeenCalled = true;
		usedTableName = tableName;
		ImportResult result = new ImportResult();
		if (returnErrors) {
			result.listOfFails.add("failed during import in CountryFromDbToCoraSpy");
			result.listOfFails.add("failed again during import of CountryFromDbToCoraSpy");
		}
		return result;
	}

}
