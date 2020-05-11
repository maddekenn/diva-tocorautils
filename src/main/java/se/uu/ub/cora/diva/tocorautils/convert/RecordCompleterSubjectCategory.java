package se.uu.ub.cora.diva.tocorautils.convert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverter;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverterFactory;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverterFactoryImp;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;
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
	public String completeMetadata(ClientDataGroup dataGroup) {
		List<Map<String, Object>> readParents = readParents(dataGroup);
		int repeatId = 0;
		for (Map<String, Object> parentRow : readParents) {
			createAndAddParentToDataGroupUsingRepeatIdAndRowFromDb(dataGroup, repeatId, parentRow);
			repeatId++;
		}
		return convertToJson(dataGroup);
	}

	private List<Map<String, Object>> readParents(ClientDataGroup dataGroup) {
		String subjectId = getSubjectIdFromDataGroup(dataGroup);
		RecordReader factoredReader = recordReaderFactory.factor();
		Map<String, Object> conditions = new HashMap<>();
		conditions.put("parent_subject_id", subjectId);
		return factoredReader.readFromTableUsingConditions("subject_parent_view", conditions);
	}

	private String getSubjectIdFromDataGroup(ClientDataGroup dataGroup) {
		ClientDataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");
		return recordInfo.getFirstAtomicValueWithNameInData("id");
	}

	private void createAndAddParentToDataGroupUsingRepeatIdAndRowFromDb(ClientDataGroup dataGroup,
			int repeatId, Map<String, Object> parentRow) {
		ClientDataGroup parentGroup = ClientDataGroup
				.withNameInData("nationalSubjectCategoryParent");
		createAndAddParentLink(parentRow, parentGroup);
		parentGroup.setRepeatId(String.valueOf(repeatId));
		dataGroup.addChild(parentGroup);
	}

	private void createAndAddParentLink(Map<String, Object> parentRowFromDb,
			ClientDataGroup parentGroup) {
		ClientDataGroup parentLink = ClientDataGroup.withNameInData(NATIONAL_SUBJECT_CATEGORY);
		parentLink.addChild(ClientDataAtomic.withNameInDataAndValue("linkedRecordType",
				NATIONAL_SUBJECT_CATEGORY));
		parentLink.addChild(ClientDataAtomic.withNameInDataAndValue("linkedRecordId",
				(String) parentRowFromDb.get("subject_id")));
		parentGroup.addChild(parentLink);
	}

	private String convertToJson(ClientDataGroup nationalSubjectCategory) {
		DataToJsonConverterFactory dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		OrgJsonBuilderFactoryAdapter jsonBuilderFactory = new OrgJsonBuilderFactoryAdapter();
		DataToJsonConverter converter = dataToJsonConverterFactory
				.createForClientDataElementIncludingActionLinks(jsonBuilderFactory,
						nationalSubjectCategory, false);
		return converter.toJson();

	}

	public RecordReaderFactory getRecordReaderFactory() {
		// needed for test
		return recordReaderFactory;
	}
}
