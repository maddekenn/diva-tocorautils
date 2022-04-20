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
import se.uu.ub.cora.diva.tocorautils.doubles.CoraClientFactorySpy;
import se.uu.ub.cora.diva.tocorautils.doubles.CoraClientSpy;

public class FromFileWithExtraTableImporterRunnerTest {
	private String args[] = new String[] { "se.uu.ub.cora.diva.tocorautils.DbToCoraTransformerSpy",
			"se.uu.ub.cora.diva.tocorautils.FromDbToCoraConverterFactorySpy",
			"se.uu.ub.cora.diva.tocorautils.doubles.CoraClientFactorySpy",
			"se.uu.ub.cora.diva.tocorautils.RecordCompleterSpy", "someApptokenVerifierUrl",
			"someBaseUrl", "someAuthtoken", "somePathToFile", "somePathToExtraFile",
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
	// [3] recordCompleterClassName
	// [4] apptokenVerifierUrl
	// [5] baseUrl
	// [6] authToken
	// [7] filename
	// [8] filename extra table
	// [9] recordType

	@Test
	public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		Constructor<FromFileWithExtraTableImporterRunner> constructor = FromFileWithExtraTableImporterRunner.class
				.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		constructor.setAccessible(true);
		constructor.newInstance();
	}

	@Test
	public void testCoraClient()
			throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, IllegalArgumentException, InstantiationException {
		FromFileWithExtraTableImporterRunner.main(args);
		CoraClientFactorySpy coraClientFactory = (CoraClientFactorySpy) FromFileWithExtraTableImporterRunner.coraClientFactory;
		assertEquals(coraClientFactory.appTokenVerifierUrl, "someApptokenVerifierUrl");
		assertEquals(coraClientFactory.baseUrl, "someBaseUrl");
		assertEquals(coraClientFactory.authToken, "someAuthtoken");

		DbToCoraTransformerSpy transformer = (DbToCoraTransformerSpy) FromFileWithExtraTableImporterRunner.coraTransformer;
		CoraClientSpy coraClient = coraClientFactory.factored;
		assertEquals(coraClient.dataGroupsSentToCreate.size(), 2);
		assertSame(coraClient.dataGroupsSentToCreate.get(0), transformer.listOfConverted.get(0));
		assertEquals(coraClient.createdRecordTypes.get(0), "someRecordType");
	}

	@Test
	public void testTransformerConvert() throws ClassNotFoundException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException, InstantiationException {
		FromFileWithExtraTableImporterRunner.main(args);
		DbToCoraTransformerSpy transformer = (DbToCoraTransformerSpy) FromFileWithExtraTableImporterRunner.coraTransformer;
		assertEquals(transformer.listOfConverted.size(), 2);
	}

	@Test
	public void testTransformerWhenUsingFileName()
			throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, InstantiationException {

		FromFileWithExtraTableImporterRunner.main(args);

		DbToCoraTransformerSpy transformer = (DbToCoraTransformerSpy) FromFileWithExtraTableImporterRunner.coraTransformer;
		assertTrue(transformer.converterFactory instanceof FromDbToCoraConverterFactorySpy);
		assertEquals(transformer.fileName, "somePathToFile");
		assertEquals(transformer.type, "someRecordType");
		assertNull(transformer.databaseFacade);
	}

	@Test
	public void testRecordCompleterCreatedCorrectly()
			throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, IllegalArgumentException, InstantiationException {
		FromFileWithExtraTableImporterRunner.main(args);

		RecordCompleterSpy recordCompleter = (RecordCompleterSpy) FromFileWithExtraTableImporterRunner.recordCompleter;
		assertEquals(recordCompleter.pathToFile, "somePathToExtraFile");
	}

	@Test
	public void testCallToRecordCompleter()
			throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, IllegalArgumentException, InstantiationException {
		FromFileWithExtraTableImporterRunner.main(args);

		RecordCompleterSpy recordCompleter = (RecordCompleterSpy) FromFileWithExtraTableImporterRunner.recordCompleter;

		DbToCoraTransformerSpy transformer = (DbToCoraTransformerSpy) FromFileWithExtraTableImporterRunner.coraTransformer;

		List<ClientDataGroup> convertedFromTransformer = transformer.listOfConverted;

		assertSame(recordCompleter.dataGroups, convertedFromTransformer);

		CoraClientFactorySpy coraClientFactory = (CoraClientFactorySpy) FromFileWithExtraTableImporterRunner.coraClientFactory;
		CoraClientSpy coraClient = coraClientFactory.factored;
		assertEquals(coraClient.dataGroupsSentToUpdate.size(), 2);
		assertEquals(coraClient.updatedRecordTypes.get(0), "someRecordType");

		String recordId = extractRecordIdUsingIndex(convertedFromTransformer, 0);
		assertEquals(coraClient.updatedRecordIds.get(0), recordId);

		String recordId2 = extractRecordIdUsingIndex(convertedFromTransformer, 1);
		assertEquals(coraClient.updatedRecordIds.get(1), recordId2);

	}

	private String extractRecordIdUsingIndex(List<ClientDataGroup> convertedFromTransformer,
			int index) {
		ClientDataGroup clientDataGroup = convertedFromTransformer.get(index);
		ClientDataGroup recordInfo = clientDataGroup.getFirstGroupWithNameInData("recordInfo");
		String recordId = recordInfo.getFirstAtomicValueWithNameInData("id");
		return recordId;
	}
}
