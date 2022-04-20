package se.uu.ub.cora.diva.tocorautils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.diva.tocorautils.convert.FromDbToCoraConverterFactory;
import se.uu.ub.cora.diva.tocorautils.convert.RecordCompleter;
import se.uu.ub.cora.diva.tocorautils.importing.DbToCoraTransformer;
import se.uu.ub.cora.javaclient.cora.CoraClient;
import se.uu.ub.cora.javaclient.cora.CoraClientFactory;

public class FromFileWithExtraTableImporterRunner {
	public static DbToCoraTransformer coraTransformer;
	public static CoraClientFactory coraClientFactory;
	public static RecordCompleter recordCompleter;
	private static String recordType;

	private FromFileWithExtraTableImporterRunner() {
	}

	public static void main(String[] args)
			throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, IllegalArgumentException, InstantiationException {
		recordType = args[9];
		constructCoraTransformer(args);

		List<ClientDataGroup> converted = coraTransformer.getConverted();

		createCoraClientFactory(args);
		CoraClient coraClient = coraClientFactory.factorUsingAuthToken(args[6]);
		createRecords(converted, coraClient, args);

		createRecordCompleter(args);
		completeAndUpdateDataGroups(converted, coraClient);
	}

	private static void createRecords(List<ClientDataGroup> converted, CoraClient coraClient,
			String[] args) {
		int counter = 0;
		String recordType = args[9];
		for (ClientDataGroup dataGroup : converted) {
			// System.out.println("Id: " +
			// dataGroup.getFirstAtomicValueWithNameInData("classicId"));
			coraClient.create(recordType, dataGroup);
			counter++;
		}

		System.out.println("Number of records imported: " + counter);
	}

	private static void constructCoraTransformer(String[] args)
			throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException,
			InvocationTargetException, IllegalArgumentException, InstantiationException {
		String toCoraTransformerName = args[0];

		FromDbToCoraConverterFactory constructConverterFactory = constructConverterFactory(args[1]);
		createTransformerUsingFilename(args, toCoraTransformerName, constructConverterFactory);
	}

	private static void createTransformerUsingFilename(String[] args, String toCoraTransformerName,
			FromDbToCoraConverterFactory constructConverterFactory) throws NoSuchMethodException,
			ClassNotFoundException, IllegalAccessException, InvocationTargetException {
		Class<?>[] cArg = new Class[3];
		cArg[0] = String.class;
		cArg[1] = FromDbToCoraConverterFactory.class;
		cArg[2] = String.class;
		String fileName = args[7];
		Method constructor = Class.forName(toCoraTransformerName)
				.getMethod("usingFilePathConverterFactoryAndType", cArg);
		coraTransformer = (DbToCoraTransformer) constructor.invoke(null, fileName,
				constructConverterFactory, recordType);
	}

	private static FromDbToCoraConverterFactory constructConverterFactory(String converterClassName)
			throws NoSuchMethodException, ClassNotFoundException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		Constructor<?> constructor = Class.forName(converterClassName).getConstructor();
		return (FromDbToCoraConverterFactory) constructor.newInstance();
	}

	private static void createCoraClientFactory(String[] args) throws NoSuchMethodException,
			ClassNotFoundException, IllegalAccessException, InvocationTargetException {
		Class<?>[] cArg = new Class[2];
		cArg[0] = String.class;
		cArg[1] = String.class;
		String coraClientFactoryClassName = args[2];
		Method constructor = Class.forName(coraClientFactoryClassName)
				.getMethod("usingAppTokenVerifierUrlAndBaseUrl", cArg);
		coraClientFactory = (CoraClientFactory) constructor.invoke(null, args[4], args[5]);

	}

	private static void createRecordCompleter(String[] args) throws NoSuchMethodException,
			ClassNotFoundException, IllegalAccessException, InvocationTargetException {
		Class<?>[] cArg = new Class[1];
		cArg[0] = String.class;
		String recordCompleterClassName = args[3];
		Method constructor = Class.forName(recordCompleterClassName).getMethod("usingPathToFile",
				cArg);
		String pathToFileExtraTable = args[8];
		recordCompleter = (RecordCompleter) constructor.invoke(null, pathToFileExtraTable);

	}

	private static void completeAndUpdateDataGroups(List<ClientDataGroup> converted,
			CoraClient coraClient) {
		List<ClientDataGroup> completedGroups = recordCompleter.completeMetadata(converted);
		for (ClientDataGroup clientDataGroup : completedGroups) {
			String recordId = extractRecordId(clientDataGroup);
			coraClient.update(recordType, recordId, clientDataGroup);
		}
	}

	private static String extractRecordId(ClientDataGroup clientDataGroup) {
		ClientDataGroup recordInfo = clientDataGroup.getFirstGroupWithNameInData("recordInfo");
		return recordInfo.getFirstAtomicValueWithNameInData("id");
	}
}
