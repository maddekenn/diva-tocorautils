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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.sqldatabase.RecordReader;

public class RecordReaderSubjectSpy implements RecordReader {
	public boolean readFromTableUsingConditionsWasCalled = false;
	public String usedTableName = "";
	public Map<String, Object> usedConditions = new HashMap<>();
	public int numOfResultToReturn = 1;
	public List<Map<String, Object>> returnedRowsFromDb = new ArrayList<>();

	@Override
	public List<Map<String, Object>> readAllFromTable(String tableName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> readFromTableUsingConditions(String tableName,
			Map<String, Object> conditions) {
		readFromTableUsingConditionsWasCalled = true;
		usedTableName = tableName;
		usedConditions = conditions;
		for (int i = 0; i < numOfResultToReturn; i++) {
			Map<String, Object> row = new HashMap<>();
			row.put("subject_id", i);
			returnedRowsFromDb.add(row);
		}
		return returnedRowsFromDb;
	}

	@Override
	public Map<String, Object> readOneRowFromDbUsingTableAndConditions(String tableName,
			Map<String, Object> conditions) {
		return null;
	}

	@Override
	public Map<String, Object> readNextValueFromSequence(String sequenceName) {
		// TODO Auto-generated method stub
		return null;
	}

}
