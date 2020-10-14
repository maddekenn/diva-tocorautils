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

import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverterFactory;
import se.uu.ub.cora.diva.tocorautils.importing.DivaImporter;
import se.uu.ub.cora.diva.tocorautils.importing.FromDbToCoraConverter;
import se.uu.ub.cora.diva.tocorautils.importing.FromDbToCoraConverterSpy;
import se.uu.ub.cora.sqldatabase.RecordCreator;
import se.uu.ub.cora.sqldatabase.RecordReader;

public class DivaImporterSpy implements DivaImporter {

	public RecordReader recordReader;
	public FromDbToCoraConverterSpy fromDbCoraDataConverter;
	public RecordCreator recordCreator;
	public DataToJsonConverterFactory toJsonConverterFactory;

	public DivaImporterSpy(RecordReader recordReader, FromDbToCoraConverter toCoraDataConverter,
			RecordCreator recordCreator, DataToJsonConverterFactory toJsonConverterFactory) {
		this.recordReader = recordReader;
		this.fromDbCoraDataConverter = (FromDbToCoraConverterSpy) toCoraDataConverter;
		this.recordCreator = recordCreator;
		this.toJsonConverterFactory = toJsonConverterFactory;
	}

	@Override
	public void importData() {
		// TODO Auto-generated method stub

	}

}
