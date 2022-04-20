package se.uu.ub.cora.diva.tocorautils.convert;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;

public class RecordCompleterSubjectCategoryTest {

	private String pathToFile = "src/test/resources/subjectCategoryParents.json";
	private RecordCompleter recordCompleter;

	@BeforeMethod
	public void setUp() {
		recordCompleter = RecordCompleterSubjectCategory.usingPathToFile(pathToFile);
	}

	@Test
	public void testNoParent() {
		List<ClientDataGroup> dataGroups = new ArrayList<>();
		ClientDataGroup dataGroup = createClientDataGroupWithRecordId("10011");
		dataGroups.add(dataGroup);

		List<ClientDataGroup> completedMetadata = recordCompleter.completeMetadata(dataGroups);

		assertTrue(completedMetadata.isEmpty());

	}

	@Test
	public void testMultipleParentsForOneSubject() {
		List<ClientDataGroup> dataGroups = new ArrayList<>();
		dataGroups.add(createClientDataGroupWithRecordId("10011"));
		dataGroups.add(createClientDataGroupWithRecordId("1001"));

		List<ClientDataGroup> completedMetadata = recordCompleter.completeMetadata(dataGroups);
		assertEquals(completedMetadata.size(), 1);
		ClientDataGroup completedDataGroup = completedMetadata.get(0);
		List<ClientDataGroup> parents = completedDataGroup
				.getAllGroupsWithNameInData("nationalSubjectCategoryParent");

		assertEquals(parents.size(), 4);

		assertCorrectParent(parents, 0, "1002");
		assertCorrectParent(parents, 1, "1003");
		assertCorrectParent(parents, 2, "1004");
		assertCorrectParent(parents, 3, "1005");

	}

	private void assertCorrectParent(List<ClientDataGroup> parents, int index, String parentId) {
		ClientDataGroup parent = parents.get(index);
		ClientDataGroup parentLink = parent.getFirstGroupWithNameInData("nationalSubjectCategory");
		assertEquals(parentLink.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"nationalSubjectCategory");
		assertEquals(parentLink.getFirstAtomicValueWithNameInData("linkedRecordId"), parentId);
		assertEquals(parent.getRepeatId(), String.valueOf(index));
	}

	@Test
	public void testMultipleParentsForMultipleSubjects() {
		List<ClientDataGroup> dataGroups = new ArrayList<>();
		dataGroups.add(createClientDataGroupWithRecordId("59"));
		dataGroups.add(createClientDataGroupWithRecordId("1001"));
		dataGroups.add(createClientDataGroupWithRecordId("550"));
		dataGroups.add(createClientDataGroupWithRecordId("55000000"));

		List<ClientDataGroup> completedDataGroups = recordCompleter.completeMetadata(dataGroups);
		assertEquals(completedDataGroups.size(), 3);

		List<ClientDataGroup> parents = extractParentsUsingIndex(completedDataGroups, 0);
		assertEquals(parents.size(), 2);
		assertCorrectParent(parents, 0, "58");
		assertCorrectParent(parents, 1, "61");

		List<ClientDataGroup> parents2 = extractParentsUsingIndex(completedDataGroups, 1);
		assertEquals(parents2.size(), 4);
		assertCorrectParent(parents2, 0, "1002");
		assertCorrectParent(parents2, 1, "1003");
		assertCorrectParent(parents2, 2, "1004");
		assertCorrectParent(parents2, 3, "1005");

		List<ClientDataGroup> parents3 = extractParentsUsingIndex(completedDataGroups, 2);
		assertEquals(parents3.size(), 1);
		assertCorrectParent(parents3, 0, "551");
	}

	private List<ClientDataGroup> extractParentsUsingIndex(
			List<ClientDataGroup> completedDataGroups, int index) {
		ClientDataGroup completedDataGroup = completedDataGroups.get(index);
		List<ClientDataGroup> parents = completedDataGroup
				.getAllGroupsWithNameInData("nationalSubjectCategoryParent");
		return parents;
	}

	private ClientDataGroup createClientDataGroupWithRecordId(String id) {
		ClientDataGroup dataGroup = ClientDataGroup.withNameInData("nationalSubjectCategory");
		ClientDataGroup recordInfo = ClientDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(ClientDataAtomic.withNameInDataAndValue("id", id));
		dataGroup.addChild(recordInfo);
		return dataGroup;
	}

}
