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

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;

public class NationalSubjectChanger implements DataGroupChanger {

	private static final String NATIONAL_SUBJECT_CATEGORY_NAME = "nationalSubjectCategoryName";

	@Override
	public ClientDataGroup change(ClientDataGroup orignalDataGroup) {
		replaceName(orignalDataGroup);
		replaceAlternativeName(orignalDataGroup);
		return orignalDataGroup;
	}

	private void replaceName(ClientDataGroup orignalDataGroup) {
		ClientDataGroup nameGroup = createNameGroup(orignalDataGroup);
		orignalDataGroup.addChild(nameGroup);
		orignalDataGroup.removeFirstChildWithNameInData(NATIONAL_SUBJECT_CATEGORY_NAME);
	}

	private ClientDataGroup createNameGroup(ClientDataGroup orignalDataGroup) {
		String name = orignalDataGroup
				.getFirstAtomicValueWithNameInData(NATIONAL_SUBJECT_CATEGORY_NAME);
		return createGroupWithNameValueAndLanguage("name", name, "sv");
	}

	private ClientDataGroup createGroupWithNameValueAndLanguage(String nameInData, String name,
			String langauge) {
		ClientDataGroup nameGroup = ClientDataGroup.withNameInData(nameInData);
		nameGroup.addChild(
				ClientDataAtomic.withNameInDataAndValue(NATIONAL_SUBJECT_CATEGORY_NAME, name));
		nameGroup.addChild(ClientDataAtomic.withNameInDataAndValue("language", langauge));
		return nameGroup;
	}

	private void replaceAlternativeName(ClientDataGroup orignalDataGroup) {
		ClientDataGroup alternativeNameGroup = createAlternativeNameGroup(orignalDataGroup);
		orignalDataGroup.addChild(alternativeNameGroup);
		orignalDataGroup.removeFirstChildWithNameInData("nationalSubjectCategoryAlternativeName");
	}

	private ClientDataGroup createAlternativeNameGroup(ClientDataGroup orignalDataGroup) {
		String name = orignalDataGroup
				.getFirstAtomicValueWithNameInData("nationalSubjectCategoryAlternativeName");
		return createGroupWithNameValueAndLanguage("alternativeName", name, "en");
	}

}
