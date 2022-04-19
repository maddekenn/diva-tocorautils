package se.uu.ub.cora.diva.tocorautils.convert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.sqldatabase.DatabaseFacade;

public class RecordCompleterSubjectCategory implements RecordCompleter {
	private static final String NATIONAL_SUBJECT_CATEGORY = "nationalSubjectCategory";
	private DatabaseFacade recordReaderFactory;

	public static RecordCompleterSubjectCategory usingDatabaseFacade(
			DatabaseFacade recordReaderFactory) {
		return new RecordCompleterSubjectCategory(recordReaderFactory);
	}

	private RecordCompleterSubjectCategory(DatabaseFacade databaseFacade) {
		this.recordReaderFactory = databaseFacade;
	}

	@Override
	public List<ClientDataGroup> completeMetadata(List<ClientDataGroup> dataGroups,
			String pathToFile) {
		List<ClientDataGroup> completedGroups = new ArrayList();
		String parentString = readJsonFromFile(pathToFile);
		JSONArray parents = new JSONArray(parentString);
		Map<String, List<String>> sortedParents = sortParentsToSubjectId(parents);

		for (ClientDataGroup dataGroup : dataGroups) {
			ClientDataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");
			String id = recordInfo.getFirstAtomicValueWithNameInData("id");
			if (sortedParents.containsKey(id)) {
				List<String> list = sortedParents.get(id);
				for (String parentId : list) {
					ClientDataGroup outerParent = ClientDataGroup
							.withNameInData("nationalSubjectCategoryParent");
					ClientDataGroup parentLink = ClientDataGroup.asLinkWithNameInDataAndTypeAndId(
							"nationalSubjectCategory", "nationalSubjectCategory", parentId);
					outerParent.addChild(parentLink);
					dataGroup.addChild(outerParent);
				}
				completedGroups.add(dataGroup);

			}
		}

		// ClientDataGroup dataGroup = dataGroups.get(0);
		// List<Map<String, Object>> readParents = readParents(dataGroup);
		//
		// int repeatId = 0;
		// for (Map<String, Object> parentRow : readParents) {
		// createAndAddParentToDataGroupUsingRepeatIdAndRowFromDb(dataGroup, repeatId, parentRow);
		// repeatId++;
		// }
		// return convertToJson(dataGroup);
		return completedGroups;
	}

	private Map<String, List<String>> sortParentsToSubjectId(JSONArray parents) {
		Map<String, List<String>> sortedParents = new HashMap<>();
		for (Object parent : parents) {
			JSONObject jsonRow = (JSONObject) parent;
			String subjectId = getSubjectIdAsString(jsonRow);
			ensureListExistsForSubjectId(sortedParents, subjectId);

			addParentIdToListForSubject(sortedParents, jsonRow, subjectId);
		}
		return sortedParents;
	}

	private void addParentIdToListForSubject(Map<String, List<String>> sortedParents,
			JSONObject jsonRow, String subjectId) {
		Integer parentId = (Integer) jsonRow.get("parent_subject_id");
		sortedParents.get(subjectId).add(String.valueOf(parentId));
	}

	private String getSubjectIdAsString(JSONObject jsonRow) {
		Integer subjectId = (Integer) jsonRow.get("subject_id");
		return String.valueOf(subjectId);
	}

	private void ensureListExistsForSubjectId(Map<String, List<String>> sortedParents,
			String subjectIdString) {
		if (!sortedParents.containsKey(subjectIdString)) {
			sortedParents.put(String.valueOf(subjectIdString), new ArrayList<>());
		}
	}

	private String readJsonFromFile(String pathToFile) {
		try {
			return Files.readString(Path.of(pathToFile));
		} catch (IOException e) {
			throw new ConverterException("Unable to parse json string using path: " + pathToFile);
		}
	}

	private List<Map<String, Object>> readParents(ClientDataGroup dataGroup) {
		// String subjectId = getSubjectIdFromDataGroup(dataGroup);
		// RecordReader factoredReader = recordReaderFactory.factor();
		// Map<String, Object> conditions = new HashMap<>();
		// conditions.put("parent_subject_id", subjectId);
		// return factoredReader.readFromTableUsingConditions("subject_parent_view", conditions);
		return null;
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

	// private String convertToJson(ClientDataGroup nationalSubjectCategory) {
	// DataToJsonConverterFactory dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
	// OrgJsonBuilderFactoryAdapter jsonBuilderFactory = new OrgJsonBuilderFactoryAdapter();
	// DataToJsonConverter converter = dataToJsonConverterFactory
	// .createForClientDataElementIncludingActionLinks(jsonBuilderFactory,
	// nationalSubjectCategory, false);
	// return converter.toJson();
	//
	// }
	//
	// public RecordReaderFactory getRecordReaderFactory() {
	// // needed for test
	// return recordReaderFactory;
	// }
}
