
package se.uu.ub.cora.diva.tocorautils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.javaclient.cora.CoraClient;
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
		ClientDataGroup clientDataGroup = listOfRecords.get(0).getClientDataGroup();
		ClientDataGroup recordInfo = clientDataGroup.getFirstGroupWithNameInData("recordInfo");
		String id = recordInfo.getFirstAtomicValueWithNameInData("id");
		coraClient.delete(recordType, id);
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
