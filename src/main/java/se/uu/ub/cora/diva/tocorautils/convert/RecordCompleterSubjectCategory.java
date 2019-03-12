package se.uu.ub.cora.diva.tocorautils.convert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.sqldatabase.RecordReader;
import se.uu.ub.cora.sqldatabase.RecordReaderFactory;

public class RecordCompleterSubjectCategory implements RecordCompleter {
	private static final String NATIONAL_SUBJECT_CATEGORY = "nationalSubjectCategory";
	private RecordReaderFactory recordReaderFactory;

	public static RecordCompleterSubjectCategory usingRecordReaderFactory(
			RecordReaderFactory recordReaderFactory) {
		return new RecordCompleterSubjectCategory(recordReaderFactory);
	}

	private RecordCompleterSubjectCategory(RecordReaderFactory recordReaderFactory) {
		this.recordReaderFactory = recordReaderFactory;
	}

	@Override
	public ClientDataGroup completeMetadata(ClientDataGroup dataGroup) {
		List<Map<String, String>> readParents = readParents(dataGroup);
		int repeatId = 0;
		for (Map<String, String> parentRow : readParents) {
			createAndAddParentToDataGroupUsingRepeatIdAndRowFromDb(dataGroup, repeatId, parentRow);
			repeatId++;
		}
		return dataGroup;
	}

	private List<Map<String, String>> readParents(ClientDataGroup dataGroup) {
		String subjectId = getSubjectIdFromDataGroup(dataGroup);
		RecordReader factoredReader = recordReaderFactory.factor();
		Map<String, String> conditions = new HashMap<>();
		conditions.put("subject_id", subjectId);
		return factoredReader.readFromTableUsingConditions("subject_parent", conditions);
	}

	private String getSubjectIdFromDataGroup(ClientDataGroup dataGroup) {
		ClientDataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");
		return recordInfo.getFirstAtomicValueWithNameInData("id");
	}

	private void createAndAddParentToDataGroupUsingRepeatIdAndRowFromDb(ClientDataGroup dataGroup,
			int repeatId, Map<String, String> parentRow) {
		ClientDataGroup parentGroup = ClientDataGroup
				.withNameInData("nationalSubjectCategoryParent");
		createAndAddParentLink(parentRow, parentGroup);
		parentGroup.setRepeatId(String.valueOf(repeatId));
		dataGroup.addChild(parentGroup);
	}

	private void createAndAddParentLink(Map<String, String> parentRowFromDb,
			ClientDataGroup parentGroup) {
		ClientDataGroup parentLink = ClientDataGroup.withNameInData(NATIONAL_SUBJECT_CATEGORY);
		parentLink.addChild(ClientDataAtomic.withNameInDataAndValue("linkedRecordType",
				NATIONAL_SUBJECT_CATEGORY));
		parentLink.addChild(ClientDataAtomic.withNameInDataAndValue("linkedRecordId",
				parentRowFromDb.get("parent_subject_id")));
		parentGroup.addChild(parentLink);
	}

}
