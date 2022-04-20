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

public class FunderImporterFromFileRunner {

	public static DbToCoraTransformer coraTransformer;
	public static CoraClientFactory coraClientFactory;

	private FunderImporterFromFileRunner() {
	}

	public static void main(String[] args)
			throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, IllegalArgumentException, InstantiationException {
		constructCoraTransformer(args);

		List<ClientDataGroup> converted = coraTransformer.getConverted();

		createCoraClientFactory(args);
		CoraClient coraClient = coraClientFactory.factorUsingAuthToken(args[5]);
		createRecords(converted, coraClient, args);
	}

	private static void createRecords(List<ClientDataGroup> converted, CoraClient coraClient,
			String[] args) {
		int counter = 0;
		String recordType = args[7];
		for (ClientDataGroup dataGroup : converted) {
			// System.out.println("Id: " +
			// dataGroup.getFirstAtomicValueWithNameInData("classicId"));
			coraClient.create(recordType, dataGroup);
			counter++;
		}

		System.out.println("Number of records imorted: " + counter);
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
		String fileName = args[6];
		Method constructor = Class.forName(toCoraTransformerName)
				.getMethod("usingFilePathConverterFactoryAndType", cArg);
		coraTransformer = (DbToCoraTransformer) constructor.invoke(null, fileName,
				constructConverterFactory, args[7]);
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
		coraClientFactory = (CoraClientFactory) constructor.invoke(null, args[3], args[4]);

	}
}
