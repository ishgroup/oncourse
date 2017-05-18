const genderHeading:any = {
  "description": "Contact Details",
  "name": "Contact Details",
  "fields": [
    {
      "value": null,
      "defaultValue": null,
      "dataType": "boolean",
      "id": "10",
      "key": "isMale",
      "description": "Gender",
      "enumItems": [],
      "mandatory": true,
      "enumType": null,
      "name": "Gender",
    }
  ]
};


const dateOfBirthHeading: any = {
  "description": "Contact Details",
  "name": "Contact Details",

  "fields": [
    {
      "value": null,
      "defaultValue": null,
      "dataType": "date",
      "id": "10",
      "key": "dateOfBirth",
      "description": "Date of Birth",
      "enumItems": [],
      "mandatory": true,
      "enumType": null,
      "name": "Date of Birth",
    }
  ]
};

const contactHeading: any = {
  "description": "Contact Details",
  "name": "Contact Details",
  "fields": [
    {
      "value": null,
      "defaultValue": null,
      "dataType": "string",
      "id": "1",
      "key": "street",
      "description": "Address",
      "enumItems": [],
      "mandatory": true,
      "enumType": null,
      "name": "Address"
    },
    {
      "value": null,
      "defaultValue": null,
      "dataType": "string",
      "id": "2",
      "key": "suburb",
      "description": "Suburb",
      "enumItems": [],
      "mandatory": true,
      "enumType": null,
      "name": "Suburb"
    },
    {
      "value": null,
      "defaultValue": null,
      "dataType": "country",
      "id": "3",
      "key": "country",
      "description": "Country",
      "enumItems": [],
      "mandatory": true,
      "enumType": null,
      "name": "Country"
    },
    {
      "value": null,
      "defaultValue": null,
      "dataType": "string",
      "id": "4",
      "key": "postcode",
      "description": "Postcode",
      "enumItems": [],
      "mandatory": true,
      "enumType": null,
      "name": "Postcode"
    },
    {
      "value": null,
      "defaultValue": null,
      "dataType": "string",
      "id": "5",
      "key": "state",
      "description": "State",
      "enumItems": [],
      "mandatory": true,
      "enumType": null,
      "name": "State"
    },
    {
      "value": null,
      "defaultValue": null,
      "dataType": "string",
      "id": "6",
      "key": "homePhoneNumber",
      "description": "Home phone number",
      "enumItems": [],
      "mandatory": true,
      "enumType": null,
      "name": "Home phone number"
    },
    {
      "value": null,
      "defaultValue": null,
      "dataType": "string",
      "id": "7",
      "key": "businessPhoneNumber",
      "description": "Business phone number",
      "enumItems": [],
      "mandatory": true,
      "enumType": null,
      "name": "Business phone number"
    },
    {
      "value": null,
      "defaultValue": null,
      "dataType": "string",
      "id": "8",
      "key": "faxNumber",
      "description": "Fax number",
      "enumItems": [],
      "mandatory": true,
      "enumType": null,
      "name": "Fax number"
    },
    {
      "value": null,
      "defaultValue": null,
      "dataType": "string",
      "id": "9",
      "key": "mobilePhoneNumber",
      "description": "Mobile phone number",
      "enumItems": [],
      "mandatory": true,
      "enumType": null,
      "name": "Mobile phone number",
    },
    {
      "value": null,
      "defaultValue": null,
      "dataType": "date",
      "id": "10",
      "key": "dateOfBirth",
      "description": "Date of Birth",
      "enumItems": [],
      "mandatory": true,
      "enumType": null,
      "name": "Date of Birth",
    }
  ],
};

const communicateHeading = {
  "description": "I would like to receive information and offers via:",
  "name": "e-mail, sms, post",
  "fields": [
    {
      "value": null,
      "defaultValue": "true",
      "dataType": "boolean",
      "id": "15",
      "key": "isMarketingViaEmailAllowed",
      "description": "E-mail",
      "enumItems": [],
      "mandatory": true,
      "enumType": null,
      "name": "E-mail"
    },
    {
      "value": null,
      "defaultValue": "true",
      "dataType": "boolean",
      "id": "16",
      "key": "isMarketingViaPostAllowed",
      "description": "Post",
      "enumItems": [],
      "mandatory": true,
      "enumType": null,
      "name": "Post"
    },
    {
      "value": null,
      "defaultValue": "true",
      "dataType": "boolean",
      "id": "17",
      "key": "isMarketingViaSMSAllowed",
      "description": "SMS",
      "enumItems": [],
      "mandatory": true,
      "enumType": null,
      "name": "SMS"
    }
  ]
};

const avetmissHeading = {
  "name": "Avetmiss Questions",
  "description": "The Federal Government requires all Colleges to ask the following questions to help with " +
  "educational planning. Your personal details will remain confidential and will not be identified in any National " +
  "collection.<strong id=\"questionnaire\" class=\"collapse\">Answering these questions is optional</strong>",
  "fields": [
    {
      "value": null,
      "defaultValue":  null,
      "dataType": "country",
      "id": "15",
      "key": "countryOfBirth",
      "description": "country",
      "enumItems": [],
      "mandatory": true,
      "enumType": null,
      "name": "In which country were you born?"
    },
    {
      "value": null,
      "defaultValue": null,
      "dataType": "language",
      "id": "16",
      "key": "languageHome",
      "description": "language",
      "enumItems": [],
      "mandatory": true,
      "enumType": null,
      "name": "What language do you normally speak at home?"
    },
    {
      "value": null,
      "defaultValue": null,
      "dataType": "enum",
      "id": "17",
      "key": "englishProficiency",
      "description": "How well do you speak English?",
      "enumItems":  [
        {
          "key": "0",
          "value": "not stated"
        },
        {
          "key": "1",
          "value": "Very Well"
        },
        {
          "key": "2",
          "value": "Well"
        },
        {
          "key": "3",
          "value": "Not Well"
        },
        {
          "key": "4",
          "value": "Not at all"
        }
      ],
      "mandatory": true,
      "enumType": "AvetmissStudentEnglishProficiency",
      "name": "How well do you speak English?"
    }
  ]
};

const response: any = {
  "contactId": "1001",
    "headings": [
      genderHeading,
      contactHeading,
      communicateHeading,
      avetmissHeading
    ]
};



export default response;



/* Default: Australia *//*}
 <ComboboxField
 name="englishProficiency"
 suggestions={englishProficiency}
 label="How well do you speak English?"
 />
 <ComboboxField
 name="indigenousStatus"
 suggestions={indigenousStatus}
 label="Are you of Aboriginal/Torres Strait Islander origin?"
 />
 <ComboboxField
 name="highestSchoolLevel"
 suggestions={highestSchoolLevel}
 label="What is your highest completed school level?"
 />
 <Field
 component={TextField}
 name="yearSchoolCompleted"
 label="In which year did you complete that school level?"
 type="text"
 />
 <ComboboxField
 name="stillAtSchool"
 suggestions={stillAtSchool}
 label="Are you still attending secondary school?"
 />
 <ComboboxField
 name="priorEducationCode"
 suggestions={priorEducationCode}
 label="What is the highest qualification level you have successfully completed?"
 />
 <ComboboxField
 name="labourForceStatus"
 suggestions={labourForceStatus}
 label="Of the following categories, which best describes your current employment status?"
 />
 <ComboboxField
 name="disabilityType"
 suggestions={disabilityType}
 label={disabilityTypeLabel}
 />
 </fieldset>
 );
 }

 function getMessage(isOptional: boolean) {
 return (
 <div className="message">
 <p>The Federal Government requires all Colleges to ask the following questions to help with
 educational planning. Your personal details will remain confidential and will not be identified in any National
 collection.
 {isOptional && <strong id="questionnaire" className="collapse">Answering these questions is optional</strong>}
 </p>
 </div>
 );
 }

 interface AtemnissDetailsComponentProps {
 isOptional: boolean;
 }*/

/*
CITIZENSHIP(ContextType.STUDENT, "Citizenship", "citizenship" ),
  COUNTRY_OF_BIRTH(ContextType.STUDENT, "Country of birth", "countryOfBirth" ),
  LANGUAGE_HOME(ContextType.STUDENT, "Language spoken at Home", "languageHome" ),
  YEAR_SCHOOL_COMPLETED(ContextType.STUDENT, "Year school completed", "yearSchoolCompleted"),
  ENGLISH_PROFICIENCY(ContextType.STUDENT, "English proficiency", "englishProficiency" ),
  INDIGENOUS_STATUS(ContextType.STUDENT, "Indigenous Status", "indigenousStatus" ),
  HIGHEST_SCHOOL_LEVEL(ContextType.STUDENT, "Highest school level", "highestSchoolLevel" ),
  IS_STILL_AT_SCHOOL(ContextType.STUDENT, "Still at school", "isStillAtSchool"),
  PRIOR_EDUCATION_CODE(ContextType.STUDENT, "Prior education code", "priorEducationCode" ),
  LABOUR_FORCE_STATUS(ContextType.STUDENT, "Labour force status", "labourForceStatus" ),
  DISABILITY_TYPE(ContextType.STUDENT, "Disability type", "disabilityType" ),
  SPECIAL_NEEDS(ContextType.STUDENT, "Special needs", "specialNeeds"),

*/
