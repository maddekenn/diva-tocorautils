package se.uu.ub.cora.diva.tocorautils.convert;

import java.util.Map;

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.diva.tocorautils.CoraJsonRecord;
import se.uu.ub.cora.diva.tocorautils.NotImplementedException;
import se.uu.ub.cora.sqldatabase.Row;

public class FromDbToCoraFunderConverter implements FromDbToCoraConverter {

	private ClientDataGroup funder;
	private Row row;

	@Override
	public CoraJsonRecord convertToJsonFromRowFromDb(Map<String, Object> rowFromDb) {
		throw NotImplementedException.withMessage("Convert to Json not implemented for funder");
	}

	@Override
	public ClientDataGroup convertToClientDataGroupFromRowFromDb(Row row) {
		this.row = row;
		funder = ClientDataGroup.withNameInData("funder");
		funder.addChild(createRecordInfo());

		createAndAddName("funder_name", "name");
		createAndAddName("alternative_name", "alternativeName");
		possiblyAddNonMandatoryValues();
		return funder;
	}

	private ClientDataGroup createRecordInfo() {
		int funderId = (int) row.getValueByColumn("funder_id");
		ClientDataGroup recordInfo = ClientDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(ClientDataAtomic.withNameInDataAndValue("id", "funder:" + funderId));
		addDataDivider(recordInfo);
		return recordInfo;
	}

	private void addDataDivider(ClientDataGroup recordInfo) {
		ClientDataGroup dataDivider = ClientDataGroup
				.asLinkWithNameInDataAndTypeAndId("dataDivider", "system", "diva");
		recordInfo.addChild(dataDivider);
	}

	private void createAndAddName(String key, String nameInData) {
		if (row.hasColumnWithNonEmptyValue(key)) {
			String funderName = (String) row.getValueByColumn(key);
			ClientDataGroup name = ClientDataGroup.withNameInData(nameInData);
			name.addChild(ClientDataAtomic.withNameInDataAndValue("funderName", funderName));

			String funderNameLocale = (String) row.getValueByColumn(key + "_locale");
			name.addChild(ClientDataAtomic.withNameInDataAndValue("language", funderNameLocale));
			funder.addChild(name);
		}
	}

	private void possiblyAddNonMandatoryValues() {
		possiblyAddValueForKey("acronym", "funderAcronym");
		possiblyAddValueForKey("orgnumber", "funderRegistrationNumber");
		possiblyAddValueForKey("doi", "funderDOI");
		possiblyAddValueForKey("closed_date", "funderClosed");
	}

	private void possiblyAddValueForKey(String key, String nameInData) {
		if (row.hasColumnWithNonEmptyValue(key)) {
			String value = (String) row.getValueByColumn(key);
			funder.addChild(ClientDataAtomic.withNameInDataAndValue(nameInData, value));
		}
	}

}
