package se.uu.ub.cora.diva.tocorautils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.diva.tocorautils.convert.FromDbToCoraConverterFactory;
import se.uu.ub.cora.diva.tocorautils.importing.DbToCoraTransformer;
import se.uu.ub.cora.javaclient.cora.CoraClient;
import se.uu.ub.cora.javaclient.cora.CoraClientFactory;
import se.uu.ub.cora.sqldatabase.DatabaseFacade;
import se.uu.ub.cora.sqldatabase.SqlDatabaseFactory;

public class FunderImporterRunner {

	private static final int NUM_OF_ARGS_WHEN_FILENAME_INCLUDED = 11;
	public static SqlDatabaseFactory sqlDatabaseFactory;
	public static DbToCoraTransformer coraTransformer;
	public static CoraClientFactory coraClientFactory;

	private FunderImporterRunner() {
	}

	public static void main(String[] args)
			throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, IllegalArgumentException, InstantiationException {
		constructSqlDatabaseFactory(args);
		constructCoraTransformer(args);

		List<ClientDataGroup> converted = coraTransformer.getConverted();

		createCoraClientFactory(args);
		CoraClient coraClient = coraClientFactory.factorUsingAuthToken(args[9]);
		createRecords(converted, coraClient);
	}

	private static void createRecords(List<ClientDataGroup> converted,
			CoraClient coraClient) {
		int counter = 0;
		for (ClientDataGroup dataGroup : converted) {
			System.out.println("Id: " + dataGroup.getFirstAtomicValueWithNameInData("classicId"));
			coraClient.create("funder", dataGroup);
			counter++;
		}

		System.out.println("Number of records imorted: " + counter);
	}

	private static void constructSqlDatabaseFactory(String[] args) throws NoSuchMethodException,
			ClassNotFoundException, IllegalAccessException, InvocationTargetException {
		String sqlDatabaseFactoryName = args[1];
		String dbUrl = args[3];
		String dbUser = args[4];
		String dbPassword = args[5];
		Class<?>[] cArg = createArgumentsForDatabaseFacadeFactory();
		Method constructor = Class.forName(sqlDatabaseFactoryName)
				.getMethod("usingUriAndUserAndPassword", cArg);
		sqlDatabaseFactory = (SqlDatabaseFactory) constructor.invoke(null, dbUrl, dbUser,
				dbPassword);
	}

	private static Class<?>[] createArgumentsForDatabaseFacadeFactory() {
		Class<?>[] cArg = new Class[3];
		cArg[0] = String.class;
		cArg[1] = String.class;
		cArg[2] = String.class;
		return cArg;
	}

	private static void constructCoraTransformer(String[] args)
			throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException,
			InvocationTargetException, IllegalArgumentException, InstantiationException {
		String toCoraTransformerName = args[0];

		FromDbToCoraConverterFactory constructConverterFactory = constructConverterFactory(args[2]);
		if (argsIncludeFileName(args)) {
			createTransformerUsingFilename(args, toCoraTransformerName, constructConverterFactory);
		} else {
			createTransformerUsingDBFacade(toCoraTransformerName, constructConverterFactory);
		}
	}

	private static boolean argsIncludeFileName(String[] args) {
		return args.length == NUM_OF_ARGS_WHEN_FILENAME_INCLUDED;
	}

	private static void createTransformerUsingFilename(String[] args, String toCoraTransformerName,
			FromDbToCoraConverterFactory constructConverterFactory) throws NoSuchMethodException,
			ClassNotFoundException, IllegalAccessException, InvocationTargetException {
		Class<?>[] cArg = new Class[2];
		cArg[0] = String.class;
		cArg[1] = FromDbToCoraConverterFactory.class;
		String fileName = args[10];
		Method constructor = Class.forName(toCoraTransformerName)
				.getMethod("usingFilePathAndConverterFactory", cArg);
		coraTransformer = (DbToCoraTransformer) constructor.invoke(null, fileName,
				constructConverterFactory);
	}

	private static void createTransformerUsingDBFacade(String toCoraTransformerName,
			FromDbToCoraConverterFactory constructConverterFactory) throws NoSuchMethodException,
			ClassNotFoundException, IllegalAccessException, InvocationTargetException {
		Class<?>[] cArg = createArgumentsForCoraTransformer();
		DatabaseFacade databaseFacade = sqlDatabaseFactory.factorDatabaseFacade();
		Method constructor = Class.forName(toCoraTransformerName)
				.getMethod("usingDatabaseFacadeAndFromDbConverterFactory", cArg);
		coraTransformer = (DbToCoraTransformer) constructor.invoke(null, databaseFacade,
				constructConverterFactory);
	}

	private static Class<?>[] createArgumentsForCoraTransformer() {
		Class<?>[] cArg = new Class[2];
		cArg[0] = DatabaseFacade.class;
		cArg[1] = FromDbToCoraConverterFactory.class;
		return cArg;
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
		String coraClientFactoryClassName = args[6];
		Method constructor = Class.forName(coraClientFactoryClassName)
				.getMethod("usingAppTokenVerifierUrlAndBaseUrl", cArg);
		coraClientFactory = (CoraClientFactory) constructor.invoke(null, args[7], args[8]);

	}
}
