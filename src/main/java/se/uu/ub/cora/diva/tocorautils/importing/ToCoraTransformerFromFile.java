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

import org.json.JSONArray;
import org.json.JSONObject;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.diva.tocorautils.convert.ConverterException;
import se.uu.ub.cora.diva.tocorautils.convert.FromDbToCoraConverter;
import se.uu.ub.cora.diva.tocorautils.convert.FromDbToCoraConverterFactory;

public class ToCoraTransformerFromFile implements DbToCoraTransformer {

	private String pathToFile;
	private FromDbToCoraConverterFactory converterFactory;
	private String type;

	public static ToCoraTransformerFromFile usingFilePathConverterFactoryAndType(String pathToFile,
			FromDbToCoraConverterFactory converterFactory, String type) {
		return new ToCoraTransformerFromFile(pathToFile, converterFactory, type);
	}

	private ToCoraTransformerFromFile(String pathToFile,
			FromDbToCoraConverterFactory converterFactory, String type) {
		this.pathToFile = pathToFile;
		this.converterFactory = converterFactory;
		this.type = type;
	}

	@Override
	public List<ClientDataGroup> getConverted() {
		JSONArray records = getRecords();
		return convertRecords(records);
	}

	private JSONArray getRecords() {
		String jsonString = readJsonFromFile();
		return new JSONArray(jsonString);
	}

	private String readJsonFromFile() {
		try {
			return Files.readString(Path.of(pathToFile));
		} catch (IOException e) {
			throw new ConverterException("Unable to parse json string using path: " + pathToFile);
		}
	}

	private List<ClientDataGroup> convertRecords(JSONArray records) {
		List<ClientDataGroup> convertedGroups = new ArrayList<>();
		for (Object dataRecord : records) {
			Map<String, Object> row = new HashMap<>();
			JSONObject jsonRow = (JSONObject) dataRecord;

			addColumnsToRow(row, jsonRow);
			convertRowAndAddToList(row, convertedGroups);
		}
		return convertedGroups;
	}

	private void addColumnsToRow(Map<String, Object> row, JSONObject jsonRow) {
		for (String key : jsonRow.keySet()) {
			if (nonNullValueForKey(jsonRow, key)) {
				Object value = jsonRow.get(key);
				row.put(key, value);
			}
		}
	}

	private boolean nonNullValueForKey(JSONObject jsonRow, String key) {
		return !jsonRow.isNull(key);
	}

	private void convertRowAndAddToList(Map<String, Object> row,
			List<ClientDataGroup> convertedGroups) {
		FromDbToCoraConverter converter = converterFactory.factor(type);
		ClientDataGroup converted = converter.convertToClientDataGroupFromRowFromDb(row);
		convertedGroups.add(converted);
	}

	public FromDbToCoraConverterFactory getConverterFactory() {
		return converterFactory;
	}

}
