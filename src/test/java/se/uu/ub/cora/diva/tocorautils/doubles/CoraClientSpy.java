/*
 * Copyright 2018 Uppsala University Library
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
package se.uu.ub.cora.diva.tocorautils.doubles;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.client.CoraClient;
import se.uu.ub.cora.client.CoraClientException;

public class CoraClientSpy implements CoraClient {

	public List<String> createdRecordTypes = new ArrayList<>();
	public List<String> updatedRecordTypes = new ArrayList<>();
	public List<String> updatedRecordIds = new ArrayList<>();
	public List<String> jsonStrings = new ArrayList<>();

	@Override
	public String create(String recordType, String json) {

		if (json.contains("FAIL")) {
			throw new CoraClientException("Failed to create record");
		}

		createdRecordTypes.add(recordType);
		jsonStrings.add(json);
		return "created " + json;
	}

	@Override
	public String read(String recordType, String recordId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String update(String recordType, String recordId, String json) {
		updatedRecordTypes.add(recordType);
		updatedRecordIds.add(recordId);
		jsonStrings.add(json);
		return null;
	}

	@Override
	public String delete(String recordType, String recordId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String readList(String recordType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String readIncomingLinks(String recordType, String recordId) {
		// TODO Auto-generated method stub
		return null;
	}

}
