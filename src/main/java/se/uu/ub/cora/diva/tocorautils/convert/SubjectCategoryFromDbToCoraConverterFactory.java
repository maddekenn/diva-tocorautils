package se.uu.ub.cora.diva.tocorautils.convert;

public class SubjectCategoryFromDbToCoraConverterFactory implements FromDbToCoraConverterFactory {

	@Override
	public FromDbToCoraConverter createConverter() {
		FromDbToCoraSubjectCategoryConverter converterFactory = FromDbToCoraSubjectCategoryConverter
				.usingJsonFactoryConverterFactoryAndReaderFactory(null, null, null);

		return converterFactory;
	}

}
