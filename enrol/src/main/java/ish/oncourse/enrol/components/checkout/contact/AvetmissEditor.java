package ish.oncourse.enrol.components.checkout.contact;

import ish.common.types.*;
import ish.oncourse.components.AvetmissStrings;
import ish.oncourse.enrol.checkout.contact.AvetmissEditorParser;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Country;
import ish.oncourse.model.Language;
import ish.oncourse.selectutils.BooleanSelection;
import ish.oncourse.selectutils.ISHEnumSelectModel;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.services.reference.ILanguageService;
import ish.oncourse.util.MessagesNamingConvention;
import ish.oncourse.util.ValidateHandler;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.internal.util.MessagesImpl;
import org.apache.tapestry5.ioc.services.PropertyAccess;
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
    private ICayenneService cayenneService;

    @Inject
    private ILanguageService languageService;

    @Inject
    private Request request;

    @Property
    private ValidateHandler validateHandler = new ValidateHandler();
    
    private Messages avetmissMessages;
    
    @Inject
	private PropertyAccess access;

    @SetupRender
    void beforeRender() {
    }

	public Messages getAvetmissMessages()
	{
		if (avetmissMessages == null)
		{
			avetmissMessages =  MessagesImpl.forClass(AvetmissStrings.class);
		}
		return avetmissMessages;
	}


    public String getCountryOfBirth() {
        Country countryOfBirth = contact.getStudent().getCountryOfBirth();
        if (countryOfBirth == null) {
            return ICountryService.DEFAULT_COUNTRY_NAME;
        }
        return countryOfBirth.getName();
    }

    public void setCountryOfBirth(String value) {
    }


    public String getLanguageHome() {
        Language languageHome = contact.getStudent().getLanguageHome();
        if (languageHome == null) {
            return null;
        }
        return languageHome.getName();
    }

    public void setLanguageHome(String value) {
    }


    public String getYearSchoolCompleted() {
        Integer yearSchoolCompleted = contact.getStudent().getYearSchoolCompleted();
        if (yearSchoolCompleted == null) {
            return null;
        }
        return Integer.toString(yearSchoolCompleted);
    }

    public void setYearSchoolCompleted(String value) {
    }


    public ISHEnumSelectModel getEnglishProficiencySelectModel() {
        return new ISHEnumSelectModel(
                AvetmissStudentEnglishProficiency.class, getAvetmissMessages());
    }


    public ISHEnumSelectModel getIndigenousStatusSelectModel() {
        return new ISHEnumSelectModel(AvetmissStudentIndigenousStatus.class,
				getAvetmissMessages());
    }

    public ISHEnumSelectModel getSchoolLevelSelectModel() {
        return new ISHEnumSelectModel(AvetmissStudentSchoolLevel.class, getAvetmissMessages());
    }

    public ISHEnumSelectModel getPriorEducationSelectModel() {
        return new ISHEnumSelectModel(AvetmissStudentPriorEducation.class,
				getAvetmissMessages());
    }

    public ISHEnumSelectModel getDisabilityTypeSelectModel() {
        return new ISHEnumSelectModel(AvetmissStudentDisabilityType.class, getAvetmissMessages());
    }
    
    public ISHEnumSelectModel getLabourForceStatusSelectModel() {
    	return new ISHEnumSelectModel(AvetmissStudentLabourStatus.class, getAvetmissMessages());
    }
    
    public ISHEnumSelectModel getStillAtSchoolSelectModel() {
		return new ISHEnumSelectModel(BooleanSelection.class, getAvetmissMessages());
	}
	
	public BooleanSelection getStillAtSchoolSelection() {
		return BooleanSelection.valueOf(contact.getStudent().getIsStillAtSchool());
	}
	
	public void setStillAtSchoolSelection(BooleanSelection selection) {
		contact.getStudent().setIsStillAtSchool(selection.getValue());
	} 

    public void save() {
        AvetmissEditorParser avetmissEditorParser = new AvetmissEditorParser();
        avetmissEditorParser.setContact(contact);
        avetmissEditorParser.setCountryService(countryService);
        avetmissEditorParser.setLanguageService(languageService);
        avetmissEditorParser.setMessages(getAvetmissMessages());
        avetmissEditorParser.setRequest(request);
        avetmissEditorParser.parse();
        validateHandler.setErrors(avetmissEditorParser.getErrors());
    }

    public Map<String, String> getErrors() {
        return validateHandler.getErrors();
    }

    public String label(String fieldName) {
		return getAvetmissMessages().get(String.format(MessagesNamingConvention.LABEL_KEY_TEMPLATE, fieldName));
	}

	public String message(String messageKey) {
		return getAvetmissMessages().get(messageKey);
	}
}
