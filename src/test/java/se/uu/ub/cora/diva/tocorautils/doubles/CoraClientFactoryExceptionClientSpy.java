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

import se.uu.ub.cora.javaclient.cora.CoraClient;
import se.uu.ub.cora.javaclient.cora.CoraClientFactory;

public class CoraClientFactoryExceptionClientSpy implements CoraClientFactory {

	public CoraClientExceptionSpy factored;
	public String userId;
	public String appToken;
	public String appTokenVerifierUrl;
	public String baseUrl;
	public String authToken;

	public static CoraClientFactory usingAppTokenVerifierUrlAndBaseUrl(String appTokenVerifierUrl,
			String baseUrl) {
		return new CoraClientFactoryExceptionClientSpy(appTokenVerifierUrl, baseUrl);
	}

	public CoraClientFactoryExceptionClientSpy(String appTokenVerifierUrl, String baseUrl) {
		this.appTokenVerifierUrl = appTokenVerifierUrl;
		this.baseUrl = baseUrl;
	}

	public CoraClientFactoryExceptionClientSpy() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public CoraClient factor(String userId, String appToken) {
		return null;
	}

	@Override
	public CoraClient factorUsingAuthToken(String authToken) {
		this.authToken = authToken;
		factored = new CoraClientExceptionSpy();
		return factored;
	}

}
