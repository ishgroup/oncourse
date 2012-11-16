package ish.oncourse.enrol.components.checkout.contact;

import ish.common.types.*;
import ish.oncourse.enrol.checkout.ValidateHandler;
import ish.oncourse.enrol.checkout.contact.AvetmissEditorParser;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Country;
import ish.oncourse.model.Language;
import ish.oncourse.selectutils.ISHEnumSelectModel;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.services.reference.ILanguageService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.ArrayList;
import java.util.List;
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
	private ICayenneService cayenneService;

	@Inject
	private ILanguageService languageService;

	@Inject
	private Request request;

	@Property
	private ValidateHandler validateHandler = new ValidateHandler();

	@SetupRender
	void beforeRender() {
	}


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
		validateHandler.setErrors(avetmissEditorParser.getErrors());
	}

	public Map<String,String> getErrors()
	{
		return validateHandler.getErrors();
	}

	public String getCountries()
	{

//		["Alabama","Alaska","Arizona","Arkansas","California","Colorado","Connecticut","Delaware","Florida","Georgia","Hawaii","Idaho","Illinois","Indiana","Iowa","Kansas","Kentucky","Louisiana","Maine","Maryland","Massachusetts","Michigan","Minnesota","Mississippi","Missouri","Montana","Nebraska","Nevada","New Hampshire","New Jersey","New Mexico","New York","North Dakota","North Carolina","Ohio","Oklahoma","Oregon","Pennsylvania","Rhode Island","South Carolina","South Dakota","Tennessee","Texas","Utah","Vermont","Virginia","Washington","West Virginia","Wisconsin","Wyoming"]

		ObjectContext context = cayenneService.sharedContext();

		SelectQuery query = new SelectQuery(Country.class);
		List<Country> countries = context.performQuery(query);


		ArrayList<String> result = new ArrayList<String>(countries.size());
		for (Country country : countries) {
			result.add(String.format("\"%s\"", country.getName()));
		}
		return String.format("[%s]",StringUtils.join(result.toArray(),","));
	}
}
