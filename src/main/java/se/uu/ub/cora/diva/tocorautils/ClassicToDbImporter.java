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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverterFactory;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverterFactoryImp;
import se.uu.ub.cora.connection.ParameterConnectionProviderImp;
import se.uu.ub.cora.diva.tocorautils.importing.DivaImporter;
import se.uu.ub.cora.diva.tocorautils.importing.FromDbToCoraConverter;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;
import se.uu.ub.cora.sqldatabase.DataReader;
import se.uu.ub.cora.sqldatabase.DataReaderImp;
import se.uu.ub.cora.sqldatabase.DataUpdater;
import se.uu.ub.cora.sqldatabase.DataUpdaterImp;
import se.uu.ub.cora.sqldatabase.RecordCreator;
import se.uu.ub.cora.sqldatabase.RecordReader;

public class ClassicToDbImporter {

	protected static DivaImporter divaImporter;

	public static void main(String[] args) throws InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		RecordReader recordReader = createRecordReader(args);
		FromDbToCoraConverter fromDbToCoraConverter = createFromDbToCoraConverter(args);
		RecordCreator recordCreator = createRecordCreator(args);
		DataToJsonConverterFactoryImp dataToJsonConverterFactory = createDataToJsonConverterFactory();

		divaImporter = createDivaImporter(args, recordReader, fromDbToCoraConverter, recordCreator,
				dataToJsonConverterFactory);
		System.out.println("before import");
		divaImporter.importData();
		System.out.println("after import");

	}

	private static RecordReader createRecordReader(String[] args) throws NoSuchMethodException,
			ClassNotFoundException, IllegalAccessException, InvocationTargetException {
		DataReader dataReader = createDataReader(args);
		String recordReaderClassName = args[1];
		return createRecordReaderUsingClassNameAndDataReader(recordReaderClassName, dataReader);
	}

	private static DataReader createDataReader(String[] args) {
		ParameterConnectionProviderImp connectionProvider = cretateConnectionProvider(args[2],
				args[3], args[4]);
		return DataReaderImp.usingSqlConnectionProvider(connectionProvider);
	}

	private static ParameterConnectionProviderImp cretateConnectionProvider(String dbToReadFromUrl,
			String dbToReadFromUserId, String dbToReadFromPassword) {
		return ParameterConnectionProviderImp.usingUriAndUserAndPassword(dbToReadFromUrl,
				dbToReadFromUserId, dbToReadFromPassword);
	}

	private static RecordReader createRecordReaderUsingClassNameAndDataReader(
			String recordReaderClassName, DataReader dataReader) throws NoSuchMethodException,
			ClassNotFoundException, IllegalAccessException, InvocationTargetException {
		Class<?>[] cArg = new Class[1];
		cArg[0] = DataReader.class;
		Method constructor = Class.forName(recordReaderClassName).getMethod("usingDataReader",
				cArg);
		return (RecordReader) constructor.invoke(null, dataReader);
	}

	private static FromDbToCoraConverter createFromDbToCoraConverter(String[] args)
			throws InstantiationException, IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, ClassNotFoundException {
		String fromDbToCoraConverterClassName = args[5];
		Class<?>[] cArg = new Class[1];
		cArg[0] = DataReader.class;
		Constructor<?> constructor = Class.forName(fromDbToCoraConverterClassName).getConstructor();
		return (FromDbToCoraConverter) constructor.newInstance();

	}

	private static RecordCreator createRecordCreator(String[] args)
			throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		DataUpdater dataUpdater = createDataUpdater(args);
		String recordCreatorClassName = args[6];
		return createRecordCreatorUsingClassNameAndDataReader(recordCreatorClassName, dataUpdater);
	}

	private static DataUpdater createDataUpdater(String[] args) {
		ParameterConnectionProviderImp connectionProvider = cretateConnectionProvider(args[7],
				args[8], args[9]);

		return DataUpdaterImp.usingSqlConnectionProvider(connectionProvider);
	}

	private static RecordCreator createRecordCreatorUsingClassNameAndDataReader(
			String recordReaderClassName, DataUpdater dataUpdater)
			throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		Class<?>[] cArg = new Class[1];
		cArg[0] = DataUpdater.class;
		Constructor<?> constructor = Class.forName(recordReaderClassName).getConstructor(cArg);
		return (RecordCreator) constructor.newInstance(dataUpdater);
	}

	private static DataToJsonConverterFactoryImp createDataToJsonConverterFactory() {
		JsonBuilderFactory orgJsonBuilderFactoryAdapter = new OrgJsonBuilderFactoryAdapter();
		return new DataToJsonConverterFactoryImp(orgJsonBuilderFactoryAdapter);
	}

	private static DivaImporter createDivaImporter(String[] args, RecordReader recordReader,
			FromDbToCoraConverter fromDbToCoraConverter, RecordCreator recordCreator,
			DataToJsonConverterFactory dataToJsonConverterFactory)
			throws InstantiationException, IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, ClassNotFoundException {
		Class<?>[] cArg = setUpDbImporterArguments();

		String importerClassName = args[0];
		Constructor<?> constructor = Class.forName(importerClassName).getConstructor(cArg);
		return (DivaImporter) constructor.newInstance(recordReader, fromDbToCoraConverter,
				recordCreator, dataToJsonConverterFactory);

	}

	private static Class<?>[] setUpDbImporterArguments() {
		Class<?>[] cArg = new Class[4];
		cArg[0] = RecordReader.class;
		cArg[1] = FromDbToCoraConverter.class;
		cArg[2] = RecordCreator.class;
		cArg[3] = DataToJsonConverterFactory.class;
		return cArg;
	}

}
