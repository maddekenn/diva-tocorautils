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

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.clientdata.ClientDataElement;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverter;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverterFactory;

public class DataToJsonConverterFactorySpy implements DataToJsonConverterFactory {

	public List<ClientDataElement> clientDataElements = new ArrayList<>();
	public List<DataToJsonConverter> factoredConverters = new ArrayList<>();

	@Override
	public DataToJsonConverter createForClientDataElement(ClientDataElement clientDataElement) {
		clientDataElements.add(clientDataElement);
		DataToJsonConverter factored = new DataToJsonConverterSpy();
		factoredConverters.add(factored);
		return factored;
	}

	@Override
	public DataToJsonConverter createForClientDataElementIncludingActionLinks(
			ClientDataElement clientDataElement, boolean includeActionLinks) {
		// TODO Auto-generated method stub
		return null;
	}

}