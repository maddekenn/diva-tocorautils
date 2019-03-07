/*
 * Copyright 2018, 2019 Uppsala University Library
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
package se.uu.ub.cora.diva.tocorautils.doubles;

import java.util.List;
import java.util.Map;

import se.uu.ub.cora.tocorautils.CoraJsonRecord;
import se.uu.ub.cora.tocorautils.importing.ImportResult;
import se.uu.ub.cora.tocorautils.importing.Importer;

public class CoraImporterSpy implements Importer {

	public List<Map<String, String>> convertedRows;
	public List<List<CoraJsonRecord>> listOfConvertedRows;
	private ImportResult importResult;

	@Override
	public ImportResult createInCora(List<List<CoraJsonRecord>> listOfConvertedRows) {
		this.listOfConvertedRows = listOfConvertedRows;
		importResult = new ImportResult();
		importResult.listOfFails.add("failure from CoraImporterSpy");
		return importResult;
	}

}
