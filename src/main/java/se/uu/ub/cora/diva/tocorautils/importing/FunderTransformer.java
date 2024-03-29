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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.diva.tocorautils.convert.FromDbToCoraConverter;
import se.uu.ub.cora.diva.tocorautils.convert.FromDbToCoraConverterFactory;
import se.uu.ub.cora.sqldatabase.DatabaseFacade;
import se.uu.ub.cora.sqldatabase.Row;

public class FunderTransformer implements DbToCoraTransformer {

	private DatabaseFacade databaseFacade;
	private FromDbToCoraConverterFactory converterFactory;

	public static FunderTransformer usingDatabaseFacadeAndFromDbConverterFactory(
			DatabaseFacade databaseFacade, FromDbToCoraConverterFactory converterFactory) {
		return new FunderTransformer(databaseFacade, converterFactory);
	}

	private FunderTransformer(DatabaseFacade databaseFacade,
			FromDbToCoraConverterFactory converterFactory) {
		this.databaseFacade = databaseFacade;
		this.converterFactory = converterFactory;
	}

	@Override
	public List<ClientDataGroup> getConverted() {
		String sql = createSqlForFindingFunders();
		List<Row> rows = databaseFacade.readUsingSqlAndValues(sql, Collections.emptyList());
		return convertGroups(rows);
	}

	private String createSqlForFindingFunders() {
		return "select f.funder_id, f.closed_date, f.funder_name, f.funder_name_locale, "
				+ "f.acronym, f.orgnumber, f.doi, fn.funder_name as alternative_name, fn.locale "
				+ "as alternative_name_locale from funder f left join funder_name fn "
				+ "on f.funder_id = fn.funder_id";
	}

	private List<ClientDataGroup> convertGroups(List<Row> rows) {
		List<ClientDataGroup> convertedGroups = new ArrayList<>();
		for (Row row : rows) {
			convertAndAddGroup(convertedGroups, row);
		}
		return convertedGroups;
	}

	private void convertAndAddGroup(List<ClientDataGroup> convertedGroups, Row row) {
		Set<String> columnSet = row.columnSet();
		Map<String, Object> columnValues = new HashMap<>();
		for (String key : columnSet) {
			columnValues.put(key, row.getValueByColumn(key));
		}

		FromDbToCoraConverter converter = converterFactory.factor("funder");
		ClientDataGroup converted = converter.convertToClientDataGroupFromRowFromDb(columnValues);
		convertedGroups.add(converted);
	}

	public DatabaseFacade getDatabaseFacade() {
		return databaseFacade;
	}

	public FromDbToCoraConverterFactory getConverterFactory() {
		return converterFactory;
	}

}
