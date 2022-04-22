package se.uu.ub.cora.diva.tocorautils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;

import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.diva.tocorautils.doubles.CoraClientExceptionSpy;
import se.uu.ub.cora.diva.tocorautils.doubles.CoraClientFactoryExceptionClientSpy;
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

		assertSame(coraClient.deletedRecordTypes.get(0), "someRecordType");
		assertEquals(coraClient.deletedRecordIds.get(0), extractId(returnedList, 0));
		assertEquals(coraClient.deletedRecordIds.get(1), extractId(returnedList, 1));
		assertEquals(coraClient.deletedRecordIds.get(2), extractId(returnedList, 2));
		assertEquals(coraClient.deletedRecordIds.get(3), extractId(returnedList, 3));
		assertEquals(coraClient.deletedRecordIds.get(4), extractId(returnedList, 4));
		assertEquals(coraClient.deletedRecordTypes.size(), 5);

	}

	private String extractId(List<ClientDataRecord> returnedList, int index) {
		ClientDataGroup clientDataGroup = returnedList.get(index).getClientDataGroup();
		ClientDataGroup recordInfo = clientDataGroup.getFirstGroupWithNameInData("recordInfo");
		String id = recordInfo.getFirstAtomicValueWithNameInData("id");
		return id;
	}

	@Test
	public void testUnableToDelete() throws NoSuchMethodException, ClassNotFoundException,
			IllegalAccessException, InvocationTargetException {
		args = new String[] {
				"se.uu.ub.cora.diva.tocorautils.doubles.CoraClientFactoryExceptionClientSpy",
				"someApptokenVerifierUrl", "someBaseUrl", "someAuthtoken", "someRecordType" };
		DeleterRunner.main(args);
		CoraClientFactoryExceptionClientSpy coraClientFactory = (CoraClientFactoryExceptionClientSpy) DeleterRunner.coraClientFactory;
		CoraClientExceptionSpy coraClient = coraClientFactory.factored;
		assertEquals(coraClient.deletedRecordTypes.size(), 5);

	}

}
