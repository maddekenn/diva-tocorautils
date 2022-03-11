/*
 * Copyright 2022 Uppsala University Library
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
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.diva.tocorautils.FromDbToCoraConverterFactorySpy;
import se.uu.ub.cora.diva.tocorautils.doubles.FromDbToCoraConverterSpy;
import se.uu.ub.cora.sqldatabase.Row;

public class FunderTransformerTest {

	private DatabaseFacadeSpy databaseFacade;
	private FunderTransformer transformer;
	private FromDbToCoraConverterFactorySpy converterFactory;

	@BeforeMethod
	public void setUp() {
		databaseFacade = new DatabaseFacadeSpy();
		converterFactory = new FromDbToCoraConverterFactorySpy();
		transformer = FunderTransformer.usingDatabaseFacadeAndFromDbConverterFactory(databaseFacade, converterFactory);
	}

	@Test
	public void testInit() {
		assertSame(transformer.getDatabaseFacade(), databaseFacade);
		assertSame(transformer.getConverterFactory(), converterFactory);
	}

	@Test
	public void testReadFromDb() {
		transformer.getConverted();

		String expectedSql = "select f.funder_id, f.closed_date, f.funder_name, "
				+ "f.funder_name_locale, f.acronym, f.orgnumber, f.doi, "
				+ "fn.funder_name as alternative_name, fn.locale as alternative_name_locale "
				+ "from funder f left join funder_name fn on f.funder_id = fn.funder_id";
		assertTrue(databaseFacade.values.isEmpty());
		assertEquals(databaseFacade.sql, expectedSql);
	}

	@Test
	public void testRowsAreConverted() {
		List<Row> rowsToReturn = createListOfRows();
		databaseFacade.rowsToReturn = rowsToReturn;
		transformer.getConverted();
		assertEquals(converterFactory.type, "funder");
		List<FromDbToCoraConverterSpy> returnedConverterSpies = converterFactory.returnedConverterSpies;
		assertEquals(returnedConverterSpies.size(), 3);

		assertSame(returnedConverterSpies.get(0).row, rowsToReturn.get(0));
	}

	private List<Row> createListOfRows() {
		List<Row> rowsToReturn = new ArrayList<>();
		rowsToReturn.add(new RowSpy());
		rowsToReturn.add(new RowSpy());
		rowsToReturn.add(new RowSpy());
		return rowsToReturn;
	}

	@Test
	public void testConvertedRowsAreRetuned() {
		List<Row> rowsToReturn = createListOfRows();
		databaseFacade.rowsToReturn = rowsToReturn;
		List<ClientDataGroup> converted = transformer.getConverted();

		List<FromDbToCoraConverterSpy> converterSpies = converterFactory.returnedConverterSpies;

		assertSame(converted.get(0), converterSpies.get(0).dataGroupToReturn);
		assertSame(converted.get(1), converterSpies.get(1).dataGroupToReturn);
		assertSame(converted.get(2), converterSpies.get(2).dataGroupToReturn);
	}

}

// select f.funder_id, f.closed_date, f.funder_name, f.funder_name_locale, f.acronym,
// f.orgnumber, f.doi, fn.funder_name as alternative_name, fn.locale as alternative_name_locale
// from funder f left join funder_name fn ON f.funder_id = fn.funder_id;
