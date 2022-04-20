package se.uu.ub.cora.diva.tocorautils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.testng.annotations.Test;

import se.uu.ub.cora.diva.tocorautils.doubles.CoraClientFactorySpy;
import se.uu.ub.cora.diva.tocorautils.doubles.CoraClientSpy;

public class FunderImporterFromFileRunnerTest {
	private String args[] = new String[] { "se.uu.ub.cora.diva.tocorautils.DbToCoraTransformerSpy",
			"se.uu.ub.cora.diva.tocorautils.FromDbToCoraConverterFactorySpy",
			"se.uu.ub.cora.diva.tocorautils.doubles.CoraClientFactorySpy",
			"someApptokenVerifierUrl", "someBaseUrl", "someAuthtoken", "someFileName",
			"someRecordType" };

	// "se.uu.ub.cora.diva.tocorautils.importing.FunderTransformer"
	// "se.uu.ub.cora.sqldatabase.SqlDatabaseFactoryImp"
	// "se.uu.ub.cora.diva.tocorautils.convert.FromDbToCoraConverterFactoryImp"
	// "jdbc:postgresql://dev-diva-postgresql:5432/diva" "diva" "diva"
	// "se.uu.ub.cora.javaclient.cora.CoraClientFactoryImp"
	// "http://ipnummer:38082/apptokenverifier/" "http://ipnummer:38082/diva/rest/" ""

	// [0] transformerClassName
	// [1] FromDbToCoraConverterFactory
	// [2] coraClientFactoryClassName
	// [3] apptokenVerifierUrl
	// [4] baseUrl
	// [5] authToken
	// [6] filename
	// [7] recordType

	@Test
	public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		Constructor<FunderImporterFromFileRunner> constructor = FunderImporterFromFileRunner.class
				.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		constructor.setAccessible(true);
		constructor.newInstance();
	}

	@Test
	public void testCoraClient()
			throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, IllegalArgumentException, InstantiationException {
		FunderImporterFromFileRunner.main(args);
		CoraClientFactorySpy coraClientFactory = (CoraClientFactorySpy) FunderImporterFromFileRunner.coraClientFactory;
		assertEquals(coraClientFactory.appTokenVerifierUrl, "someApptokenVerifierUrl");
		assertEquals(coraClientFactory.baseUrl, "someBaseUrl");
		assertEquals(coraClientFactory.authToken, "someAuthtoken");

		DbToCoraTransformerSpy transformer = (DbToCoraTransformerSpy) FunderImporterFromFileRunner.coraTransformer;
		CoraClientSpy coraClient = coraClientFactory.factored;
		assertEquals(coraClient.dataGroupsSentToCreate.size(), 2);
		assertSame(coraClient.dataGroupsSentToCreate.get(0), transformer.listOfConverted.get(0));
		assertEquals(coraClient.createdRecordTypes.get(0), "someRecordType");
	}

	@Test
	public void testTransformerConvert() throws ClassNotFoundException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException, InstantiationException {
		FunderImporterFromFileRunner.main(args);
		DbToCoraTransformerSpy transformer = (DbToCoraTransformerSpy) FunderImporterFromFileRunner.coraTransformer;
		assertEquals(transformer.listOfConverted.size(), 2);
	}

	@Test
	public void testTransformerWhenUsingFileName()
			throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, InstantiationException {

		FunderImporterFromFileRunner.main(args);

		DbToCoraTransformerSpy transformer = (DbToCoraTransformerSpy) FunderImporterFromFileRunner.coraTransformer;
		assertTrue(transformer.converterFactory instanceof FromDbToCoraConverterFactorySpy);
		assertEquals(transformer.fileName, "someFileName");
		assertEquals(transformer.type, "someRecordType");
		assertNull(transformer.databaseFacade);
	}

}
