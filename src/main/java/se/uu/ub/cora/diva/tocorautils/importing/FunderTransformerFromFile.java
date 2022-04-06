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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.diva.tocorautils.convert.FromDbToCoraConverter;
import se.uu.ub.cora.diva.tocorautils.convert.FromDbToCoraConverterFactory;

public class FunderTransformerFromFile implements DbToCoraTransformer {

	private String pathToFile;
	private FromDbToCoraConverterFactory converterFactory;

	public FunderTransformerFromFile(String pathToFile,
			FromDbToCoraConverterFactory converterFactory) {
		this.pathToFile = pathToFile;
		this.converterFactory = converterFactory;
	}

	@Override
	public List<ClientDataGroup> getConverted() {

		String jsonString;
		try {
			jsonString = Files.readString(Path.of(pathToFile));

			JSONArray records = new JSONArray(jsonString);

			Map<String, Object> row;
			for (Object dataRecord : records) {
				row = new HashMap<>();
				JSONObject jsonRow = (JSONObject) dataRecord;

				for (String key : jsonRow.keySet()) {
					Object value = jsonRow.get(key);

					row.put(key, value);
				}
				FromDbToCoraConverter converter = converterFactory.factor("funder");
				ClientDataGroup converted = converter.convertToClientDataGroupFromRowFromDb(row);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// List<List<String>> records = new ArrayList<>();
		// try (Scanner scanner = new Scanner(new File(pathToFile));) {
		// while (scanner.hasNextLine()) {
		// records.add(getRecordFromLine(scanner.nextLine()));
		// }
		// } catch (FileNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// List<String> columnNames = records.get(0);
		// // for (List<String> record : records) {
		// for (String columnName : records.get(0)) {
		//
		// }
		// // }
		return null;
	}

	private List<String> getRecordFromLine(String line) {
		List<String> values = new ArrayList<String>();
		try (Scanner rowScanner = new Scanner(line)) {
			rowScanner.useDelimiter(",");
			while (rowScanner.hasNext()) {
				values.add(rowScanner.next());
			}
		}
		return values;
	}

	public FromDbToCoraConverterFactory getConverterFactory() {
		return converterFactory;
	}

}
