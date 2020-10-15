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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverterFactoryImp;
import se.uu.ub.cora.connection.ParameterConnectionProviderImp;
import se.uu.ub.cora.diva.tocorautils.doubles.RecordReaderSpy;
import se.uu.ub.cora.diva.tocorautils.importing.FromDbToCoraConverterSpy;
import se.uu.ub.cora.diva.tocorautils.importing.RecordCreatorSpy;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;
import se.uu.ub.cora.sqldatabase.DataReaderImp;
import se.uu.ub.cora.sqldatabase.DataUpdaterImp;

public class ClassicToDbImporterTest {

	private String args[];

	// arg[0] = ClassName for DivaImporter (main class to run), for example
	// EducationalProgramImporter
	// args[1] = className for RecordReader, for example RecordReaderImp
	// args[2] = url to database to read from
	// args[3] = userId for database to read from
	// args[4] = password for database to read from
	// args[5] = FromDbToCoraConverter, converter for the data read from database, for example
	// FromDbEducationalProgramConverter
	// args[6] className for RecordCreator, for example RecordCreatorImp
	// args[7] = url to database to create from
	// args[8] = userId for database to create from
	// args[9] = password for database to create from

	// "se.uu.ub.cora.diva.tocorautils.importing.EducationalProgramImporter"
	// "se.uu.ub.cora.sqldatabase.RecordReaderImp"
	// "jdbc:postgresql://diva-cora-docker-postgresql:5432/diva" "diva" "diva"
	// "se.uu.ub.cora.diva.tocorautils.importing.FromDbEducationalProgramConverter"
	// "se.uu.ub.cora.sqldatabase.RecordCreatorImp"
	// "jdbc:postgresql://diva-cora-metadata-docker-postgresql:5432/diva" "diva" "diva"

	@BeforeMethod
	private void beforeMethod() {
		args = new String[] { "se.uu.ub.cora.diva.tocorautils.DivaImporterSpy",
				"se.uu.ub.cora.diva.tocorautils.doubles.RecordReaderSpy", "someDbToReadFromUrl",
				"someDbToReadFromUserId", "someDbToReadFromPassword",
				"se.uu.ub.cora.diva.tocorautils.importing.FromDbToCoraConverterSpy",
				"se.uu.ub.cora.diva.tocorautils.importing.RecordCreatorSpy",
				"someDbToCreateFromUrl", "someDbToCreateFromUserId", "someDbToCreateFromPassword" };
	}

	@Test
	public void testInit() throws InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		ClassicToDbImporter.main(args);
		DivaImporterSpy divaImporter = (DivaImporterSpy) ClassicToDbImporter.divaImporter;
		assertNotNull(divaImporter);

	}

	@Test
	public void testImport() throws InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		ClassicToDbImporter.main(args);
		DivaImporterSpy divaImporter = (DivaImporterSpy) ClassicToDbImporter.divaImporter;
		assertTrue(divaImporter.importDataWasCalled);

	}

	@Test
	public void testRecordReader()
			throws InstantiationException, IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, ClassNotFoundException, NoSuchFieldException, SecurityException {
		ClassicToDbImporter.main(args);
		DivaImporterSpy divaImporter = (DivaImporterSpy) ClassicToDbImporter.divaImporter;
		RecordReaderSpy recordReader = (RecordReaderSpy) divaImporter.recordReader;
		assertTrue(recordReader.dataReader instanceof DataReaderImp);
		DataReaderImp dataReader = (DataReaderImp) recordReader.dataReader;
		ParameterConnectionProviderImp sqlConnectionProvider = (ParameterConnectionProviderImp) dataReader
				.getSqlConnectionProvider();

		Field declaredUrlField = sqlConnectionProvider.getClass().getDeclaredField("url");
		declaredUrlField.setAccessible(true);
		String setUrl = (String) declaredUrlField.get(sqlConnectionProvider);
		assertEquals(setUrl, "someDbToReadFromUrl");

		Field declaredUserField = sqlConnectionProvider.getClass().getDeclaredField("user");
		declaredUserField.setAccessible(true);
		String userId = (String) declaredUserField.get(sqlConnectionProvider);
		assertEquals(userId, "someDbToReadFromUserId");

		Field declaredPasswordField = sqlConnectionProvider.getClass().getDeclaredField("password");
		declaredPasswordField.setAccessible(true);
		String password = (String) declaredPasswordField.get(sqlConnectionProvider);
		assertEquals(password, "someDbToReadFromPassword");
	}

	@Test
	public void testFromDbToCoraConverter()
			throws InstantiationException, IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, ClassNotFoundException, NoSuchFieldException, SecurityException {
		ClassicToDbImporter.main(args);
		DivaImporterSpy divaImporter = (DivaImporterSpy) ClassicToDbImporter.divaImporter;

		assertTrue(divaImporter.fromDbCoraDataConverter instanceof FromDbToCoraConverterSpy);

	}

	@Test
	public void testRecordCreator()
			throws InstantiationException, IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, ClassNotFoundException, NoSuchFieldException, SecurityException {

		ClassicToDbImporter.main(args);
		DivaImporterSpy divaImporter = (DivaImporterSpy) ClassicToDbImporter.divaImporter;
		RecordCreatorSpy recordCreator = (RecordCreatorSpy) divaImporter.recordCreator;
		assertTrue(recordCreator.dataUpdater instanceof DataUpdaterImp);
		DataUpdaterImp dataUpdater = (DataUpdaterImp) recordCreator.dataUpdater;
		ParameterConnectionProviderImp sqlConnectionProvider = (ParameterConnectionProviderImp) dataUpdater
				.getSqlConnectionProvider();

		Field declaredUrlField = sqlConnectionProvider.getClass().getDeclaredField("url");
		declaredUrlField.setAccessible(true);
		String setUrl = (String) declaredUrlField.get(sqlConnectionProvider);
		assertEquals(setUrl, "someDbToCreateFromUrl");

		Field declaredUserField = sqlConnectionProvider.getClass().getDeclaredField("user");
		declaredUserField.setAccessible(true);
		String userId = (String) declaredUserField.get(sqlConnectionProvider);
		assertEquals(userId, "someDbToCreateFromUserId");

		Field declaredPasswordField = sqlConnectionProvider.getClass().getDeclaredField("password");
		declaredPasswordField.setAccessible(true);
		String password = (String) declaredPasswordField.get(sqlConnectionProvider);
		assertEquals(password, "someDbToCreateFromPassword");
	}

	@Test
	public void testDataToJsonConverterFactory()
			throws InstantiationException, IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, ClassNotFoundException {
		ClassicToDbImporter.main(args);
		DivaImporterSpy divaImporter = (DivaImporterSpy) ClassicToDbImporter.divaImporter;
		DataToJsonConverterFactoryImp dataToJsonConverterFactory = (DataToJsonConverterFactoryImp) divaImporter.dataToJsonConverterFactory;
		assertTrue(dataToJsonConverterFactory
				.getJsonBuilderFactory() instanceof OrgJsonBuilderFactoryAdapter);
	}

}
