/*
 * Copyright 2019 Uppsala University Library
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Field;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverterFactory;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverterFactoryImp;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataConverterFactory;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataConverterFactoryImp;
import se.uu.ub.cora.connection.ParameterConnectionProviderImp;
import se.uu.ub.cora.connection.SqlConnectionProvider;
import se.uu.ub.cora.diva.tocorautils.convert.FromDbToCoraJsonConverter;
import se.uu.ub.cora.diva.tocorautils.convert.FromDbToCoraSubjectCategoryConverter;
import se.uu.ub.cora.diva.tocorautils.convert.RecordCompleterSubjectCategory;
import se.uu.ub.cora.diva.tocorautils.doubles.CoraClientFactorySpy;
import se.uu.ub.cora.diva.tocorautils.importing.CoraImporter;
import se.uu.ub.cora.javaclient.CoraClientConfig;
import se.uu.ub.cora.javaclient.cora.CoraClient;
import se.uu.ub.cora.javaclient.cora.CoraClientFactory;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;
import se.uu.ub.cora.sqldatabase.RecordReaderFactoryImp;

public class FromDbToCoraSubjectCategoryFactoryTest {

	private FromDbToCoraSubjectCategory fromDbToCoraSubjectCategory;
	private FromDbToCoraSubjectCategoryFactory toCoraSubjectCategoryFactory;
	private CoraClientConfig coraClientConfig;
	private DbConfig dbConfig;

	@BeforeMethod
	public void beforeMethod() {

		String userId = "someCoraUserId";
		String appToken = "someCoraAppToken";
		String appTokenVerifierUrl = "someCoraAppTokenVierifierUrl";
		String coraUrl = "someCoraUrl";
		coraClientConfig = new CoraClientConfig(userId, appToken, appTokenVerifierUrl, coraUrl);

		String dbUserId = "someDbUserId";
		String password = "someDbPassword";
		String url = "someDbUrl";
		dbConfig = new DbConfig(dbUserId, password, url);

		CoraClientFactory coraClientFactory = new CoraClientFactorySpy();

		toCoraSubjectCategoryFactory = new FromDbToCoraSubjectCategoryFactory();
		fromDbToCoraSubjectCategory = (FromDbToCoraSubjectCategory) toCoraSubjectCategoryFactory
				.factorFromDbToCora(coraClientFactory, coraClientConfig, dbConfig);
	}

	@Test
	public void testInitCreatedRecordReaderFactory() throws Exception {
		RecordReaderFactoryImp createdRecordReaderFactory = (RecordReaderFactoryImp) fromDbToCoraSubjectCategory
				.getRecordReaderFactory();
		assertTrue(createdRecordReaderFactory instanceof RecordReaderFactoryImp);

		SqlConnectionProvider connectionProvider = createdRecordReaderFactory
				.getSqlConnectionProvider();
		assertTrue(connectionProvider instanceof ParameterConnectionProviderImp);

		Field declaredUrlField = connectionProvider.getClass().getDeclaredField("url");
		declaredUrlField.setAccessible(true);
		String setUrl = (String) declaredUrlField.get(connectionProvider);
		assertEquals(setUrl, "someDbUrl");

		Field declaredUserField = connectionProvider.getClass().getDeclaredField("user");
		declaredUserField.setAccessible(true);
		String userId = (String) declaredUserField.get(connectionProvider);
		assertEquals(userId, "someDbUserId");

		Field declaredPasswordField = connectionProvider.getClass().getDeclaredField("password");
		declaredPasswordField.setAccessible(true);
		String password = (String) declaredPasswordField.get(connectionProvider);
		assertEquals(password, "someDbPassword");
	}

	@Test
	public void testInitFromDbToCoraConverter() throws Exception {
		FromDbToCoraJsonConverter createdConverter = fromDbToCoraSubjectCategory
				.getFromDbToCoraConverter();
		assertTrue(createdConverter instanceof FromDbToCoraSubjectCategoryConverter);

		FromDbToCoraSubjectCategoryConverter subjectCategoryConverter = (FromDbToCoraSubjectCategoryConverter) createdConverter;

		JsonBuilderFactory jsonBuilderFactory = subjectCategoryConverter.getJsonBuilderFactory();
		assertTrue(jsonBuilderFactory instanceof OrgJsonBuilderFactoryAdapter);
		assertNotNull(jsonBuilderFactory);
		DataToJsonConverterFactory dataToJsonConverterFactory = subjectCategoryConverter
				.getDataToJsonConverterFactory();
		assertTrue(dataToJsonConverterFactory instanceof DataToJsonConverterFactoryImp);
		assertNotNull(dataToJsonConverterFactory);
	}

	@Test
	public void testInitCoraImporter() throws Exception {
		CoraImporter importer = (CoraImporter) fromDbToCoraSubjectCategory.getImporter();
		assertTrue(importer instanceof CoraImporter);

		CoraClient coraClient = importer.getCoraClient();

		CoraClientFactorySpy coraClientFactory = (CoraClientFactorySpy) toCoraSubjectCategoryFactory
				.getCoraClientFactory();
		assertTrue(coraClientFactory instanceof CoraClientFactorySpy);

		assertEquals(coraClient, coraClientFactory.factored);
		assertEquals(coraClientFactory.userId, coraClientConfig.userId);
		assertEquals(coraClientFactory.appToken, coraClientConfig.appToken);
	}

	@Test
	public void testInitRecordCompleter() throws Exception {
		RecordCompleterSubjectCategory recordCompleter = (RecordCompleterSubjectCategory) fromDbToCoraSubjectCategory
				.getRecordCompleter();
		assertTrue(recordCompleter.getRecordReaderFactory() instanceof RecordReaderFactoryImp);

	}

	@Test
	public void testInitJsonToDataConverterFactory() throws Exception {
		JsonToDataConverterFactory jsonToDataConverterFactory = fromDbToCoraSubjectCategory
				.getJsonToDataConverterFactory();
		assertTrue(jsonToDataConverterFactory instanceof JsonToDataConverterFactoryImp);
	}
}
