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
package se.uu.ub.cora.diva.tocorautils.convert;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import se.uu.ub.cora.diva.tocorautils.NotImplementedException;

public class FromDbToCoraConverterFactoryTest {

	@Test
	public void testFactorFunderConverter() {
		FromDbToCoraConverterFactoryImp factory = new FromDbToCoraConverterFactoryImp();
		FromDbToCoraConverter converter = factory.factor("funder");
		assertTrue(converter instanceof FromDbToCoraFunderConverter);
	}

	@Test
	public void testFactorNationalSubjectCategoryConverter() {
		FromDbToCoraConverterFactoryImp factory = new FromDbToCoraConverterFactoryImp();
		FromDbToCoraConverter converter = factory.factor("nationalSubjectCategory");
		assertTrue(converter instanceof FromDbToCoraSubjectCategoryConverter);
	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "Converter not implemented for notImplementedType")
	public void testTypeNotImplemented() {
		FromDbToCoraConverterFactoryImp factory = new FromDbToCoraConverterFactoryImp();
		factory.factor("notImplementedType");

	}

}
