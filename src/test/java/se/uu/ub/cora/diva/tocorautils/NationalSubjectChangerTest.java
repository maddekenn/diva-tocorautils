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
import static org.testng.Assert.assertFalse;

import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;

public class NationalSubjectChangerTest {

	@Test
	public void testInit() {
		ClientDataGroup orignalDataGroup = createOriginalDataGroup();
		String nameValue = orignalDataGroup
				.getFirstAtomicValueWithNameInData("nationalSubjectCategoryName");
		String alternativeNameValue = orignalDataGroup
				.getFirstAtomicValueWithNameInData("nationalSubjectCategoryAlternativeName");

		DataGroupChanger changer = new NationalSubjectChanger();
		ClientDataGroup clientDataGroup = changer.change(orignalDataGroup);

		assertNameAsGroupHasBeenAdded(nameValue, clientDataGroup);
		assertAlternativeNameAsGroupHasBeenAdded(alternativeNameValue, clientDataGroup);

		assertFalse(clientDataGroup.containsChildWithNameInData("nationalSubjectCategoryName"));
		assertFalse(clientDataGroup
				.containsChildWithNameInData("nationalSubjectCategoryAlternativeName"));
	}

	private void assertNameAsGroupHasBeenAdded(String nameValue, ClientDataGroup clientDataGroup) {
		ClientDataGroup nameGroup = clientDataGroup.getFirstGroupWithNameInData("name");

		assertEquals(nameGroup.getFirstAtomicValueWithNameInData("nationalSubjectCategoryName"),
				nameValue);
		assertEquals(nameGroup.getFirstAtomicValueWithNameInData("language"), "sv");
	}

	private void assertAlternativeNameAsGroupHasBeenAdded(String alternativeNameValue,
			ClientDataGroup clientDataGroup) {
		ClientDataGroup alternativeNameGroup = clientDataGroup
				.getFirstGroupWithNameInData("alternativeName");

		assertEquals(alternativeNameGroup.getFirstAtomicValueWithNameInData(
				"nationalSubjectCategoryName"), alternativeNameValue);
		assertEquals(alternativeNameGroup.getFirstAtomicValueWithNameInData("language"), "en");
	}

	private ClientDataGroup createOriginalDataGroup() {
		ClientDataGroup orignalDataGroup = ClientDataGroup
				.withNameInData("nationalSubjectCategory");
		addAtomicName(orignalDataGroup);
		addAtomicAlternativeName(orignalDataGroup);

		return orignalDataGroup;
	}

	private void addAtomicAlternativeName(ClientDataGroup orignalDataGroup) {
		ClientDataAtomic alternativeNameAtomic = ClientDataAtomic
				.withNameInDataAndValue("nationalSubjectCategoryAlternativeName", "Chemistry");
		orignalDataGroup.addChild(alternativeNameAtomic);
	}

	private void addAtomicName(ClientDataGroup orignalDataGroup) {
		ClientDataAtomic nameAtomic = ClientDataAtomic
				.withNameInDataAndValue("nationalSubjectCategoryName", "Kemi");
		orignalDataGroup.addChild(nameAtomic);
	}

}
