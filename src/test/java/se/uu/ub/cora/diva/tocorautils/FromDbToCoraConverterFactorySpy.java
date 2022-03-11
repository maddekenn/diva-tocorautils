package se.uu.ub.cora.diva.tocorautils;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.diva.tocorautils.convert.FromDbToCoraConverter;
import se.uu.ub.cora.diva.tocorautils.convert.FromDbToCoraConverterFactory;
import se.uu.ub.cora.diva.tocorautils.doubles.FromDbToCoraConverterSpy;

public class FromDbToCoraConverterFactorySpy implements FromDbToCoraConverterFactory {

	public String type;
	public List<FromDbToCoraConverterSpy> returnedConverterSpies = new ArrayList<>();

	@Override
	public FromDbToCoraConverter factor(String type) {
		this.type = type;
		FromDbToCoraConverterSpy returnedConverterSpy = new FromDbToCoraConverterSpy();

		returnedConverterSpies.add(returnedConverterSpy);
		return returnedConverterSpy;
	}

}
