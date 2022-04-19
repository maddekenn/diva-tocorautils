package se.uu.ub.cora.diva.tocorautils.importing;

import java.util.List;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.diva.tocorautils.convert.FromDbToCoraConverterFactory;

public class SubjectCategoryTransformerFromFile implements DbToCoraTransformer {

	private String pathToFile;
	private FromDbToCoraConverterFactory converterFactory;

	public static SubjectCategoryTransformerFromFile usingFilenameAndConverterFactory(
			String pathToFile, FromDbToCoraConverterFactory converterFactory) {
		return new SubjectCategoryTransformerFromFile(pathToFile, converterFactory);
	}

	private SubjectCategoryTransformerFromFile(String pathToFile,
			FromDbToCoraConverterFactory converterFactory) {
		this.pathToFile = pathToFile;
		this.converterFactory = converterFactory;
	}

	@Override
	public List<ClientDataGroup> getConverted() {
		// TODO Auto-generated method stub
		return null;
	}

	public FromDbToCoraConverterFactory getConverterFactory() {
		return converterFactory;
	}

}
