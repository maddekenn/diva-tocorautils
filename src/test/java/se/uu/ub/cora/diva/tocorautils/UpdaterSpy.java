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

import se.uu.ub.cora.batchrunner.JsonToClientData;
import se.uu.ub.cora.javaclient.cora.CoraClient;

public class UpdaterSpy implements Updater {

	public String type;
	public CoraClient coraClient;
	public JsonToClientData jsonToClientData;
	public DataGroupChangerFactory changerFactory;
	public boolean updateRecordsCalled = false;

	public static UpdaterSpy usingCoraClientJsonToClientDataAndChangerFactory(CoraClient coraClient,
			JsonToClientData jsonToClientData, DataGroupChangerFactory changerFactory) {
		return new UpdaterSpy(coraClient, jsonToClientData, changerFactory);
	}

	public UpdaterSpy(CoraClient coraClient, JsonToClientData jsonToClientData,
			DataGroupChangerFactory changerFactory) {
		this.coraClient = coraClient;
		this.jsonToClientData = jsonToClientData;
		this.changerFactory = changerFactory;
	}

	@Override
	public void update(String type) {
		updateRecordsCalled = true;
		this.type = type;

	}

}
