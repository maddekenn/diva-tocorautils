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

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.sqldatabase.DatabaseFacade;

public class RecordCompleterSubjectCategory implements RecordCompleter {
	private static final String NATIONAL_SUBJECT_CATEGORY_PARENT = "nationalSubjectCategoryParent";
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
			possiblyAddParents(completedGroups, sortedParents, dataGroup);
		}
		return completedGroups;
	}

	private String readJsonFromFile(String pathToFile) {
		try {
			return Files.readString(Path.of(pathToFile));
		} catch (IOException e) {
			throw new ConverterException("Unable to parse json string using path: " + pathToFile);
		}
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

	private void addParentIdToListForSubject(Map<String, List<String>> sortedParents,
			JSONObject jsonRow, String subjectId) {
		Integer parentId = (Integer) jsonRow.get("parent_subject_id");
		sortedParents.get(subjectId).add(String.valueOf(parentId));
	}

	private void possiblyAddParents(List<ClientDataGroup> completedGroups,
			Map<String, List<String>> sortedParents, ClientDataGroup dataGroup) {
		String id = extractId(dataGroup);
		if (sortedParents.containsKey(id)) {
			addParents(completedGroups, sortedParents, dataGroup, id);
		}
	}

	private String extractId(ClientDataGroup dataGroup) {
		ClientDataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");
		return recordInfo.getFirstAtomicValueWithNameInData("id");
	}

	private void addParents(List<ClientDataGroup> completedGroups,
			Map<String, List<String>> sortedParents, ClientDataGroup dataGroup, String id) {
		List<String> list = sortedParents.get(id);
		for (String parentId : list) {
			createParent(dataGroup, parentId);
		}
		setRepeatIds(dataGroup);
		completedGroups.add(dataGroup);
	}

	private void setRepeatIds(ClientDataGroup dataGroup) {
		List<ClientDataGroup> parents = dataGroup
				.getAllGroupsWithNameInData(NATIONAL_SUBJECT_CATEGORY_PARENT);
		int repatId = 0;
		for (ClientDataGroup parent : parents) {
			parent.setRepeatId(String.valueOf(repatId));
			repatId++;
		}
	}

	private void createParent(ClientDataGroup dataGroup, String parentId) {
		ClientDataGroup outerParent = ClientDataGroup
				.withNameInData(NATIONAL_SUBJECT_CATEGORY_PARENT);
		ClientDataGroup parentLink = ClientDataGroup.asLinkWithNameInDataAndTypeAndId(
				NATIONAL_SUBJECT_CATEGORY, NATIONAL_SUBJECT_CATEGORY, parentId);
		outerParent.addChild(parentLink);
		dataGroup.addChild(outerParent);
	}

}
