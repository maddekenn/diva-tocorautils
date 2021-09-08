package se.uu.ub.cora.diva.tocorautils.importing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.sqldatabase.RecordReader;

public abstract class SubjectImporter {

	protected RecordReader recordReader;

	protected List<Map<String, Object>> readRowsFromDb() {
		Map<String, Object> conditions = createConditionsForRead();
		return recordReader.readFromTableUsingConditions("subjectview", conditions);
	}

	Map<String, Object> createConditionsForRead() {
		Map<String, Object> conditions = new HashMap<>();
		conditions.put("subject_type_id", getSubjectCode());
		return conditions;
	}

	protected abstract int getSubjectCode();

}
