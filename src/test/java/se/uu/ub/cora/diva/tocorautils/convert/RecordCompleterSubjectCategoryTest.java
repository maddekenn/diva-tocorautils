package se.uu.ub.cora.diva.tocorautils.convert;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.diva.tocorautils.doubles.RecordReaderFactorySpy;
import se.uu.ub.cora.diva.tocorautils.doubles.RecordReaderSpy;

public class RecordCompleterSubjectCategoryTest {

	private RecordReaderFactorySpy recordReaderFactory;

	@BeforeMethod
	public void setUp() {
		recordReaderFactory = new RecordReaderFactorySpy();
	}

	@Test
	public void testNoParent() {
		ClientDataGroup dataGroup = createClientDataGroupWithRecordId("someSubjectId");
		RecordCompleter recordCompleter = RecordCompleterSubjectCategory
				.usingRecordReaderFactory(recordReaderFactory);

		ClientDataGroup completedMetadata = recordCompleter.completeMetadata(dataGroup);

		RecordReaderSpy factoredReader = recordReaderFactory.factored;
		assertEquals(factoredReader.usedTableNames.get(0), "subject_parent");
		assertEquals(factoredReader.usedConditions.get("subject_id"), "someSubjectId");
		assertEquals(completedMetadata.getNameInData(), "nationalSubjectCategory");
		assertFalse(completedMetadata.containsChildWithNameInData("nationalSubjectCategoryParent"));

	}

	@Test
	public void testOneParent() {
		String subjectId = "someSubjectWithParentId";
		recordReaderFactory.idsToReturnParent.add(subjectId);
		ClientDataGroup dataGroup = createClientDataGroupWithRecordId(subjectId);
		RecordCompleter recordCompleter = RecordCompleterSubjectCategory
				.usingRecordReaderFactory(recordReaderFactory);

		ClientDataGroup completedMetadata = recordCompleter.completeMetadata(dataGroup);

		RecordReaderSpy factoredReader = recordReaderFactory.factored;
		assertEquals(factoredReader.usedTableNames.get(0), "subject_parent");
		assertEquals(factoredReader.usedConditions.get("subject_id"), "someSubjectWithParentId");
		assertEquals(completedMetadata.getNameInData(), "nationalSubjectCategory");

		assertOkParentInDataGroupByIndex(completedMetadata, 0);
		assertEquals(completedMetadata.getAllGroupsWithNameInData("nationalSubjectCategoryParent")
				.size(), 1);

	}

	private void assertOkParentInDataGroupByIndex(ClientDataGroup completedMetadata, int index) {
		ClientDataGroup parentGroup = completedMetadata
				.getAllGroupsWithNameInData("nationalSubjectCategoryParent").get(index);
		assertEquals(parentGroup.getRepeatId(), String.valueOf(index));
		ClientDataGroup parentLink = parentGroup
				.getFirstGroupWithNameInData("nationalSubjectCategory");
		assertEquals(parentLink.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"nationalSubjectCategory");
		assertEquals(parentLink.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"someParent" + String.valueOf(index));
	}

	@Test
	public void testTwoParents() {
		String subjectId = "someSubjectWithParentId";
		recordReaderFactory.idsToReturnParent.add(subjectId);
		recordReaderFactory.noOfParentsToReturn = 2;
		ClientDataGroup dataGroup = createClientDataGroupWithRecordId(subjectId);
		RecordCompleter recordCompleter = RecordCompleterSubjectCategory
				.usingRecordReaderFactory(recordReaderFactory);

		ClientDataGroup completedMetadata = recordCompleter.completeMetadata(dataGroup);

		RecordReaderSpy factoredReader = recordReaderFactory.factored;
		assertEquals(factoredReader.usedTableNames.get(0), "subject_parent");
		assertEquals(factoredReader.usedConditions.get("subject_id"), "someSubjectWithParentId");
		assertEquals(completedMetadata.getNameInData(), "nationalSubjectCategory");

		assertOkParentInDataGroupByIndex(completedMetadata, 0);
		assertOkParentInDataGroupByIndex(completedMetadata, 1);
		assertEquals(completedMetadata.getAllGroupsWithNameInData("nationalSubjectCategoryParent")
				.size(), 2);

	}

	private ClientDataGroup createClientDataGroupWithRecordId(String id) {
		ClientDataGroup dataGroup = ClientDataGroup.withNameInData("nationalSubjectCategory");
		ClientDataGroup recordInfo = ClientDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(ClientDataAtomic.withNameInDataAndValue("id", id));
		dataGroup.addChild(recordInfo);
		return dataGroup;
	}

}
