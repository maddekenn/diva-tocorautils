package se.uu.ub.cora.diva.tocorautils.doubles;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.sqldatabase.RecordReader;
import se.uu.ub.cora.sqldatabase.RecordReaderFactory;

public class RecordReaderFactorySpy implements RecordReaderFactory {

	public boolean factorWasCalled = false;
	public RecordReaderSpy factored;
	public int noOfRecordsToReturn = 1;
	public int noOfParentsToReturn = 1;
	public List<String> idsToReturnParent = new ArrayList<>();

	@Override
	public RecordReader factor() {
		factorWasCalled = true;
		factored = new RecordReaderSpy();
		factored.idsToReturnParent.addAll(idsToReturnParent);
		factored.noOfRecordsToReturn = noOfRecordsToReturn;
		factored.noOfParentsToReturn = noOfParentsToReturn;
		return factored;
	}

}
