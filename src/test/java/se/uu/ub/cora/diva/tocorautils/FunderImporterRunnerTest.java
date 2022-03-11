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
		CoraClientSpy coraClient = coraClientFactory.factored;
		assertEquals(coraClient.dataGroupsSentToCreate.size(), 1);
	}

	@Test
	public void testTransformerConvert() throws ClassNotFoundException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException, InstantiationException {
		FunderImporterRunner.main(args);
		DbToCoraTransformerSpy transformer = (DbToCoraTransformerSpy) FunderImporterRunner.coraTransformer;
		assertEquals(transformer.listOfConverted.size(), 1);
	}

}
