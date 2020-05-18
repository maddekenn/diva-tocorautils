package se.uu.ub.cora.diva.tocorautils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientData;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataConverterFactory;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataRecordConverterImp;
import se.uu.ub.cora.diva.tocorautils.doubles.CoraClientSpy;

public class UpdaterTest {

	private CoraClientSpy coraClient;
	private JsonToClientDataSpy jsonToClientData;
	private DataGroupChangerFactorySpy changerFactory;
	private JsonToDataConverterFactory jsonToDataFactory;

	@BeforeMethod
	public void setUp() {
		coraClient = new CoraClientSpy();
		jsonToClientData = new JsonToClientDataSpy();
		changerFactory = new DataGroupChangerFactorySpy();
		jsonToDataFactory = new JsonToDataConverterFactorySpy();

	}

	@Test
	public void testInit() {
		Updater updater = UpdaterImp
				.usingCoraClientJsonToClientDataChangerFactoryAndConverterFactory(coraClient,
						jsonToClientData, changerFactory, jsonToDataFactory);

		String type = "nationalSubjectCategory";
		updater.update(type);

		assertEquals(coraClient.readRecordTypes.get(0), type);
		assertEquals(coraClient.jsonToReturn, jsonToClientData.jsonListToConvert);
		assertEquals(changerFactory.type, type);

		JsonToDataRecordConverterImp converterImp = (JsonToDataRecordConverterImp) jsonToClientData.jsonToDataRecordConverter;
		assertSame(converterImp.getConverterFactory(), jsonToDataFactory);

		List<ClientData> dataList = jsonToClientData.dataList.getDataList();
		assertRecordIsReadConvertedAndUpdatedCorrectly(type, dataList, 0);
		assertRecordIsReadConvertedAndUpdatedCorrectly(type, dataList, 1);
	}

	private void assertRecordIsReadConvertedAndUpdatedCorrectly(String type,
			List<ClientData> dataList, int index) {

		DataGroupChangerSpy changer = changerFactory.changer;
		ClientDataRecord clientDataRecord = (ClientDataRecord) dataList.get(index);
		ClientDataGroup clientDataGroup = clientDataRecord.getClientDataGroup();

		assertSame(changer.originalDataGroups.get(index), clientDataGroup);

		ClientDataGroup recordInfo = clientDataGroup.getFirstGroupWithNameInData("recordInfo");
		String idInOriginalDataGroup = recordInfo.getFirstAtomicValueWithNameInData("id");

		assertSame(coraClient.updatedDataGroups.get(index), changer.modifiedDataGroups.get(index));
		assertEquals(coraClient.updatedRecordIds.get(index), idInOriginalDataGroup);
		assertSame(coraClient.updatedRecordTypes.get(index), type);
	}

}
