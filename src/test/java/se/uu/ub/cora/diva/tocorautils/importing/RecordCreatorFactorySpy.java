package se.uu.ub.cora.diva.tocorautils.importing;

import se.uu.ub.cora.connection.SqlConnectionProvider;
import se.uu.ub.cora.sqldatabase.RecordCreator;
import se.uu.ub.cora.sqldatabase.RecordCreatorFactory;

public class RecordCreatorFactorySpy implements RecordCreatorFactory {

	public RecordCreatorSpy factored;

	@Override
	public SqlConnectionProvider getSqlConnectionProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RecordCreator factor() {
		factored = new RecordCreatorSpy();
		return factored;
	}

}
