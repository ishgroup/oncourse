package ish.oncourse.enrol.components.checkout.contact;

import ish.common.types.*;
import ish.oncourse.enrol.checkout.contact.AvetmissEditorParser;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Country;
import ish.oncourse.model.Language;
import ish.oncourse.selectutils.ISHEnumSelectModel;
import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.services.reference.ILanguageService;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.Map;

public class AvetmissEditor {

	@Parameter(required = true)
	@Property
	private Contact contact;

	@Inject
	private Messages messages;

	@Inject
	private ICountryService countryService;

	@Inject
	private ILanguageService languageService;

	@Inject
	private Request request;

	private Map<String,String> errors;

	public String getCountryOfBirth() {
		Country countryOfBirth = contact.getStudent().getCountryOfBirth();
		if (countryOfBirth == null) {
			return null;
		}
		return countryOfBirth.getName();
	}

	public String getLanguageHome() {
		Language languageHome = contact.getStudent().getLanguageHome();
		if (languageHome == null) {
			return null;
		}
		return languageHome.getName();
	}

	public String getYearSchoolCompleted() {
		Integer yearSchoolCompleted = contact.getStudent().getYearSchoolCompleted();
		if (yearSchoolCompleted == null) {
			return null;
		}
		return Integer.toString(yearSchoolCompleted);
	}


	public ISHEnumSelectModel getEnglishProficiencySelectModel()
	{
		return new ISHEnumSelectModel(
				AvetmissStudentEnglishProficiency.class, messages);
	}


	public ISHEnumSelectModel getIndigenousStatusSelectModel()
	{
		return new ISHEnumSelectModel(AvetmissStudentIndigenousStatus.class,
				messages);
	}

	public ISHEnumSelectModel getSchoolLevelSelectModel()
	{
		return new ISHEnumSelectModel(AvetmissStudentSchoolLevel.class, messages);
	}

	public ISHEnumSelectModel getPriorEducationSelectModel()
	{
		return new ISHEnumSelectModel(AvetmissStudentPriorEducation.class,
				messages);
	}

	public ISHEnumSelectModel getDisabilityTypeSelectModel()
	{
		return new ISHEnumSelectModel(AvetmissStudentDisabilityType.class, messages);
	}

	public void save()
	{
		AvetmissEditorParser avetmissEditorParser = new AvetmissEditorParser();
		avetmissEditorParser.setContact(contact);
		avetmissEditorParser.setCountryService(countryService);
		 avetmissEditorParser.setLanguageService(languageService);
		avetmissEditorParser.setMessages(messages);
		avetmissEditorParser.setRequest(request);
		avetmissEditorParser.parse();
		errors = avetmissEditorParser.getErrors();
	}

	public Map<String, String> getErrors() {
		return errors;
	}
}
