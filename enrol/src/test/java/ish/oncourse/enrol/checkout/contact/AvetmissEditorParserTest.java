package ish.oncourse.enrol.checkout.contact;

import ish.common.types.*;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Country;
import ish.oncourse.model.Language;
import ish.oncourse.model.Student;
import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.services.reference.ILanguageService;
import org.apache.cayenne.DataObject;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.services.Request;
import org.junit.Before;
import org.junit.Test;

import static ish.oncourse.enrol.checkout.contact.AvetmissEditorParser.Field.*;
import static ish.oncourse.util.MessagesNamingConvention.MESSAGE_KEY_TEMPLATE;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AvetmissEditorParserTest {

    private Contact contact;
    private Student student;
    private Country country;
    private Language language;
    private ICountryService countryService;
    private ILanguageService languageService;
    private Messages messages;
    private Request request;
    private ObjectContext objectContext;

    @Before
    public void setup() {
        objectContext = mock(ObjectContext.class);

        country = mock(Country.class);
        ObjectId objectId = mock(ObjectId.class);
        doReturn(objectId).when(country).getObjectId();
        doReturn(country).when(objectContext).localObject(country);

        contact = mock(Contact.class);
        student = spy(new Student());
        doNothing().when(student).setToOneTarget(anyString(), any(DataObject.class), anyBoolean());
        doReturn(student).when(contact).getStudent();
        doReturn(objectContext).when(contact).getObjectContext();

        language = mock(Language.class);
        objectId = mock(ObjectId.class);
        doReturn(objectId).when(language).getObjectId();
        doReturn(language).when(objectContext).localObject(language);


        countryService = mock(ICountryService.class);
        languageService = mock(ILanguageService.class);
        messages = mock(Messages.class);
        request = mock(Request.class);

    }

    @Test
    public void testCountry() {

        String validCountry = "validCountry";
        String inValidCountry = "inValidCountry";

        when(countryService.getCountryByName(validCountry)).thenReturn(country);
        when(countryService.getCountryByName(inValidCountry)).thenReturn(null);

        String key = String.format(MESSAGE_KEY_TEMPLATE, AvetmissEditorParser.Field.countryOfBirth);
        when(messages.get(key)).thenReturn(key);


        //test invalid country
        AvetmissEditorParser parser = getAvetmissEditorParser();
        when(request.getParameter(AvetmissEditorParser.Field.countryOfBirth.name())).thenReturn(inValidCountry);
        parser.parse();
        assertFalse(parser.getErrors().isEmpty());
        assertEquals(1, parser.getErrors().size());
        assertEquals(key, parser.getErrors().get(AvetmissEditorParser.Field.countryOfBirth.name()));


        //test valid country
        parser = getAvetmissEditorParser();
        when(request.getParameter(AvetmissEditorParser.Field.countryOfBirth.name())).thenReturn(validCountry);
        parser.parse();
        assertTrue(parser.getErrors().isEmpty());
        verify(contact.getStudent(), times(1)).setCountryOfBirth(country);

    }

    @Test
    public void testLanguageHome()
    {
        String validLanguge = "valid";
        String inValidLanguge = "inValid";

        when(languageService.getLanguageByName(validLanguge)).thenReturn(language);
        when(languageService.getLanguageByName(inValidLanguge)).thenReturn(null);
        String key = String.format(MESSAGE_KEY_TEMPLATE, AvetmissEditorParser.Field.languageHome);
        when(messages.get(key)).thenReturn(key);

        //test invalid languageHome
        AvetmissEditorParser parser = getAvetmissEditorParser();
        when(request.getParameter(AvetmissEditorParser.Field.languageHome.name())).thenReturn(inValidLanguge);
        parser.parse();
        assertFalse(parser.getErrors().isEmpty());
        assertEquals(1, parser.getErrors().size());
        assertEquals(key, parser.getErrors().get(AvetmissEditorParser.Field.languageHome.name()));


        //test valid languageHome
        parser = getAvetmissEditorParser();
        when(request.getParameter(AvetmissEditorParser.Field.languageHome.name())).thenReturn(validLanguge);
        parser.parse();
        assertTrue(parser.getErrors().isEmpty());
        verify(contact.getStudent(), times(1)).setLanguageHome(language);
    }

    @Test
    public void testStringFields()
    {
        when(request.getParameter(englishProficiency.name())).thenReturn(AvetmissStudentEnglishProficiency.WELL.name());
        when(request.getParameter(indigenousStatus.name())).thenReturn(AvetmissStudentIndigenousStatus.ABORIGINAL.name());
        when(request.getParameter(highestSchoolLevel.name())).thenReturn(AvetmissStudentSchoolLevel.DID_NOT_GO_TO_SCHOOL.name());
        when(request.getParameter(priorEducationCode.name())).thenReturn(AvetmissStudentPriorEducation.BACHELOR.name());
        when(request.getParameter(disabilityType.name())).thenReturn(AvetmissStudentDisabilityType.HEARING.name());
        AvetmissEditorParser parser = getAvetmissEditorParser();
        parser.parse();
        verify(contact.getStudent(), times(1)).setEnglishProficiency(AvetmissStudentEnglishProficiency.WELL);
        verify(contact.getStudent(), times(1)).setIndigenousStatus(AvetmissStudentIndigenousStatus.ABORIGINAL);
        verify(contact.getStudent(), times(1)).setHighestSchoolLevel(AvetmissStudentSchoolLevel.DID_NOT_GO_TO_SCHOOL);
        verify(contact.getStudent(), times(1)).setPriorEducationCode(AvetmissStudentPriorEducation.BACHELOR);
        verify(contact.getStudent(), times(1)).setDisabilityType(AvetmissStudentDisabilityType.HEARING);
    }

    @Test
    public void testWrongYearSchoolCompleted()
    {
        when(request.getParameter(yearSchoolCompleted.name())).thenReturn("YEAR");
        String key = String.format(MESSAGE_KEY_TEMPLATE, AvetmissEditorParser.Field.yearSchoolCompleted);
        when(messages.get(key)).thenReturn(key);
        AvetmissEditorParser parser = getAvetmissEditorParser();
        parser.parse();
        verify(contact.getStudent(), times(1)).setYearSchoolCompleted(null);
        assertFalse(parser.getErrors().isEmpty());
        assertEquals(1, parser.getErrors().size());
        assertEquals(key, parser.getErrors().get(AvetmissEditorParser.Field.yearSchoolCompleted.name()));
        assertNull(contact.getStudent().getYearSchoolCompleted());


    }

    @Test
    public void testValidYearSchoolCompleted()
    {
        when(request.getParameter(yearSchoolCompleted.name())).thenReturn("2000");
        AvetmissEditorParser parser = getAvetmissEditorParser();
        parser.parse();
        assertTrue(parser.getErrors().isEmpty());
        verify(contact.getStudent(), times(1)).setYearSchoolCompleted(2000);
    }

    @Test
    public void testInvalidYearSchoolCompleted()
    {
        when(request.getParameter(yearSchoolCompleted.name())).thenReturn("12");
        String key = String.format(MESSAGE_KEY_TEMPLATE, AvetmissEditorParser.Field.yearSchoolCompleted);
        when(messages.get(key)).thenReturn(key);


        AvetmissEditorParser parser = getAvetmissEditorParser();
        parser.parse();
        verify(contact.getStudent(), times(1)).setYearSchoolCompleted(12);
        assertFalse(parser.getErrors().isEmpty());
        assertEquals(1, parser.getErrors().size());
        assertEquals("Year school completed if supplied should be within not earlier than 1940.", parser.getErrors().get(AvetmissEditorParser.Field.yearSchoolCompleted.name()));
        verify(contact.getStudent(), times(1)).setYearSchoolCompleted(null);
        assertNull(contact.getStudent().getYearSchoolCompleted());
    }

    private AvetmissEditorParser getAvetmissEditorParser() {
        AvetmissEditorParser parser = new AvetmissEditorParser();
        parser.setContact(contact);
        parser.setCountryService(countryService);
        parser.setLanguageService(languageService);
        parser.setMessages(messages);
        parser.setRequest(request);
        return parser;
    }

}
