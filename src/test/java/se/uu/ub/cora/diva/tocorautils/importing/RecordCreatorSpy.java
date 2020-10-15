/*
 * Copyright 2020 Uppsala University Library
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
package se.uu.ub.cora.diva.tocorautils.importing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.sqldatabase.DataUpdater;
import se.uu.ub.cora.sqldatabase.RecordCreator;

public class RecordCreatorSpy implements RecordCreator {

	public List<String> usedTableNames = new ArrayList<>();
	public List<Map<String, Object>> usedValues = new ArrayList<>();
	public DataUpdater dataUpdater;

	public RecordCreatorSpy() {
	}

	public RecordCreatorSpy(DataUpdater dataUpdater) {
		this.dataUpdater = dataUpdater;
	}

	public static RecordCreator usingDataUpdater(DataUpdater dataUpdater) {
		return new RecordCreatorSpy(dataUpdater);
	}

	@Override
	public DataUpdater getDataUpdater() {

		return dataUpdater;
	}

	@Override
	public void insertIntoTableUsingNameAndColumnsWithValues(String tableName,
			Map<String, Object> values) {
		usedTableNames.add(tableName);
		usedValues.add(values);
	}

}
