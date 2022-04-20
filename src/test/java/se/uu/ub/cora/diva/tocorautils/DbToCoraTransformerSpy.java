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
	public String type;

	public DbToCoraTransformerSpy() {
	}

	private DbToCoraTransformerSpy(String fileName, FromDbToCoraConverterFactory converterFactory,
			String type) {
		this.fileName = fileName;
		this.converterFactory = converterFactory;
		this.type = type;

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

	public static DbToCoraTransformerSpy usingFilePathConverterFactoryAndType(String fileName,
			FromDbToCoraConverterFactory converterFactory, String type) {
		return new DbToCoraTransformerSpy(fileName, converterFactory, type);
	}

	@Override
	public List<ClientDataGroup> getConverted() {
		ClientDataGroup dataGroup = createDataGroup("someIdFromSpy");
		listOfConverted.add(dataGroup);
		ClientDataGroup dataGroup2 = createDataGroup("someOtherIdFromSpy");
		listOfConverted.add(dataGroup2);
		return listOfConverted;
	}

	private ClientDataGroup createDataGroup(String id) {
		ClientDataGroup dataGroup = ClientDataGroup.withNameInData("someNameInData");
		ClientDataGroup recordInfo = ClientDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(ClientDataAtomic.withNameInDataAndValue("id", id));
		dataGroup.addChild(recordInfo);
		return dataGroup;
	}

}
