import {ContactFields, FieldHeading, ValidationError, DataType} from "../../model";

const genderHeading:any = {
  "description": "Contact Details",
  "name": "Contact Details",
  "fields": [
    {
      "value": null,
      "defaultValue": null,
      "dataType": DataType.BOOLEAN,
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
      "dataType": DataType.DATE,
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
      "dataType": DataType.STRING,
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
      "dataType": DataType.STRING,
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
      "dataType": DataType.COUNTRY,
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
      "dataType": DataType.POSTCODE,
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
      "dataType": DataType.STRING,
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
      "dataType": DataType.PHONE,
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
      "dataType": DataType.PHONE,
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
      "dataType": DataType.STRING,
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
      "dataType": DataType.STRING,
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
      "dataType": DataType.DATE,
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

const communicateHeading:FieldHeading = {
  "description": "I would like to receive information and offers via:",
  "name": "e-mail, sms, post",
  "fields": [
    {
      "value": null,
      "defaultValue": "true",
      "dataType": DataType.BOOLEAN,
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
      "dataType": DataType.BOOLEAN,
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
      "dataType": DataType.BOOLEAN,
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
      "dataType": DataType.COUNTRY,
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
      "dataType": DataType.LANGUAGE,
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
      "dataType": DataType.ENUM,
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

export const ContactFieldsRequest:ContactFields = {
  "contactId": "1001",
    "headings": [
      communicateHeading
    ]
};

export const ContactFieldsErrorResponse:ValidationError = {
  formErrors: ["Form error example"],
  fieldsErrors: [{
    name: "isMale",
    error: "isMale Field error example"
  },
    {
      name: "street",
      error: "street Field error example"
    }
  ]
};
