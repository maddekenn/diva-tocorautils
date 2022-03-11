package se.uu.ub.cora.diva.tocorautils;

import java.util.List;

import se.uu.ub.cora.sqldatabase.DatabaseFacade;
import se.uu.ub.cora.sqldatabase.Row;

public class DatabaseFacadeSpy implements DatabaseFacade {

	@Override
	public List<Row> readUsingSqlAndValues(String sql, List<Object> values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Row readOneRowOrFailUsingSqlAndValues(String sql, List<Object> values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int executeSqlWithValues(String sql, List<Object> values) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void startTransaction() {
		// TODO Auto-generated method stub

	}

	@Override
	public void endTransaction() {
		// TODO Auto-generated method stub

	}

	@Override
	public void rollback() {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
