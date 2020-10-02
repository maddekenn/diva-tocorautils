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

import se.uu.ub.cora.diva.tocorautils.NotImplementedException;
import se.uu.ub.cora.diva.tocorautils.convert.FromDbToCoraJsonConverter;
import se.uu.ub.cora.diva.tocorautils.convert.FromDbToCoraConverterFactory;
import se.uu.ub.cora.sqldatabase.RecordCreator;
import se.uu.ub.cora.sqldatabase.RecordCreatorFactory;
import se.uu.ub.cora.sqldatabase.RecordReader;
import se.uu.ub.cora.sqldatabase.RecordReaderFactory;

public class DivaImporterFactoryImp implements DivaImporterFactory {

	private RecordReaderFactory recordReaderFactory;
	private FromDbToCoraConverterFactory converterFactory;
	private RecordCreatorFactory recordCreatorFactory;

	public DivaImporterFactoryImp(RecordReaderFactory readerFactory,
			FromDbToCoraConverterFactory converterFactory, RecordCreatorFactory creatorFactory) {
		this.recordReaderFactory = readerFactory;
		this.converterFactory = converterFactory;
		this.recordCreatorFactory = creatorFactory;
	}

	@Override
	public DivaImporter factor(String type) {
		if ("educationalProgram".equals(type)) {
			RecordReader recordReader = recordReaderFactory.factor();
			FromDbToCoraJsonConverter converter = converterFactory.factor("educationalProgram");
			RecordCreator recordCreator = recordCreatorFactory.factor();
			return new EducationalProgramImporter(recordReader, converter, recordCreator);
		}
		throw NotImplementedException.withMessage("No importer implemented for: " + type);
	}

}
