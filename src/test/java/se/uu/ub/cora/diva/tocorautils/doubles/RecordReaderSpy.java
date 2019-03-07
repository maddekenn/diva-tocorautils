package se.uu.ub.cora.diva.tocorautils.doubles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.sqldatabase.RecordReader;

public class RecordReaderSpy implements RecordReader {

	public String usedTableName = "";
	public List<Map<String, String>> returnedListForReadAll;
	public List<Map<String, String>> returnedListForReadOne;
	public List<Map<String, String>> returnedListForReadAllWithConditions;
	public int noOfRecordsToReturn = 1;
	public Map<String, String> usedConditions = new HashMap<>();
	public List<String> usedTableNames = new ArrayList<>();
	public List<String> idsToReturnParent = new ArrayList<>();

	@Override
	public List<Map<String, String>> readAllFromTable(String tableName) {
		usedTableName = tableName;
		usedTableNames.add(usedTableName);
		returnedListForReadAll = new ArrayList<>();
		for (int i = 0; i < noOfRecordsToReturn; i++) {
			Map<String, String> map = new HashMap<>();
			map.put("subject_id", "someId" + i);
			map.put("someKey" + i, "someValue" + i);
			returnedListForReadAll.add(map);
		}
		return returnedListForReadAll;
	}

	@Override
	public Map<String, String> readOneRowFromDbUsingTableAndConditions(String tableName,
			Map<String, String> conditions) {
		usedTableName = tableName;
		usedTableNames.add(tableName);
		usedConditions = conditions;
		Map<String, String> map = new HashMap<>();
		map.put("someKey", "someValue");
		returnedListForReadOne = new ArrayList<>();
		returnedListForReadOne.add(map);
		return map;

	}

	@Override
	public List<Map<String, String>> readFromTableUsingConditions(String tableName,
			Map<String, String> conditions) {
		usedConditions = conditions;
		usedTableNames.add(tableName);
		returnedListForReadAllWithConditions = new ArrayList<>();
		if (idsToReturnParent.contains(conditions.get("subject_id"))) {
			for (int i = 0; i < noOfRecordsToReturn; i++) {
				Map<String, String> map = new HashMap<>();
				map.put("subject_id", "someSubjectId" + i);
				map.put("parent_id", "someParent" + i);
				returnedListForReadAllWithConditions.add(map);
			}
		}
		return returnedListForReadAllWithConditions;
	}

}
