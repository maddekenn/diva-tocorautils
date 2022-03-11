package se.uu.ub.cora.diva.tocorautils;

import se.uu.ub.cora.sqldatabase.DatabaseFacade;
import se.uu.ub.cora.sqldatabase.SqlDatabaseFactory;
import se.uu.ub.cora.sqldatabase.table.TableFacade;
import se.uu.ub.cora.sqldatabase.table.TableQuery;

public class SqlDatabaseFactorySpy implements SqlDatabaseFactory {

	public String url;
	public String user;
	public String password;
	public DatabaseFacadeSpy factoredDatabaseFacade;

	public SqlDatabaseFactorySpy(String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;
	}

	public static SqlDatabaseFactorySpy usingUriAndUserAndPassword(String url, String user,
			String password) {
		return new SqlDatabaseFactorySpy(url, user, password);
	}

	@Override
	public DatabaseFacade factorDatabaseFacade() {
		factoredDatabaseFacade = new DatabaseFacadeSpy();
		return factoredDatabaseFacade;
	}

	@Override
	public TableFacade factorTableFacade() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TableQuery factorTableQuery(String tableName) {
		// TODO Auto-generated method stub
		return null;
	}

}
