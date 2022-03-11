package se.uu.ub.cora.diva.tocorautils.importing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import se.uu.ub.cora.sqldatabase.DatabaseValues;
import se.uu.ub.cora.sqldatabase.Row;

public class RowSpy implements Row {

	public Map<String, Object> columnValues = new HashMap<>();
	public List<String> reuqestedColumnNames = new ArrayList<>();

	@Override
	public Object getValueByColumn(String columnName) {
		reuqestedColumnNames.add(columnName);
		return columnValues.get(columnName);
	}

	@Override
	public Set<String> columnSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasColumn(String columnName) {
		return columnValues.containsKey(columnName);
	}

	@Override
	public boolean hasColumnWithNonEmptyValue(String columnName) {
		if (hasColumn(columnName)) {
			return columnHasValue(columnName);
		}
		return false;
	}

	private boolean columnHasValue(String columnName) {
		Object value = columnValues.get(columnName);
		return (DatabaseValues.NULL != value && !isStringWithoutValue(value));
	}

	private boolean isStringWithoutValue(Object value) {
		// using longer version as we do not get full coverage with short version
		// return (value instanceof String stringValue && stringValue.isBlank());
		if (value instanceof String) {
			String stringValue = (String) value;
			return stringValue.isBlank();
		}
		return false;
	}

}
