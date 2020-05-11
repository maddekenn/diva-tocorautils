package se.uu.ub.cora.diva.tocorautils.doubles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.sqldatabase.RecordReader;

public class RecordReaderSpy implements RecordReader {

	public String usedTableName = "";
	public List<Map<String, Object>> returnedListForReadAll;
	public List<Map<String, Object>> returnedListForReadOne;
	public List<Map<String, Object>> returnedListForReadAllWithConditions;
	public int noOfRecordsToReturn = 1;
	public Map<String, Object> usedConditions = new HashMap<>();
	public List<String> usedTableNames = new ArrayList<>();
	public List<String> idsToReturnParent = new ArrayList<>();
	public int noOfParentsToReturn = 1;

	@Override
	public List<Map<String, Object>> readAllFromTable(String tableName) {
		usedTableName = tableName;
		usedTableNames.add(usedTableName);
		returnedListForReadAll = new ArrayList<>();
		for (int i = 0; i < noOfRecordsToReturn; i++) {
			Map<String, Object> map = new HashMap<>();
			map.put("subject_id", "someId" + i);
			map.put("someKey" + i, "someValue" + i);
			returnedListForReadAll.add(map);
		}
		return returnedListForReadAll;
	}

	@Override
	public Map<String, Object> readOneRowFromDbUsingTableAndConditions(String tableName,
			Map<String, Object> conditions) {
		usedTableName = tableName;
		usedTableNames.add(tableName);
		usedConditions = conditions;
		Map<String, Object> map = new HashMap<>();
		map.put("someKey", "someValue");
		returnedListForReadOne = new ArrayList<>();
		returnedListForReadOne.add(map);
		return map;

	}

	@Override
	public List<Map<String, Object>> readFromTableUsingConditions(String tableName,
			Map<String, Object> conditions) {
		usedConditions = conditions;
		usedTableNames.add(tableName);
		returnedListForReadAllWithConditions = new ArrayList<>();
		if (idsToReturnParent.contains(conditions.get("parent_subject_id"))) {
			for (int i = 0; i < noOfParentsToReturn; i++) {
				Map<String, Object> map = new HashMap<>();
				map.put("subject_id", "someParent" + i);
				map.put("parent_subject_id", "someSubjectId" + i);
				returnedListForReadAllWithConditions.add(map);
			}
		}
		return returnedListForReadAllWithConditions;
	}

	@Override
	public Map<String, Object> readNextValueFromSequence(String sequenceName) {
		// TODO Auto-generated method stub
		return null;
	}

}
