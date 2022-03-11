package se.uu.ub.cora.diva.tocorautils;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.diva.tocorautils.convert.FromDbToCoraConverterFactory;
import se.uu.ub.cora.diva.tocorautils.importing.DbToCoraTransformer;
import se.uu.ub.cora.sqldatabase.DatabaseFacade;

public class DbToCoraTransformerSpy implements DbToCoraTransformer {

	public DatabaseFacade databaseFacade;
	public FromDbToCoraConverterFactory converterFactory;
	public List<ClientDataGroup> listOfConverted = new ArrayList<>();

	public DbToCoraTransformerSpy() {
	}

	public DbToCoraTransformerSpy(DatabaseFacade databaseFacade,
			FromDbToCoraConverterFactory converterFactory) {
		this.databaseFacade = databaseFacade;
		this.converterFactory = converterFactory;
	}

	public static DbToCoraTransformerSpy usingDatabaseFacadeAndFromDbConverterFactory(
			DatabaseFacade databaseFacade, FromDbToCoraConverterFactory converterFactory) {
		return new DbToCoraTransformerSpy(databaseFacade, converterFactory);
	}

	@Override
	public List<ClientDataGroup> getConverted() {
		listOfConverted.add(ClientDataGroup.withNameInData("someNameInData"));
		return listOfConverted;
	}

}
