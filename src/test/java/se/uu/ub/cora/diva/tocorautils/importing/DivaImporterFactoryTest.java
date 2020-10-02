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
package se.uu.ub.cora.diva.tocorautils.importing;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.diva.tocorautils.FromDbToCoraConverterFactorySpy;
import se.uu.ub.cora.diva.tocorautils.NotImplementedException;
import se.uu.ub.cora.diva.tocorautils.doubles.FromDbToCoraConverterSpy;
import se.uu.ub.cora.diva.tocorautils.doubles.RecordReaderFactorySpy;
import se.uu.ub.cora.diva.tocorautils.doubles.RecordReaderSpy;

public class DivaImporterFactoryTest {

	private RecordReaderFactorySpy readerFactory;
	private FromDbToCoraConverterFactorySpy converterFactory;
	private DivaImporterFactory importerFactory;
	private RecordCreatorFactorySpy creatorFactory;

	@BeforeMethod
	public void setUp() {
		readerFactory = new RecordReaderFactorySpy();
		converterFactory = new FromDbToCoraConverterFactorySpy();
		creatorFactory = new RecordCreatorFactorySpy();
		importerFactory = new DivaImporterFactoryImp(readerFactory, converterFactory,
				creatorFactory);

	}

	@Test
	public void testFactorEducationalProgram() {
		String type = "educationalProgram";
		EducationalProgramImporter importer = (EducationalProgramImporter) importerFactory
				.factor(type);

		RecordReaderSpy factoredRecordReader = readerFactory.factored;

		assertNotNull(factoredRecordReader);
		assertSame(importer.getRecordReader(), factoredRecordReader);

		FromDbToCoraConverterSpy factoredConverter = converterFactory.factored;
		assertEquals(converterFactory.type, type);
		assertNotNull(factoredConverter);
		assertSame(importer.getFromDbToCoraConverter(), factoredConverter);

		RecordCreatorSpy creator = creatorFactory.factored;
		assertNotNull(creator);
		assertSame(importer.getRecordCreator(), creator);

	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "No importer implemented for: notImplementedType")
	public void testTypeNotImplemented() {
		importerFactory.factor("notImplementedType");

	}

}
