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

import importing.CoraImporter;
import importing.Importer;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverterFactory;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverterFactoryImp;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataConverterFactory;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataConverterFactoryImp;
import se.uu.ub.cora.connection.ParameterConnectionProviderImp;
import se.uu.ub.cora.connection.SqlConnectionProvider;
import se.uu.ub.cora.diva.tocorautils.convert.FromDbToCoraConverter;
import se.uu.ub.cora.diva.tocorautils.convert.FromDbToCoraSubjectCategoryConverter;
import se.uu.ub.cora.diva.tocorautils.convert.RecordCompleterSubjectCategory;
import se.uu.ub.cora.javaclient.CoraClientConfig;
import se.uu.ub.cora.javaclient.cora.CoraClient;
import se.uu.ub.cora.javaclient.cora.CoraClientFactory;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;
import se.uu.ub.cora.sqldatabase.RecordReaderFactory;
import se.uu.ub.cora.sqldatabase.RecordReaderFactoryImp;

public class FromDbToCoraSubjectCategoryFactory implements FromDbToCoraFactory {

	private CoraClientFactory coraClientFactory;

	@Override
	public final FromDbToCora factorFromDbToCora(CoraClientFactory coraClientFactory,
			CoraClientConfig coraClientConfig, DbConfig dbConfig) {
		this.coraClientFactory = coraClientFactory;

		FromDbToCoraConverter fromDbToCoraConverter = createConverter();

		RecordReaderFactory recordReaderFactory = createRecordReaderFactory(dbConfig);
		RecordCompleterSubjectCategory recordCompleter = RecordCompleterSubjectCategory
				.usingRecordReaderFactory(recordReaderFactory);

		JsonToDataConverterFactory jsonToDataConverterFactory = new JsonToDataConverterFactoryImp();
		Importer importer = createImporter(coraClientConfig);

		return FromDbToCoraSubjectCategory
				.usingRecordReaderFactoryDbToCoraConverterRecordCompleterJsonToDataConverterFactoryAndImporter(
						recordReaderFactory, fromDbToCoraConverter, recordCompleter,
						jsonToDataConverterFactory, importer);
	}

	private FromDbToCoraConverter createConverter() {
		DataToJsonConverterFactory dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		JsonBuilderFactory jsonFactory = createJsonBuilderFactory();
		return FromDbToCoraSubjectCategoryConverter.usingJsonFactoryAndConverterFactory(jsonFactory,
				dataToJsonConverterFactory);
	}

	protected final RecordReaderFactory createRecordReaderFactory(DbConfig dbConfig) {
		SqlConnectionProvider connectionProvider = ParameterConnectionProviderImp
				.usingUriAndUserAndPassword(dbConfig.url, dbConfig.userId, dbConfig.password);
		return RecordReaderFactoryImp.usingSqlConnectionProvider(connectionProvider);
	}

	protected final JsonBuilderFactory createJsonBuilderFactory() {
		return new OrgJsonBuilderFactoryAdapter();
	}

	protected final CoraClient createCoraClient(CoraClientFactory coraClientFactory,
			CoraClientConfig coraClientConfig) {
		return coraClientFactory.factor(coraClientConfig.userId, coraClientConfig.appToken);
	}

	protected Importer createImporter(CoraClientConfig coraClientConfig) {
		CoraClient coraClient = createCoraClient(coraClientFactory, coraClientConfig);
		return CoraImporter.usingCoraClient(coraClient);
	}

	protected CoraClientFactory getCoraClientFactory() {
		// needed for test
		return coraClientFactory;
	}

}
