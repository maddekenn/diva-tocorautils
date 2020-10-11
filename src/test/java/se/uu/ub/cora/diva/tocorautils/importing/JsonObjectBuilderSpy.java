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

import se.uu.ub.cora.json.builder.JsonArrayBuilder;
import se.uu.ub.cora.json.builder.JsonObjectBuilder;
import se.uu.ub.cora.json.parser.JsonObject;

public class JsonObjectBuilderSpy implements JsonObjectBuilder {

	public String jsonToReturn = "someJsonFromSpy";
	public JsonObjectSpy returnedJsonObject;

	@Override
	public void addKeyString(String key, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addKeyJsonObjectBuilder(String key, JsonObjectBuilder jsonObjectBuilder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addKeyJsonArrayBuilder(String key, JsonArrayBuilder jsonArrayBuilder) {
		// TODO Auto-generated method stub

	}

	@Override
	public JsonObject toJsonObject() {
		returnedJsonObject = new JsonObjectSpy();
		return returnedJsonObject;
	}

	@Override
	public String toJsonFormattedString() {
		return jsonToReturn;
	}

	@Override
	public String toJsonFormattedPrettyString() {
		// TODO Auto-generated method stub
		return null;
	}

}
