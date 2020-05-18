/*
 * Copyright 2020 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.diva.tocorautils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.batchrunner.JsonToClientData;
import se.uu.ub.cora.batchrunner.find.Finder;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataConverterFactory;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataConverterFactoryImp;
import se.uu.ub.cora.javaclient.cora.CoraClient;
import se.uu.ub.cora.javaclient.cora.CoraClientFactory;
import se.uu.ub.cora.json.parser.JsonParser;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;

public class ChangeDataGroupBatchRunner {

	static List<String> errors = new ArrayList<>();
	protected static Finder finder;
	protected static JsonToClientData jsonToClientData;
	protected static Updater updater;

	protected static CoraClientFactory coraClientFactory;

	private ChangeDataGroupBatchRunner() {
	}

	public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException {
		String userId = args[0];
		String appToken = args[1];
		String apptokenVerifierUrl = args[2];
		String baseUrl = args[3];
		String coraClientFactoryClassName = args[4];
		String jsonToClientDataClassName = args[5];
		String updaterClassName = args[6];
		String recordType = args[7];

		System.out.println("starting");
		createCoraClientFactory(coraClientFactoryClassName, apptokenVerifierUrl, baseUrl);
		CoraClient coraClient = coraClientFactory.factor(userId, appToken);

		createJsonToClientData(jsonToClientDataClassName);

		createUpdater(updaterClassName, coraClient);
		updater.update(recordType);
		System.out.println("done ");
	}

	private static void createCoraClientFactory(String clientFactoryClassName,
			String apptokenVerifierUrl, String baseUrl) throws NoSuchMethodException,
			ClassNotFoundException, IllegalAccessException, InvocationTargetException {
		Class<?>[] cArg = new Class[2];
		cArg[0] = String.class;
		cArg[1] = String.class;
		Method constructor = Class.forName(clientFactoryClassName)
				.getMethod("usingAppTokenVerifierUrlAndBaseUrl", cArg);
		coraClientFactory = (CoraClientFactory) constructor.invoke(null, apptokenVerifierUrl,
				baseUrl);
	}

	private static void createJsonToClientData(String jsonToClientDataClassName)
			throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException,
			InvocationTargetException {

		Class<?>[] cArg = new Class[1];
		cArg[0] = JsonParser.class;
		JsonParser jsonParser = new OrgJsonParser();
		Method constructor = Class.forName(jsonToClientDataClassName).getMethod("usingJsonParser",
				cArg);
		jsonToClientData = (JsonToClientData) constructor.invoke(null, jsonParser);
	}

	private static void createUpdater(String updaterClassName, CoraClient coraClient)
			throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException,
			InvocationTargetException {
		Class<?>[] cArg = new Class[4];
		cArg[0] = CoraClient.class;
		cArg[1] = JsonToClientData.class;
		cArg[2] = DataGroupChangerFactory.class;
		cArg[3] = JsonToDataConverterFactory.class;
		DataGroupChangerFactory changerFactory = new DataGroupChangerFactoryImp();
		JsonToDataConverterFactory jsonToDataConverterFactory = new JsonToDataConverterFactoryImp();

		Method constructor = Class.forName(updaterClassName).getMethod(
				"usingCoraClientJsonToClientDataChangerFactoryAndConverterFactory", cArg);
		updater = (Updater) constructor.invoke(null, coraClient, jsonToClientData, changerFactory,
				jsonToDataConverterFactory);
	}

}
