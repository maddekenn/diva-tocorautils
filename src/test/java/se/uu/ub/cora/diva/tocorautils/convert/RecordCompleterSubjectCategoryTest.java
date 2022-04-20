package se.uu.ub.cora.diva.tocorautils.convert;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.diva.tocorautils.doubles.RecordReaderFactorySpy;
import se.uu.ub.cora.diva.tocorautils.importing.DatabaseFacadeSpy;

public class RecordCompleterSubjectCategoryTest {

	private RecordReaderFactorySpy recordReaderFactory;
	private DatabaseFacadeSpy databaseFacade;
	private String pathToFile = "src/test/resources/subjectCategoryParents.json";
	private RecordCompleter recordCompleter;

	@BeforeMethod
	public void setUp() {
		databaseFacade = new DatabaseFacadeSpy();
		recordCompleter = RecordCompleterSubjectCategory.usingDatabaseFacade(databaseFacade);
	}

	@Test
	public void testNoParent() {
		List<ClientDataGroup> dataGroups = new ArrayList<>();
		ClientDataGroup dataGroup = createClientDataGroupWithRecordId("10011");
		dataGroups.add(dataGroup);

		List<ClientDataGroup> completedMetadata = recordCompleter.completeMetadata(dataGroups,
				pathToFile);

		assertTrue(completedMetadata.isEmpty());

	}

	@Test
	public void testMultipleParentsForOneSubject() {
		List<ClientDataGroup> dataGroups = new ArrayList<>();
		dataGroups.add(createClientDataGroupWithRecordId("10011"));
		dataGroups.add(createClientDataGroupWithRecordId("1001"));

		List<ClientDataGroup> completedMetadata = recordCompleter.completeMetadata(dataGroups,
				pathToFile);
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

		// RecordCompleter recordCompleter = RecordCompleterSubjectCategory
		// .usingDatabaseFacade(recordReaderFactory);
		//
		// String completedMetadataJson = recordCompleter.completeMetadata(dataGroup);
		//
		// RecordReaderSpy factoredReader = recordReaderFactory.factored;
		// assertEquals(factoredReader.usedTableNames.get(0), "subject_parent_view");
		// assertEquals(factoredReader.usedConditions.get("parent_subject_id"),
		// "someSubjectWithParentId");
		//
		// assertEquals(completedMetadataJson,
		// "{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"someSubjectWithParentId\"}],\"name\":\"recordInfo\"},{\"repeatId\":\"0\",\"children\":[{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"nationalSubjectCategory\"},{\"name\":\"linkedRecordId\",\"value\":\"someParent0\"}],\"name\":\"nationalSubjectCategory\"}],\"name\":\"nationalSubjectCategoryParent\"},{\"repeatId\":\"1\",\"children\":[{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"nationalSubjectCategory\"},{\"name\":\"linkedRecordId\",\"value\":\"someParent1\"}],\"name\":\"nationalSubjectCategory\"}],\"name\":\"nationalSubjectCategoryParent\"}],\"name\":\"nationalSubjectCategory\"}");

	}

	private ClientDataGroup createClientDataGroupWithRecordId(String id) {
		ClientDataGroup dataGroup = ClientDataGroup.withNameInData("nationalSubjectCategory");
		ClientDataGroup recordInfo = ClientDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(ClientDataAtomic.withNameInDataAndValue("id", id));
		dataGroup.addChild(recordInfo);
		return dataGroup;
	}

}
