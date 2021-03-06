/// *
// * Copyright 2018 Uppsala University Library
// *
// * This file is part of Cora.
// *
// * Cora is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * Cora is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with Cora. If not, see <http://www.gnu.org/licenses/>.
// */
// package se.uu.ub.cora.diva.tocorautils;
//
// import static org.testng.Assert.assertEquals;
//
// import org.testng.annotations.BeforeMethod;
// import org.testng.annotations.Test;
//
// import se.uu.ub.cora.tocorautils.doubles.FromDbToCoraConverterSpy;
// import se.uu.ub.cora.tocorautils.doubles.ListImporterSpy;
// import se.uu.ub.cora.tocorautils.doubles.RecordReaderFactorySpy;
// import se.uu.ub.cora.tocorautils.doubles.RecordReaderSpy;
// import se.uu.ub.cora.tocorautils.importing.ImportResult;
//
// public class ListFromDbToCoraTest {
//
// private FromDbToCoraConverterSpy toCoraConverter;
// private RecordReaderFactorySpy recordReaderFactory;
// private ListImporterSpy importer;
// private FromDbToCoraImp fromDbToCora;
//
// @BeforeMethod
// public void beforeMethod() {
// toCoraConverter = new FromDbToCoraConverterSpy();
// recordReaderFactory = new RecordReaderFactorySpy();
// importer = new ListImporterSpy();
// fromDbToCora = (FromDbToCoraImp) FromDbToCoraImp
// .usingRecordReaderFactoryAndDbToCoraConverterAndImporter(recordReaderFactory,
// toCoraConverter, importer);
// }
//
// @Test
// public void testGetters() {
// assertEquals(fromDbToCora.getRecordReaderFactory(), recordReaderFactory);
// assertEquals(fromDbToCora.getFromDbToCoraConverter(), toCoraConverter);
// assertEquals(fromDbToCora.getImporter(), importer);
// }
//
// @Test
// public void testImportFromTable() {
// ImportResult importResult = fromDbToCora.importFromTable("someTableName");
//
// RecordReaderSpy factoredReader = recordReaderFactory.factored;
// assertEquals(factoredReader.usedTableName, "someTableName");
//
// assertEquals(factoredReader.returnedList, toCoraConverter.rowsFromDb);
// assertEquals(toCoraConverter.returnedList2, importer.listOfConvertedRows);
//
// assertEquals(importResult.listOfFails.get(0), "failure from
/// ListImporterSpy");
// }
// }