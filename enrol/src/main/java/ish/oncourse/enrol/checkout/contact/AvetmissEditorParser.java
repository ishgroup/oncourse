package ish.oncourse.enrol.checkout.contact;

import ish.common.types.*;
import ish.oncourse.enrol.checkout.IFieldsParser;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Country;
import ish.oncourse.model.Language;
import ish.oncourse.model.Student;
import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.services.reference.ILanguageService;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.services.Request;

import java.util.HashMap;
import java.util.Map;

import static ish.oncourse.util.MessagesNamingConvention.MESSAGE_KEY_TEMPLATE;

public class AvetmissEditorParser implements IFieldsParser{



	private ICountryService countryService;
	private ILanguageService languageService;
	private Messages messages;

	private Request request;

	private Contact contact;


	private Map<String, String> errors = new HashMap<String, String>();


	public void parse() {
		Field[] fields = Field.values();
		for (Field field : fields) {
			String value = StringUtils.trimToNull(request.getParameter(field.name()));
			if (value != null)
				setValue(field, value);
		}
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	private void setValue(Field field, String value) {
		switch (field) {
			case countryOfBirth:
				Country country = countryService.getCountryByName(value);
				if (country == null) {
					errors.put(field.propertyName, messages.get(String.format(MESSAGE_KEY_TEMPLATE, field.name())));
				} else {
					contact.getStudent().setCountryOfBirth(
							(Country) contact.getObjectContext()
									.localObject(country.getObjectId(), country));
				}
				break;
			case languageHome:
				Language language = languageService.getLanguageByName(value);
				if (language == null) {
					errors.put(field.propertyName, messages.get(String.format(MESSAGE_KEY_TEMPLATE, field.name())));
				} else {
					contact.getStudent().setLanguageHome(
							(Language) contact.getObjectContext().localObject(language.getObjectId(),
									language));
				}
				break;
			case englishProficiency:
				contact.getStudent().setEnglishProficiency(AvetmissStudentEnglishProficiency.valueOf(value));
				break;
			case indigenousStatus:
				contact.getStudent().setIndigenousStatus(AvetmissStudentIndigenousStatus.valueOf(value));
				break;
			case highestSchoolLevel:
				contact.getStudent().setHighestSchoolLevel(AvetmissStudentSchoolLevel.valueOf(value));
				break;
			case yearSchoolCompleted:
				String error;
				if (StringUtils.isNumeric(value)) {
					contact.getStudent().setYearSchoolCompleted(Integer.parseInt(value));
					error = contact.getStudent().validateSchoolYear();
				} else
					error = messages.get(String.format(MESSAGE_KEY_TEMPLATE, field.name()));
				if (error != null)
					errors.put(field.propertyName, error);
				break;
			case priorEducationCode:
				contact.getStudent().setPriorEducationCode(AvetmissStudentPriorEducation.valueOf(value));
				break;
			case disabilityType:
				contact.getStudent().setDisabilityType(AvetmissStudentDisabilityType.valueOf(value));
				break;
            default:
                throw new IllegalArgumentException();
		}
	}

	public void setLanguageService(ILanguageService languageService) {
		this.languageService = languageService;
	}

	public void setCountryService(ICountryService countryService) {
		this.countryService = countryService;
	}

	public void setMessages(Messages messages) {
		this.messages = messages;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}


	public Map<String,String>  getErrors()
	{
		return errors;
	}


	public static enum Field {
		countryOfBirth(Student.COUNTRY_OF_BIRTH_PROPERTY),
		languageHome(Student.LANGUAGE_HOME_PROPERTY),
		englishProficiency(Student.ENGLISH_PROFICIENCY_PROPERTY),
		indigenousStatus(Student.INDIGENOUS_STATUS_PROPERTY),
		highestSchoolLevel(Student.HIGHEST_SCHOOL_LEVEL_PROPERTY),
		yearSchoolCompleted(Student.YEAR_SCHOOL_COMPLETED_PROPERTY),
		priorEducationCode(Student.PRIOR_EDUCATION_CODE_PROPERTY),
		disabilityType(Student.DISABILITY_TYPE_PROPERTY);
		private String propertyName;

		Field(String propertyName) {
			this.propertyName = propertyName;
		}
	}
}
