package ish.oncourse.ui.pages;

import ish.oncourse.services.IReachtextConverter;
import ish.oncourse.util.ValidationErrors;
import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.io.IOException;
import java.io.InputStream;

public class FormTextilePageWithTwoForms {

	@Inject
	private IReachtextConverter textileConverter;

	public String getTextile() {
		InputStream st = FormTextilePage.class.getClassLoader().getResourceAsStream("ish/oncourse/ui/pages/dataset/formTextilePageSydneyWithTwoFormsDataSet.txt");
		String data = "";
		try {
			data = IOUtils.toString(st, "UTF-8");
		} catch (IOException e) {}
		
		return textileConverter.convertCustomText(data, new ValidationErrors());
	}
}
