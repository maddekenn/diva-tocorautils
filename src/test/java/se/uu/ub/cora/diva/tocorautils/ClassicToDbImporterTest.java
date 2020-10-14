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

import se.uu.ub.cora.connection.ParameterConnectionProviderImp;
import se.uu.ub.cora.diva.tocorautils.doubles.RecordReaderSpy;
import se.uu.ub.cora.diva.tocorautils.importing.FromDbToCoraConverterSpy;
import se.uu.ub.cora.diva.tocorautils.importing.RecordCreatorSpy;
import se.uu.ub.cora.sqldatabase.DataReaderImp;
import se.uu.ub.cora.sqldatabase.DataUpdaterImp;

public class ClassicToDbImporterTest {

	private String args[];

	// arg[] = ClassName for DivaImporter (main class to run), for example
	// EducationalProgramImporter
	// args[] = className for RecordReader, for example RecordReaderImp
	// args[] = url to database to read from
	// args[] = userId for database to read from
	// args[] = password for database to read from
	// args[] = FromDbToCoraConverter, converter for the data read from database, for example
	// FromDbEducationalProgramConverter

	// FromDbToCoraConverterSpy

	@BeforeMethod
	private void beforeMethod() {
		args = new String[] { "se.uu.ub.cora.diva.tocorautils.DivaImporterSpy",
				"se.uu.ub.cora.diva.tocorautils.doubles.RecordReaderSpy", "someDbToReadFromUrl",
				"someDbToReadFromUserId", "someDbToReadFromPassword",
				"se.uu.ub.cora.diva.tocorautils.importing.FromDbToCoraConverterSpy" };
	}

	@Test
	public void testInit() throws InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		ClassicToDbImporter.main(args);
		DivaImporterSpy divaImporter = (DivaImporterSpy) ClassicToDbImporter.divaImporter;
		assertNotNull(divaImporter);

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
		// DataReaderImp dataReader = (DataReaderImp) recordCreator.dataReader;
		// ParameterConnectionProviderImp sqlConnectionProvider = (ParameterConnectionProviderImp)
		// dataReader
		// .getSqlConnectionProvider();

		// Field declaredUrlField = sqlConnectionProvider.getClass().getDeclaredField("url");
		// declaredUrlField.setAccessible(true);
		// String setUrl = (String) declaredUrlField.get(sqlConnectionProvider);
		// assertEquals(setUrl, "someDbToReadFromUrl");
		//
		// Field declaredUserField = sqlConnectionProvider.getClass().getDeclaredField("user");
		// declaredUserField.setAccessible(true);
		// String userId = (String) declaredUserField.get(sqlConnectionProvider);
		// assertEquals(userId, "someDbToReadFromUserId");
		//
		// Field declaredPasswordField =
		// sqlConnectionProvider.getClass().getDeclaredField("password");
		// declaredPasswordField.setAccessible(true);
		// String password = (String) declaredPasswordField.get(sqlConnectionProvider);
		// assertEquals(password, "someDbToReadFromPassword");
	}

}
