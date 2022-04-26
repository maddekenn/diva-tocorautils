package se.uu.ub.cora.diva.tocorautils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;

import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.diva.tocorautils.doubles.CoraClientFactorySpy;
import se.uu.ub.cora.diva.tocorautils.doubles.CoraClientSpy;

public class DeleterRunnerTest {
	private String args[] = new String[] {
			"se.uu.ub.cora.diva.tocorautils.doubles.CoraClientFactorySpy",
			"someApptokenVerifierUrl", "someBaseUrl", "someAuthtoken", "someRecordType" };

	// "se.uu.ub.cora.diva.tocorautils.importing.FunderTransformer"
	// "se.uu.ub.cora.sqldatabase.SqlDatabaseFactoryImp"
	// "se.uu.ub.cora.diva.tocorautils.convert.FromDbToCoraConverterFactoryImp"
	// "jdbc:postgresql://dev-diva-postgresql:5432/diva" "diva" "diva"
	// "se.uu.ub.cora.javaclient.cora.CoraClientFactoryImp"
	// "http://ipnummer:38082/apptokenverifier/" "http://ipnummer:38082/diva/rest/" ""

	// [0] coraClientFactoryClassName
	// [1] apptokenVerifierUrl
	// [2] baseUrl
	// [3] authToken
	// [4] type

	@Test
	public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		Constructor<DeleterRunner> constructor = DeleterRunner.class.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		constructor.setAccessible(true);
		constructor.newInstance();
	}

	@Test
	public void testCoraClient()
			throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, IllegalArgumentException, InstantiationException {
		DeleterRunner.main(args);
		CoraClientFactorySpy coraClientFactory = (CoraClientFactorySpy) DeleterRunner.coraClientFactory;
		assertEquals(coraClientFactory.appTokenVerifierUrl, "someApptokenVerifierUrl");
		assertEquals(coraClientFactory.baseUrl, "someBaseUrl");
		assertEquals(coraClientFactory.authToken, "someAuthtoken");

		CoraClientSpy coraClient = coraClientFactory.factored;
		assertEquals(coraClient.recordTypeToList, "someRecordType");
		List<ClientDataRecord> returnedList = coraClient.listToReturn;
		int index = 0;
		String id = extractId(returnedList, index);

		assertSame(coraClient.deletedRecordTypes.get(0), "someRecordType");
		assertSame(coraClient.deletedRecordIds.get(0), id);
	}

	private String extractId(List<ClientDataRecord> returnedList, int index) {
		ClientDataGroup clientDataGroup = returnedList.get(index).getClientDataGroup();
		ClientDataGroup recordInfo = clientDataGroup.getFirstGroupWithNameInData("recordInfo");
		String id = recordInfo.getFirstAtomicValueWithNameInData("id");
		return id;
	}

	@Test
	public void testTransformerConvert() throws ClassNotFoundException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException, InstantiationException {
		FunderImporterRunner.main(args);
		DbToCoraTransformerSpy transformer = (DbToCoraTransformerSpy) FunderImporterRunner.coraTransformer;
		assertEquals(transformer.listOfConverted.size(), 1);
	}

	@Test
	public void testTransformerWhenUsingFileName()
			throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		args = new String[] { "se.uu.ub.cora.diva.tocorautils.DbToCoraTransformerSpy",
				"se.uu.ub.cora.diva.tocorautils.SqlDatabaseFactorySpy",
				"se.uu.ub.cora.diva.tocorautils.FromDbToCoraConverterFactorySpy",
				"jdbc:postgresql://diva-cora-docker-postgresql:543200/diva", "diva", "diva",
				"se.uu.ub.cora.diva.tocorautils.doubles.CoraClientFactorySpy",
				"someApptokenVerifierUrl", "someBaseUrl", "someAuthtoken", "someFileName" };

		FunderImporterRunner.main(args);

		DbToCoraTransformerSpy transformer = (DbToCoraTransformerSpy) FunderImporterRunner.coraTransformer;
		assertTrue(transformer.converterFactory instanceof FromDbToCoraConverterFactorySpy);
		assertEquals(transformer.fileName, "someFileName");
		assertNull(transformer.databaseFacade);
	}

}
