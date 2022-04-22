package se.uu.ub.cora.diva.tocorautils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.javaclient.cora.CoraClient;
import se.uu.ub.cora.javaclient.cora.CoraClientException;
import se.uu.ub.cora.javaclient.cora.CoraClientFactory;

public class DeleterRunner {
	public static CoraClientFactory coraClientFactory;

	private DeleterRunner() {
	}

	public static void main(String[] args) throws NoSuchMethodException, ClassNotFoundException,
			IllegalAccessException, InvocationTargetException {
		createCoraClientFactory(args);
		CoraClient coraClient = coraClientFactory.factorUsingAuthToken(args[3]);

		String recordType = args[4];
		List<ClientDataRecord> listOfRecords = coraClient.readListAsDataRecords(recordType);
		for (ClientDataRecord clientDataRecord : listOfRecords) {
			String id = extractId(clientDataRecord);
			try {
				coraClient.delete(recordType, id);
				System.out.println("Deleted " + id);
			} catch (CoraClientException exception) {
				System.out.println("Unable to delete " + id);
			}
		}
	}

	private static String extractId(ClientDataRecord clientDataRecord) {
		ClientDataGroup clientDataGroup = clientDataRecord.getClientDataGroup();
		ClientDataGroup recordInfo = clientDataGroup.getFirstGroupWithNameInData("recordInfo");
		return recordInfo.getFirstAtomicValueWithNameInData("id");
	}

	private static void createCoraClientFactory(String[] args) throws NoSuchMethodException,
			ClassNotFoundException, IllegalAccessException, InvocationTargetException {
		Class<?>[] cArg = new Class[2];
		cArg[0] = String.class;
		cArg[1] = String.class;
		String coraClientFactoryClassName = args[0];
		Method constructor = Class.forName(coraClientFactoryClassName)
				.getMethod("usingAppTokenVerifierUrlAndBaseUrl", cArg);
		coraClientFactory = (CoraClientFactory) constructor.invoke(null, args[1], args[2]);

	}
}
