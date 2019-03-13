package se.uu.ub.cora.diva.tocorautils.convert;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverterFactory;
import se.uu.ub.cora.diva.tocorautils.doubles.RecordReaderFactorySpy;
import se.uu.ub.cora.diva.tocorautils.doubles.RecordReaderSpy;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;

public class RecordCompleterSubjectCategoryTest {

	private RecordReaderFactorySpy recordReaderFactory;
	private DataToJsonConverterFactory dataToJsonConverterFactory;
	private JsonBuilderFactory jsonBuilderFactory;

	@BeforeMethod
	public void setUp() {
		recordReaderFactory = new RecordReaderFactorySpy();
		jsonBuilderFactory = new JsonBuilderFactorySpy();
		dataToJsonConverterFactory = new DataToJsonConverterFactorySpy();
	}

	@Test
	public void testNoParent() {
		ClientDataGroup dataGroup = createClientDataGroupWithRecordId("someSubjectId");
		RecordCompleter recordCompleter = RecordCompleterSubjectCategory
				.usingRecordReaderFactory(recordReaderFactory);

		String completedMetadataJson = recordCompleter.completeMetadata(dataGroup);

		RecordReaderSpy factoredReader = recordReaderFactory.factored;
		assertEquals(factoredReader.usedTableNames.get(0), "subject_parent");
		assertEquals(factoredReader.usedConditions.get("subject_id"), "someSubjectId");

		assertEquals(completedMetadataJson,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"someSubjectId\"}],\"name\":\"recordInfo\"}],\"name\":\"nationalSubjectCategory\"}");

	}

	@Test
	public void testOneParent() {
		String subjectId = "someSubjectWithParentId";
		recordReaderFactory.idsToReturnParent.add(subjectId);
		ClientDataGroup dataGroup = createClientDataGroupWithRecordId(subjectId);
		RecordCompleter recordCompleter = RecordCompleterSubjectCategory
				.usingRecordReaderFactory(recordReaderFactory);

		String completedMetadataJson = recordCompleter.completeMetadata(dataGroup);

		RecordReaderSpy factoredReader = recordReaderFactory.factored;
		assertEquals(factoredReader.usedTableNames.get(0), "subject_parent");
		assertEquals(factoredReader.usedConditions.get("subject_id"), "someSubjectWithParentId");

		assertEquals(completedMetadataJson,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"someSubjectWithParentId\"}],\"name\":\"recordInfo\"},{\"repeatId\":\"0\",\"children\":[{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"nationalSubjectCategory\"},{\"name\":\"linkedRecordId\",\"value\":\"someParent0\"}],\"name\":\"nationalSubjectCategory\"}],\"name\":\"nationalSubjectCategoryParent\"}],\"name\":\"nationalSubjectCategory\"}");
	}
	//
	// private void assertOkParentInDataGroupByIndex(ClientDataGroup
	// completedMetadata, int index) {
	// ClientDataGroup parentGroup = completedMetadata
	// .getAllGroupsWithNameInData("nationalSubjectCategoryParent").get(index);
	// assertEquals(parentGroup.getRepeatId(), String.valueOf(index));
	// ClientDataGroup parentLink = parentGroup
	// .getFirstGroupWithNameInData("nationalSubjectCategory");
	// assertEquals(parentLink.getFirstAtomicValueWithNameInData("linkedRecordType"),
	// "nationalSubjectCategory");
	// assertEquals(parentLink.getFirstAtomicValueWithNameInData("linkedRecordId"),
	// "someParent" + String.valueOf(index));
	// }

	@Test
	public void testTwoParents() {
		String subjectId = "someSubjectWithParentId";
		recordReaderFactory.idsToReturnParent.add(subjectId);
		recordReaderFactory.noOfParentsToReturn = 2;
		ClientDataGroup dataGroup = createClientDataGroupWithRecordId(subjectId);
		RecordCompleter recordCompleter = RecordCompleterSubjectCategory
				.usingRecordReaderFactory(recordReaderFactory);

		String completedMetadataJson = recordCompleter.completeMetadata(dataGroup);

		RecordReaderSpy factoredReader = recordReaderFactory.factored;
		assertEquals(factoredReader.usedTableNames.get(0), "subject_parent");
		assertEquals(factoredReader.usedConditions.get("subject_id"), "someSubjectWithParentId");

		assertEquals(completedMetadataJson,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"someSubjectWithParentId\"}],\"name\":\"recordInfo\"},{\"repeatId\":\"0\",\"children\":[{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"nationalSubjectCategory\"},{\"name\":\"linkedRecordId\",\"value\":\"someParent0\"}],\"name\":\"nationalSubjectCategory\"}],\"name\":\"nationalSubjectCategoryParent\"},{\"repeatId\":\"1\",\"children\":[{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"nationalSubjectCategory\"},{\"name\":\"linkedRecordId\",\"value\":\"someParent1\"}],\"name\":\"nationalSubjectCategory\"}],\"name\":\"nationalSubjectCategoryParent\"}],\"name\":\"nationalSubjectCategory\"}");

	}

	private ClientDataGroup createClientDataGroupWithRecordId(String id) {
		ClientDataGroup dataGroup = ClientDataGroup.withNameInData("nationalSubjectCategory");
		ClientDataGroup recordInfo = ClientDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(ClientDataAtomic.withNameInDataAndValue("id", id));
		dataGroup.addChild(recordInfo);
		return dataGroup;
	}

}
