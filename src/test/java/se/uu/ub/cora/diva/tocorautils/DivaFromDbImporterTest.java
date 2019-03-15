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
package se.uu.ub.cora.diva.tocorautils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.client.CoraClientConfig;
import se.uu.ub.cora.client.CoraClientFactoryImp;

public class DivaFromDbImporterTest {

	private String args[];

	@BeforeMethod
	private void beforeMethod() {

		args = new String[] { "someUserId", "someAppToken", "appTokenVerifierUrl", "baseUrl",
				"dbUser", "dbPassword", "dbUrl", "tableName",
				"se.uu.ub.cora.diva.tocorautils.FromDbToCoraFactorySpy" };
	}

	@Test
	public void testMainFactorsCorrectly() throws Exception {
		DivaFromDbImporter.main(args);
		FromDbToCoraFactorySpy fromDbToCoraFactory = (FromDbToCoraFactorySpy) DivaFromDbImporter
				.getInstance();
		assertTrue(fromDbToCoraFactory instanceof FromDbToCoraFactorySpy);

		CoraClientFactoryImp coraClientFactory = (CoraClientFactoryImp) fromDbToCoraFactory.coraClientFactory;
		assertTrue(coraClientFactory instanceof CoraClientFactoryImp);
		assertEquals(coraClientFactory.getAppTokenVerifierUrl(), "appTokenVerifierUrl");
		assertEquals(coraClientFactory.getBaseUrl(), "baseUrl");

		CoraClientConfig coraClientConfig = fromDbToCoraFactory.coraClientConfig;
		assertEquals(coraClientConfig.userId, args[0]);
		assertEquals(coraClientConfig.appToken, args[1]);
		assertEquals(coraClientConfig.appTokenVerifierUrl, args[2]);
		assertEquals(coraClientConfig.coraUrl, args[3]);

		DbConfig dbConfig = fromDbToCoraFactory.dbConfig;
		assertNotNull(dbConfig);
		assertEquals(dbConfig.userId, args[4]);
		assertEquals(dbConfig.password, args[5]);
		assertEquals(dbConfig.url, args[6]);
	}

	@Test
	public void testCallingImport() throws Exception {
		DivaFromDbImporter.main(args);
		FromDbToCoraFactorySpy fromDbToCoraFactory = (FromDbToCoraFactorySpy) DivaFromDbImporter
				.getInstance();
		FromDbToCoraSpy fromDbToCoraSpy = fromDbToCoraFactory.factored;

		assertTrue(fromDbToCoraSpy.importCountriesHasBeenCalled);
		assertEquals(fromDbToCoraSpy.usedTableName, "tableName");
	}

	@Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "failed during import in CountryFromDbToCoraSpy\n"
			+ "ERROR: failed again during import of CountryFromDbToCoraSpy")
	public void testErrorsDuringImport() throws Exception {
		String args[] = new String[] { "someUserId", "someAppToken", "appTokenVerifierUrl",
				"baseUrl", "dbUrl", "dbUser", "dbPassword", "tableName",
				"se.uu.ub.cora.diva.tocorautils.FromDbToCoraFactoryReturningErrorsSpy" };

		DivaFromDbImporter.main(args);
	}

	@Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = ""
			+ "se.uu.ub.cora.FromDbToCoraFactoryNOTFOUND")
	public void testFactoryClassNotFound() throws Exception {

		String args[] = new String[] { "someUserId", "someAppToken", "appTokenVerifierUrl",
				"baseUrl", "dbUrl", "dbUser", "dbPassword", "tableName",
				"se.uu.ub.cora.FromDbToCoraFactoryNOTFOUND" };

		DivaFromDbImporter.main(args);
	}
}
