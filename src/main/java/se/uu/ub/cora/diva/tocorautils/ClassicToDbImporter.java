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
import se.uu.ub.cora.connection.ParameterConnectionProviderImp;
import se.uu.ub.cora.diva.tocorautils.importing.DivaImporter;
import se.uu.ub.cora.diva.tocorautils.importing.FromDbToCoraConverter;
import se.uu.ub.cora.sqldatabase.DataReader;
import se.uu.ub.cora.sqldatabase.DataReaderImp;
import se.uu.ub.cora.sqldatabase.RecordCreator;
import se.uu.ub.cora.sqldatabase.RecordReader;

public class ClassicToDbImporter {

	protected static DivaImporter divaImporter;

	public static void main(String[] args) throws InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		String importerClassName = args[0];
		RecordReader recordReader = createRecordReader(args);

		String fromDbToCoraConverterClassName = args[5];
		FromDbToCoraConverter fromDbToCoraConverter = createFromDbToCoraConverter(
				fromDbToCoraConverterClassName);
		divaImporter = createDivaImporter(importerClassName, recordReader, fromDbToCoraConverter);

	}

	private static FromDbToCoraConverter createFromDbToCoraConverter(
			String fromDbToCoraConverterClassName)
			throws InstantiationException, IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, ClassNotFoundException {
		Class<?>[] cArg = new Class[1];
		cArg[0] = DataReader.class;
		Constructor<?> constructor = Class.forName(fromDbToCoraConverterClassName).getConstructor();
		return (FromDbToCoraConverter) constructor.newInstance();

	}

	private static RecordReader createRecordReader(String[] args) throws NoSuchMethodException,
			ClassNotFoundException, IllegalAccessException, InvocationTargetException {
		DataReader dataReader = createDataReader(args);
		String recordReaderClassName = args[1];
		return createRecordReaderUsingClassNameAndDataReader(recordReaderClassName, dataReader);
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

	private static DataReader createDataReader(String[] args) {
		String dbToReadFromUrl = args[2];
		String dbToReadFromUserId = args[3];
		String dbToReadFromPassword = args[4];

		ParameterConnectionProviderImp connectionProvider = ParameterConnectionProviderImp
				.usingUriAndUserAndPassword(dbToReadFromUrl, dbToReadFromUserId,
						dbToReadFromPassword);
		return DataReaderImp.usingSqlConnectionProvider(connectionProvider);
	}

	private static DivaImporter createDivaImporter(String importerClassName,
			RecordReader recordReader, FromDbToCoraConverter fromDbToCoraConverter)
			throws InstantiationException, IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, ClassNotFoundException {
		Class<?>[] cArg = new Class[4];
		cArg[0] = RecordReader.class;
		cArg[1] = FromDbToCoraConverter.class;
		cArg[2] = RecordCreator.class;
		cArg[3] = DataToJsonConverterFactory.class;
		Constructor<?> constructor = Class.forName(importerClassName).getConstructor(cArg);
		return (DivaImporter) constructor.newInstance(recordReader, fromDbToCoraConverter, null,
				null);

	}

	// static void createCoraClientConfig(String[] args) {
	// String userId = args[0];
	// String appToken = args[1];
	// String appTokenVerifierUrl = args[2];
	// String coraUrl = args[3];
	// coraClientConfig = new CoraClientConfig(userId, appToken, appTokenVerifierUrl, coraUrl);
	// }

}
