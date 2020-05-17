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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataConverterFactoryImp;
import se.uu.ub.cora.diva.tocorautils.doubles.CoraClientFactorySpy;
import se.uu.ub.cora.diva.tocorautils.doubles.CoraClientSpy;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;

public class ChangeDataGroupBatchRunnerTest {

	@Test
	public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		Constructor<ChangeDataGroupBatchRunner> constructor = ChangeDataGroupBatchRunner.class
				.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		constructor.setAccessible(true);
		constructor.newInstance();
	}

	@Test
	public void testMainMethod() throws ClassNotFoundException, NoSuchMethodException,
			InvocationTargetException, InstantiationException, IllegalAccessException {
		String args[] = new String[] { "apptokenVerifierUrl", "baseUrl", "userId", "appToken",
				"se.uu.ub.cora.diva.tocorautils.doubles.CoraClientFactorySpy",
				"changerFactoryClassName", "se.uu.ub.cora.diva.tocorautils.JsonToClientDataSpy",
				"se.uu.ub.cora.diva.tocorautils.UpdaterSpy" };
		ChangeDataGroupBatchRunner.main(args);
		UpdaterSpy updaterSpy = (UpdaterSpy) ChangeDataGroupBatchRunner.updater;

		CoraClientFactorySpy clientFactorySpy = (CoraClientFactorySpy) ChangeDataGroupBatchRunner.coraClientFactory;
		assertEquals(clientFactorySpy.appTokenVerifierUrl, "apptokenVerifierUrl");
		assertEquals(clientFactorySpy.baseUrl, "baseUrl");

		CoraClientSpy factoredCoraClient = clientFactorySpy.factored;
		assertSame(updaterSpy.coraClient, factoredCoraClient);

		JsonToClientDataSpy jsonToClientData = (JsonToClientDataSpy) ChangeDataGroupBatchRunner.jsonToClientData;
		assertTrue(jsonToClientData.jsonParser instanceof OrgJsonParser);
		assertTrue(jsonToClientData.converterFactory instanceof JsonToDataConverterFactoryImp);

		assertSame(updaterSpy.jsonToClientData, jsonToClientData);

		// assertTrue(updaterSpy.updateRecordsCalled);
		// assertEquals(finderSpy.url, "http://localhost:8080/therest/rest/record/recordType");
		//
		// ModifierSpy modifierSpy = (ModifierSpy) RecordTypePGroupIdsModifierBatchRunner.modifier;
		// assertEquals(modifierSpy.recordTypes.get(0), "someId");
		//
		// }
		//
		// @Test
		// public void testMainMethodErrorWhenModifyingFound()
		// throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
		// InstantiationException, IllegalAccessException {
		//
		// String args[] = new String[] { "http://localhost:8080/therest/rest/record/",
		// "se.uu.ub.cora.batchrunner.find.HttpHandlerFactorySpy",
		// "se.uu.ub.cora.batchrunner.find.FinderSpy",
		// "se.uu.ub.cora.batchrunner.change.ModifierWithErrorSpy" };
		//
		// RecordTypePGroupIdsModifierBatchRunner.main(args);
		// FinderSpy finderSpy = (FinderSpy) RecordTypePGroupIdsModifierBatchRunner.finder;
		// assertTrue(finderSpy.findRecordsCalled);
		// assertEquals(finderSpy.url, "http://localhost:8080/therest/rest/record/recordType");
		//
	}

}
