package se.uu.ub.cora.diva.tocorautils;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.diva.tocorautils.convert.FromDbToCoraConverterFactory;
import se.uu.ub.cora.diva.tocorautils.importing.DbToCoraTransformer;
import se.uu.ub.cora.sqldatabase.DatabaseFacade;

public class DbToCoraTransformerSpy implements DbToCoraTransformer {

	public DatabaseFacade databaseFacade;
	public FromDbToCoraConverterFactory converterFactory;
	public List<ClientDataGroup> listOfConverted = new ArrayList<>();
	public String fileName;

	public DbToCoraTransformerSpy() {
	}

	private DbToCoraTransformerSpy(String fileName, FromDbToCoraConverterFactory converterFactory) {
		this.fileName = fileName;
		this.converterFactory = converterFactory;

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

	public static DbToCoraTransformerSpy usingFilePathAndConverterFactory(String fileName,
			FromDbToCoraConverterFactory converterFactory) {
		return new DbToCoraTransformerSpy(fileName, converterFactory);
	}

	@Override
	public List<ClientDataGroup> getConverted() {
		ClientDataGroup dataGroup = ClientDataGroup.withNameInData("someNameInData");
		ClientDataGroup recordInfo = ClientDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(ClientDataAtomic.withNameInDataAndValue("id", "someIdFromSpy"));
		dataGroup.addChild(recordInfo);
		listOfConverted.add(dataGroup);
		return listOfConverted;
	}

}
