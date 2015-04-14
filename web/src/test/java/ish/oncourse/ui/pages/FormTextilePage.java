package ish.oncourse.ui.pages;

import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.util.ValidationErrors;
import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.io.IOException;
import java.io.InputStream;

public class FormTextilePage {

	public static final String FIELD_LABEL = "field";
	public static final String TO_EMAIL = "to_email@gmail.com";
	public static final String FORM_NAME = "testform";
	@Inject
	private ITextileConverter textileConverter;

	public String getTextile() {
		return textileConverter.convertCustomTextile("{form name:&#8220;" + FORM_NAME + "&#8221; email:&#8220;"
				+ TO_EMAIL + "&#8221;}<br/>{text label:&#8220;"+FIELD_LABEL+"&#8221; required:&#8220;yes&#8221;}{form}", new ValidationErrors());
	}
	
	public String getTextileForJrda() {
		return textileConverter.convertCustomTextile(getTextile("ish/oncourse/ui/pages/dataset/formTextilePageJrdaDataSet.txt"), new ValidationErrors());
	}
	
	public String getTextileForActt() {
		return textileConverter.convertCustomTextile(getTextile("ish/oncourse/ui/pages/dataset/formTextilePageActtDataSet.txt"), new ValidationErrors());
	}
	
	public String getTextileForAvb() {
		return textileConverter.convertCustomTextile(getTextile("ish/oncourse/ui/pages/dataset/formTextilePageAvbDataSet.txt"), new ValidationErrors());
	}
	
	public String getTextileForDjwarehouse() {
		return textileConverter.convertCustomTextile(getTextile("ish/oncourse/ui/pages/dataset/formTextilePageDjwarehouseDataSet.txt"), new ValidationErrors());
	}
	
	public String getTextileForDjwarehousetrainingSend() {
		return textileConverter.convertCustomTextile(getTextile("ish/oncourse/ui/pages/dataset/formTextilePageDjwarehousetrainingSendDataSet.txt"), new ValidationErrors());
	}
	
	public String getTextileForEducation() {
		return textileConverter.convertCustomTextile(getTextile("ish/oncourse/ui/pages/dataset/formTextilePageEducationDataSet.txt"), new ValidationErrors());
	}
	
	public String getTextileForImprovisation() {
		return textileConverter.convertCustomTextile(getTextile("ish/oncourse/ui/pages/dataset/formTextilePageImprovisationDataSet.txt"), new ValidationErrors());
	}
	
	public String getTextileForScc() {
		return textileConverter.convertCustomTextile(getTextile("ish/oncourse/ui/pages/dataset/formTextilePageSccDataSet.txt"), new ValidationErrors());
	}
	
	
	private String getTextile(String path){
		InputStream st = FormTextilePage.class.getClassLoader().getResourceAsStream(path);
		String data = "";
		try {
			data = IOUtils.toString(st, "UTF-8");
		} catch (IOException e) {}
		return data;
	}
	
}
