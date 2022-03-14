package se.uu.ub.cora.diva.tocorautils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.testng.annotations.Test;

import se.uu.ub.cora.diva.tocorautils.doubles.CoraClientFactorySpy;
import se.uu.ub.cora.diva.tocorautils.doubles.CoraClientSpy;

public class FunderImporterRunnerTest {
	private String args[] = new String[] { "se.uu.ub.cora.diva.tocorautils.DbToCoraTransformerSpy",
			"se.uu.ub.cora.diva.tocorautils.SqlDatabaseFactorySpy",
			"se.uu.ub.cora.diva.tocorautils.FromDbToCoraConverterFactorySpy",
			"jdbc:postgresql://diva-cora-docker-postgresql:543200/diva", "diva", "diva",
			"se.uu.ub.cora.diva.tocorautils.doubles.CoraClientFactorySpy",
			"someApptokenVerifierUrl", "someBaseUrl", "someAuthtoken" };

	// "se.uu.ub.cora.diva.tocorautils.importing.FunderTransformer"
	// "se.uu.ub.cora.sqldatabase.SqlDatabaseFactoryImp"
	// "se.uu.ub.cora.diva.tocorautils.convert.FromDbToCoraConverterFactoryImp"
	// "jdbc:postgresql://dev-diva-postgresql:5432/diva" "diva" "diva"
	// "se.uu.ub.cora.javaclient.cora.CoraClientFactoryImp"
	// "http://ipnummer:38082/apptokenverifier/" "http://ipnummer:38082/diva/rest/" ""

	// [0] transformerClassName
	// [1] sqlDatabaseFactoryName
	// [2] FromDbToCoraConverterFactory
	// [3] dburl
	// [4] dbUser
	// [5] dbPssword
	// [6] coraClientFactoryClassName
	// [7] apptokenVerifierUrl
	// [8] baseUrl
	// [9] authToken

	@Test
	public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		Constructor<FunderImporterRunner> constructor = FunderImporterRunner.class
				.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		constructor.setAccessible(true);
		constructor.newInstance();
	}

	@Test
	public void testSqlDatabaseFactory() throws ClassNotFoundException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException, InstantiationException {
		FunderImporterRunner.main(args);

		SqlDatabaseFactorySpy sqlDatabaseFactory = (SqlDatabaseFactorySpy) FunderImporterRunner.sqlDatabaseFactory;
		assertEquals(sqlDatabaseFactory.url,
				"jdbc:postgresql://diva-cora-docker-postgresql:543200/diva");
		assertEquals(sqlDatabaseFactory.user, "diva");
		assertEquals(sqlDatabaseFactory.password, "diva");

	}

	@Test
	public void testTransformer() throws ClassNotFoundException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException, InstantiationException {
		FunderImporterRunner.main(args);
		FunderImporterRunner.main(args);

		SqlDatabaseFactorySpy sqlDatabaseFactory = (SqlDatabaseFactorySpy) FunderImporterRunner.sqlDatabaseFactory;
		DbToCoraTransformerSpy transformer = (DbToCoraTransformerSpy) FunderImporterRunner.coraTransformer;
		assertNotNull(transformer.databaseFacade);
		assertSame(transformer.databaseFacade, sqlDatabaseFactory.factoredDatabaseFacade);
		assertTrue(transformer.converterFactory instanceof FromDbToCoraConverterFactorySpy);
	}

	@Test
	public void testCoraClient()
			throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, IllegalArgumentException, InstantiationException {
		FunderImporterRunner.main(args);
		CoraClientFactorySpy coraClientFactory = (CoraClientFactorySpy) FunderImporterRunner.coraClientFactory;
		assertEquals(coraClientFactory.appTokenVerifierUrl, "someApptokenVerifierUrl");
		assertEquals(coraClientFactory.baseUrl, "someBaseUrl");
		assertEquals(coraClientFactory.authToken, "someAuthtoken");

		DbToCoraTransformerSpy transformer = (DbToCoraTransformerSpy) FunderImporterRunner.coraTransformer;
		CoraClientSpy coraClient = coraClientFactory.factored;
		assertEquals(coraClient.dataGroupsSentToCreate.size(), 1);
		assertSame(coraClient.dataGroupsSentToCreate.get(0), transformer.listOfConverted.get(0));
		assertEquals(coraClient.createdRecordTypes.get(0), "funder");
	}

	@Test
	public void testTransformerConvert() throws ClassNotFoundException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException, InstantiationException {
		FunderImporterRunner.main(args);
		DbToCoraTransformerSpy transformer = (DbToCoraTransformerSpy) FunderImporterRunner.coraTransformer;
		assertEquals(transformer.listOfConverted.size(), 1);
	}

}
