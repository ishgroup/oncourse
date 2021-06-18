import * as L from "lodash";
import faker from "faker";
import { v4 as uuid } from "uuid";
import {
  CourseClass, Enrolment, Contact, Field, DataType, FieldHeading, Item,
  Voucher, Product, Membership, Article, Application,
} from "../../../js/model";
import {
  mockChoiceField, mockContact, mockCourseClass, mockEnumField, mockField, mockProduct,
  mockWaitingCourse,
} from "./MockFunctions";
import {normalize} from "normalizr";
import {
  ClassesListSchema, ContactsSchema, ContactsState, ProductsListSchema, WaitingCoursesListSchema,
} from "../../../js/NormalizeSchema";
import {WaitingList} from "../../../js/model/checkout/WaitingList";
import {Course} from "../../../js/model/web/Course";
import { localForage } from "../../constants/LocalForage";

export const CreateMockDB = (): MockDB => {
  const result: MockDB = new MockDB();
  localForage.getItem("MockDB").then((db: MockDB) => {
    if (db) {
      result.classes = db.classes;
      result.contacts = db.contacts;
      result.fields = db.fields;
      result.countries = db.countries;
      result.suburbs = db.suburbs;
      result.products = db.products;
      result.waitingCourses = db.waitingCourses;
    } else {
      localForage.setItem("MockDB", result);
    }
  }).catch(e => {
    console.error(e);
  });
  return result;
};

export class MockDB {

  private id: string = uuid();

  contacts: ContactsState = null;
  classes: { entities: any, result: any } = null;
  products: { entities: any, result: any } = null;
  waitingCourses: { entities: any, result: any } = null;
  fields: Field[] = [];
  countries: Item[] = [];
  suburbs: Item[] = [];
  languages: Item[] = [];
  postcodes: Item[] = [];

  constructor() {
    this.init();
  }

  init(): void {
    this.contacts = normalize([mockContact(), mockContact(), mockContact()], ContactsSchema);
    this.classes = normalize(
      [mockCourseClass(),
        mockCourseClass(),
        mockCourseClass(),
        mockCourseClass(),
        mockCourseClass(),
        mockCourseClass(),
        mockCourseClass()],
      ClassesListSchema);

    this.products = normalize([
      mockProduct(),
      mockProduct(),
      mockProduct(),
      mockProduct(),
      mockProduct(),
      mockProduct(),
      mockProduct(),
    ],                        ProductsListSchema);

    this.waitingCourses = normalize([
      mockWaitingCourse(),
      mockWaitingCourse(),
      mockWaitingCourse(),
      mockWaitingCourse(),
      mockWaitingCourse(),
      mockWaitingCourse(),
      mockWaitingCourse(),
    ],                              WaitingCoursesListSchema);

    this.fields = [
      mockField("Street", "street", DataType.STRING, true, 'Kirova'),
      mockField("Passport number", "customField.contact.passportNumber", DataType.STRING, true),
      mockField("Other info", "customField.contact.information", DataType.LONG_STRING, true),
      mockField("Suburb", "suburb", DataType.SUBURB, true),
      mockField("Postcode", "postcode", DataType.POSTCODE, true, "2202"),
      mockField("State", "state", DataType.STRING),
      mockField("Country", "country", DataType.COUNTRY),
      mockField("Street", "street", DataType.STRING),
      mockField("Home phone number", "homePhoneNumber", DataType.PHONE),
      mockField("Business phone number", "businessPhoneNumber", DataType.PHONE),
      mockField("Fax number", "faxNumber", DataType.PHONE),
      mockField("Mobile phone number", "mobilePhoneNumber", DataType.PHONE, true),
      mockField("Date of Birth", "dateOfBirth", DataType.DATE, false, "22/12/1980"),
      mockField("ABN", "abn", DataType.STRING),
      mockEnumField("Gender", "isMale", "Gender",
    [{
            key: "0",
            value: "Female",
            },
            {
              key: "1",
              value: "Male",
            },
            {
              key: "2",
              value: "Other",
            },
          ],
      ),
      mockField("E-mail", "isMarketingViaEmailAllowed", DataType.BOOLEAN, false, true),
      mockField("Post", "isMarketingViaPostAllowed", DataType.BOOLEAN),
      mockField("SMS", "isMarketingViaSMSAllowed", DataType.BOOLEAN),
      mockChoiceField(
        "Passport type",
        "customField.contact.passportType",
        "Passport type",
        [{key: "1", value: "MP"}, {key: "2", value: "MK"}],
        true,
        "MPP",
      ),
      mockEnumField("Citizenship", "citizenship", "StudentCitizenship",
        [{key: "1", value: "Australian citizen"},
          {key: "2", value: "New Zealand citizen"},
          {key: "3", value: "Students/Applicants with permanent visa"},
          {key: "4", value: "Student/Applicant has a temporary entry permit"},
          {key: "5", value: "Not one of the above categories"},
          {key: "8", value: "Students/Applicants with permanent humanitarian visa"},
          {key: "9", value: "No information"},
        ],
      ),
      mockField("Country of birth", "countryOfBirth", DataType.COUNTRY),
      mockField("Language spoken at Home", "languageHome", DataType.LANGUAGE, false, 'English'),
      mockField("Year school completed", "yearSchoolCompleted", DataType.INTEGER, false, 1980),
      mockEnumField("English proficiency", "englishProficiency", "AvetmissStudentEnglishProficiency",
        [{key: "0", value: "not stated"},
          {key: "1", value: "Very Well"},
          {key: "2", value: "Well"},
          {key: "3", value: "Not Well"},
          {key: "4", value: "Not at all"}]),
      mockEnumField("Indigenous Status", "indigenousStatus", "AvetmissStudentIndigenousStatus",
        [{key: "0", value: "not stated"},
          {key: "1", value: "Aboriginal"},
          {key: "2", value: "Torres Strait Islander"},
          {key: "3", value: "Aboriginal and Torres Strait Islander"},
          {key: "4", value: "Neither"}]),
      mockEnumField("Highest school level", "highestSchoolLevel", "AvetmissStudentSchoolLevel",
        [{key: "0", value: "not stated"}, {key: "1", value: "Did not go to school"},
          {key: "2", value: "Year 8 or below"},
          {key: "3", value: "Year 9"},
          {key: "4", value: "Year 10"},
          {key: "5", value: "Year 11"},
          {key: "6", value: "Year 12"},
        ]),
      mockField("Still at school", "isStillAtSchool", DataType.BOOLEAN),
      mockEnumField("Prior education code", "priorEducationCode", "AvetmissStudentPriorEducation", [
        {key: "0", value: "not stated"},
        {key: "1", value: "Bachelor degree or higher degree level"},
        {key: "2", value: "Advanced diploma or associate degree level"},
        {key: "3", value: "Diploma level"},
        {key: "4", value: "Certificate IV"},
        {key: "5", value: "Certificate III"},
        {key: "6", value: "Certificate II"},
        {key: "7", value: "Certificate I"},
        {key: "8", value: "Miscellaneous education"},
        {key: "100", value: "None"},
      ]),
      mockEnumField("Labour force status", "labourForceStatus", "AvetmissStudentLabourStatus", [
        {key: "0", value: "not stated"},
        {key: "1", value: "Full-time employee"},
        {key: "2", value: "Part-time employee"},
        {key: "3", value: "Self-employed, not employing others"},
        {key: "4", value: "Employer"},
        {key: "5", value: "Employed - unpaid in family business"},
        {key: "6", value: "Unemployed - seeking full-time work"},
        {key: "7", value: "Unemployed - seeking part-time work"},
        {key: "8", value: "Not employed - not seeking employment"}]),
      mockEnumField("Disability type", "disabilityType", "AvetmissStudentDisabilityType",
        [{key: "0", value: "not stated"},
          {key: "100", value: "none"},
          {key: "1", value: "Hearing/Deaf"},
          {key: "2", value: "Physical"},
          {key: "3", value: "Intellectual"},
          {key: "4", value: "Learning"},
          {key: "5", value: "Mental illness"},
          {key: "6", value: "Acquired brain impairment"},
          {key: "7", value: "Vision"},
          {key: "8", value: "Medical condition"},
          {key: "9", value: "Other"}]),
      mockField("Special needs", "specialNeeds", DataType.STRING),
    ];
    this.countries = [
      {key: "1", value: "Unknown"},
      {key: "2", value: "Born at sea"},
      {key: "3", value: "Not Stated"},
      {key: "4", value: "Australia"},
      {key: "5", value: "Norfolk Island"},
      {key: "6", value: "Australian External Territories, nec"},
      {key: "7", value: "New Zealand"},
      {key: "8", value: "New Caledonia"},
      {key: "9", value: "Papua New Guinea"},
      {key: "10", value: "Solomon Islands"},
      {key: "11", value: "Vanuatu"},
      {key: "12", value: "Guam"},
      {key: "13", value: "Kiribati"},
      {key: "14", value: "Marshall Islands"},
      {key: "15", value: "Micronesia, Federated States of"},
      {key: "16", value: "Nauru"},
      {key: "17", value: "Northern Mariana Islands"},
      {key: "18", value: "Palau"},
      {key: "19", value: "Cook Islands"},
      {key: "20", value: "Fiji"},
      {key: "21", value: "French Polynesia"},
      {key: "22", value: "Niue"},
      {key: "23", value: "Samoa"},
      {key: "24", value: "Samoa, American"},
      {key: "25", value: "Tokelau"},
      {key: "26", value: "Tonga"},
      {key: "27", value: "Tuvalu"},
      {key: "28", value: "Wallis and Futuna"},
      {key: "29", value: "Polynesia (excludes Hawaii), nec"},
      {key: "30", value: "Adelie Land (France)"},
      {key: "31", value: "Argentinian Antarctic Territory"},
      {key: "32", value: "Australian Antarctic Territory"},
      {key: "33", value: "British Antarctic Territory"},
      {key: "34", value: "Chilean Antarctic Territory"},
      {key: "35", value: "Queen Maud Land (Norway)"},
      {key: "36", value: "Ross Dependency (New Zealand)"},
      {key: "37", value: "Channel Islands"},
      {key: "38", value: "England"},
      {key: "39", value: "Isle of Man"},
      {key: "40", value: "Northern Ireland"},
      {key: "41", value: "Scotland"},
      {key: "42", value: "Wales"},
      {key: "43", value: "Ireland"},
      {key: "44", value: "Austria"},
      {key: "45", value: "Belgium"},
      {key: "46", value: "France"},
      {key: "47", value: "Germany"},
      {key: "48", value: "Liechtenstein"},
      {key: "49", value: "Luxembourg"},
      {key: "50", value: "Monaco"},
      {key: "51", value: "Netherlands"},
      {key: "52", value: "Switzerland"},
      {key: "53", value: "Denmark"},
      {key: "54", value: "Faeroe Islands"},
      {key: "55", value: "Finland"},
      {key: "56", value: "Greenland"},
      {key: "57", value: "Iceland"},
      {key: "58", value: "Norway"},
      {key: "59", value: "Sweden"},
      {key: "60", value: "Andorra"},
      {key: "61", value: "Gibraltar"},
      {key: "62", value: "Holy See"},
      {key: "63", value: "Italy"},
      {key: "64", value: "Malta"},
      {key: "65", value: "Portugal"},
      {key: "66", value: "San Marino"},
      {key: "67", value: "Spain"},
      {key: "68", value: "Albania"},
      {key: "69", value: "Bosnia and Herzegovina"},
      {key: "70", value: "Bulgaria"},
      {key: "71", value: "Croatia"},
      {key: "72", value: "Cyprus"},
      {key: "73", value: "Former Yugoslav Republic of Macedonia (FYROM)"},
      {key: "74", value: "Greece"},
      {key: "75", value: "Moldova"},
      {key: "76", value: "Romania"},
      {key: "77", value: "Slovenia"},
      {key: "78", value: "Serbia and Montenegro"},
      {key: "79", value: "Belarus"},
      {key: "80", value: "Czech Republic"},
      {key: "81", value: "Estonia"},
      {key: "82", value: "Hungary"},
      {key: "83", value: "Latvia"},
      {key: "84", value: "Lithuania"},
      {key: "85", value: "Poland"},
      {key: "86", value: "Russian Federation"},
      {key: "87", value: "Slovakia"},
      {key: "88", value: "Ukraine"},
      {key: "89", value: "Algeria"},
      {key: "90", value: "Egypt"},
      {key: "91", value: "Libya"},
      {key: "92", value: "Morocco"},
      {key: "93", value: "Sudan"},
      {key: "94", value: "Tunisia"},
      {key: "95", value: "Western Sahara"},
      {key: "96", value: "North Africa, nec"},
      {key: "97", value: "Bahrain"},
      {key: "98", value: "Gaza Strip and West Bank"},
      {key: "99", value: "Iran"},
      {key: "100", value: "Iraq"},
      {key: "101", value: "Israel"},
      {key: "102", value: "Jordan"},
      {key: "103", value: "Kuwait"},
      {key: "104", value: "Lebanon"},
      {key: "105", value: "Oman"},
      {key: "106", value: "Qatar"},
      {key: "107", value: "Saudi Arabia"},
      {key: "108", value: "Syria"},
      {key: "109", value: "Turkey"},
      {key: "110", value: "United Arab Emirates"},
      {key: "111", value: "Yemen"},
      {key: "112", value: "Burma (Myanmar)"},
      {key: "113", value: "Cambodia"},
      {key: "114", value: "Laos"},
      {key: "115", value: "Thailand"},
      {key: "116", value: "Viet Nam"},
      {key: "117", value: "Brunei Darussalam"},
      {key: "118", value: "Indonesia"},
      {key: "119", value: "Malaysia"},
      {key: "120", value: "Philippines"},
      {key: "121", value: "Singapore"},
      {key: "122", value: "East Timor"},
      {key: "123", value: "China (excludes SARs and Taiwan Province)"},
      {key: "124", value: "Hong Kong (SAR of China)"},
      {key: "125", value: "Macau (SAR of China)"},
      {key: "126", value: "Mongolia"},
      {key: "127", value: "Taiwan"},
      {key: "128", value: "Japan"},
      {key: "129", value: "Korea, Democratic People's Republic of (North)"},
      {key: "130", value: "Korea, Republic of"},
      {key: "131", value: "Bangladesh"},
      {key: "132", value: "Bhutan"},
      {key: "133", value: "India"},
      {key: "134", value: "Maldives"},
      {key: "135", value: "Nepal"},
      {key: "136", value: "Pakistan"},
      {key: "137", value: "Sri Lanka"},
      {key: "138", value: "Afghanistan"},
      {key: "139", value: "Armenia"},
      {key: "140", value: "Azerbaijan"},
      {key: "141", value: "Georgia"},
      {key: "142", value: "Kazakhstan"},
      {key: "143", value: "Kyrgyz Republic"},
      {key: "144", value: "Tajikistan"},
      {key: "145", value: "Turkmenistan"},
      {key: "146", value: "Uzbekistan"},
      {key: "147", value: "Bermuda"},
      {key: "148", value: "Canada"},
      {key: "149", value: "St Pierre and Miquelon"},
      {key: "150", value: "United States of America"},
      {key: "151", value: "Argentina"},
      {key: "152", value: "Bolivia"},
      {key: "153", value: "Brazil"},
      {key: "154", value: "Chile"},
      {key: "155", value: "Colombia"},
      {key: "156", value: "Ecuador"},
      {key: "157", value: "Falkland Islands"},
      {key: "158", value: "French Guiana"},
      {key: "159", value: "Guyana"},
      {key: "160", value: "Paraguay"},
      {key: "161", value: "Peru"},
      {key: "162", value: "Suriname"},
      {key: "163", value: "Uruguay"},
      {key: "164", value: "Venezuela"},
      {key: "165", value: "South America, nec"},
      {key: "166", value: "Belize"},
      {key: "167", value: "Costa Rica"},
      {key: "168", value: "El Salvador"},
      {key: "169", value: "Guatemala"},
      {key: "170", value: "Honduras"},
      {key: "171", value: "Mexico"},
      {key: "172", value: "Nicaragua"},
      {key: "173", value: "Panama"},
      {key: "174", value: "Anguilla"},
      {key: "175", value: "Antigua and Barbuda"},
      {key: "176", value: "Aruba"},
      {key: "177", value: "Bahamas"},
      {key: "178", value: "Barbados"},
      {key: "179", value: "Cayman Islands"},
      {key: "180", value: "Cuba"},
      {key: "181", value: "Dominica"},
      {key: "182", value: "Dominican Republic"},
      {key: "183", value: "Grenada"},
      {key: "184", value: "Guadeloupe"},
      {key: "185", value: "Haiti"},
      {key: "186", value: "Jamaica"},
      {key: "187", value: "Martinique"},
      {key: "188", value: "Montserrat"},
      {key: "189", value: "Netherlands Antilles"},
      {key: "190", value: "Puerto Rico"},
      {key: "191", value: "St Kitts and Nevis"},
      {key: "192", value: "St Lucia"},
      {key: "193", value: "St Vincent and the Grenadines"},
      {key: "194", value: "Trinidad and Tobago"},
      {key: "195", value: "Turks and Caicos Islands"},
      {key: "196", value: "Virgin Islands, British"},
      {key: "197", value: "Virgin Islands, United States"},
      {key: "198", value: "Benin"},
      {key: "199", value: "Burkina Faso"},
      {key: "200", value: "Cameroon"},
      {key: "201", value: "Cape Verde"},
      {key: "202", value: "Central African Republic"},
      {key: "203", value: "Chad"},
      {key: "204", value: "Congo"},
      {key: "205", value: "Congo, Democratic Republic of"},
      {key: "206", value: "Cote d'Ivoire"},
      {key: "207", value: "Equatorial Guinea"},
      {key: "208", value: "Gabon"},
      {key: "209", value: "Gambia"},
      {key: "210", value: "Ghana"},
      {key: "211", value: "Guinea"},
      {key: "212", value: "Guinea-Bissau"},
      {key: "213", value: "Liberia"},
      {key: "214", value: "Mali"},
      {key: "215", value: "Mauritania"},
      {key: "216", value: "Niger"},
      {key: "217", value: "Nigeria"},
      {key: "218", value: "Sao Tome and Principe"},
      {key: "219", value: "Senegal"},
      {key: "220", value: "Sierra Leone"},
      {key: "221", value: "Togo"},
      {key: "222", value: "Angola"},
      {key: "223", value: "Botswana"},
      {key: "224", value: "Burundi"},
      {key: "225", value: "Comoros"},
      {key: "226", value: "Djibouti"},
      {key: "227", value: "Eritrea"},
      {key: "228", value: "Ethiopia"},
      {key: "229", value: "Kenya"},
      {key: "230", value: "Lesotho"},
      {key: "231", value: "Madagascar"},
      {key: "232", value: "Malawi"},
      {key: "233", value: "Mauritius"},
      {key: "234", value: "Mayotte"},
      {key: "235", value: "Mozambique"},
      {key: "236", value: "Namibia"},
      {key: "237", value: "Reunion"},
      {key: "238", value: "Rwanda"},
      {key: "239", value: "St Helena"},
      {key: "240", value: "Seychelles"},
      {key: "241", value: "Somalia"},
      {key: "242", value: "South Africa"},
      {key: "243", value: "Swaziland"},
      {key: "244", value: "Tanzania"},
      {key: "245", value: "Uganda"},
      {key: "246", value: "Zambia"},
      {key: "247", value: "Zimbabwe"},
      {key: "248", value: "Southern and East Africa, nec"},
    ];

    this.languages = [
      {
        key: "10",
        value: "European",
      },
      {
        key: "69",
        value: "Eastern Creole",
      },
      {
        key: "90",
        value: "Erse",
      },
      {
        key: "112",
        value: "England",
      },
      {
        key: "113",
        value: "English",
      },
      {
        key: "172",
        value: "Estonian",
      },
      {
        key: "185",
        value: "European south",
      },
      {
        key: "220",
        value: "El Salvadorian",
      },
      {
        key: "221",
        value: "Espagnol",
      },
      {
        key: "222",
        value: "Espanish",
      },
      {
        key: "223",
        value: "Espanol",
      },
      {
        key: "261",
        value: "Eastern European",
      },
      {
        key: "276",
        value: "East Slavic",
      },
      {
        key: "438",
        value: "Egyptian",
      },
      {
        key: "439",
        value: "Egytion",
      },
      {
        key: "771",
        value: "Eastern Asian",
      },
      {
        key: "889",
        value: "East Aboriginal",
      },
      {
        key: "1066",
        value: "Emmi",
      },
      {
        key: "1132",
        value: "English Djambarrpuyngou",
      },
      {
        key: "1148",
        value: "English Gupapuyngu",
      },
      {
        key: "1260",
        value: "Eacham",
      },
      {
        key: "1452",
        value: "Eastern Arrada",
      },
      {
        key: "1453",
        value: "Eastern Arrante",
      },
      {
        key: "1454",
        value: "Eastern Arrente",
      },
      {
        key: "1827",
        value: "Ebonics",
      },
      {
        key: "1828",
        value: "Eskimo",
      },
      {
        key: "1841",
        value: "Eritrean",
      },
      {
        key: "1842",
        value: "Ethiopa",
      },
      {
        key: "1843",
        value: "Ethiopian",
      },
      {
        key: "1890",
        value: "Ewe",
      },
      {
        key: "1938",
        value: "Edo",
      },
      {
        key: "1939",
        value: "Edo Ishan",
      },
      {
        key: "1940",
        value: "Efik",
      },
      {
        key: "2058",
        value: "English Pidgeon",
      },
      {
        key: "2059",
        value: "English Pidgin",
      },
      {
        key: "2060",
        value: "English Pigin",
      },
      {
        key: "2112",
        value: "Esperanto",
      },
      {
        key: "2142",
        value: "English signed",
      },
    ];

    this.postcodes = [
      {
        key: "1200",
        value: {
          state: null,
          suburb: "SYDNEY",
          postcode: "1200",
        },
      },
      {
        key: "1201",
        value: {
          state: null,
          suburb: "SYDNEY",
          postcode: "1201",
        },
      },
      {
        key: "1202",
        value: {
          state: null,
          suburb: "SYDNEY",
          postcode: "1202",
        },
      },
      {
        key: "1203",
        value: {
          state: null,
          suburb: "SYDNEY",
          postcode: "1203",
        },
      },
      {
        key: "1204",
        value: {
          state: null,
          suburb: "SYDNEY",
          postcode: "1204",
        },
      },
      {
        key: "1205",
        value: {
          state: null,
          suburb: "SYDNEY",
          postcode: "1205",
        },
      },
      {
        key: "1206",
        value: {
          state: null,
          suburb: "SYDNEY",
          postcode: "1206",
        },
      },
      {
        key: "1207",
        value: {
          state: null,
          suburb: "SYDNEY",
          postcode: "1207",
        },
      },
      {
        key: "1208",
        value: {
          state: null,
          suburb: "HAYMARKET",
          postcode: "1208",
        },
      },
      {
        key: "1209",
        value: {
          state: null,
          suburb: "AUSTRALIA SQUARE",
          postcode: "1209",
        },
      },
    ];

    this.suburbs = [
      {
        key: "2250",
        value: {state: "NSW", suburb: "LISAROW", postcode: "2250"},
      },
      {
        key: "2000",
        value: {state: "NSW", suburb: "DAWES POINT", postcode: "2000"},
      },
      {
        key: "3000",
        value: {state: "NSW", suburb: "MELBOURNE", postcode: "3000"},
      },
      {
        key: "4000",
        value: {state: "NSW", suburb: "MELBOURNE1", postcode: "4000"},
      },
      {
        key: "5000",
        value: {state: "NSW", suburb: "MELBOURNE2", postcode: "5000"},
      },
      {
        key: "6000",
        value: {state: "NSW", suburb: "MELBOURNE3", postcode: "6000"},
      },
      {
        key: "7000",
        value: {state: "NSW", suburb: "MELBOURNE4", postcode: "7000"},
      },
    ];
  }


  createEnrolment(contactId: string, classId: string, errors: boolean = false, warnings: boolean = false): Enrolment {
    return {
      contactId: this.contacts.entities.contact[contactId].id,
      classId: this.classes.entities.classes[classId].id,
      errors: errors ? [faker.hacker.phrase(), faker.hacker.phrase()] : [],
      warnings: warnings ? [faker.hacker.phrase(), faker.hacker.phrase()] : [],
      price: this.classes.entities.classes[classId].price,
      selected: !errors,
    };
  }

  createApplication(contactId: string, classId: string, errors: boolean = false, warnings: boolean = false): Application {
    return {
      contactId: this.contacts.entities.contact[contactId].id,
      classId: this.classes.entities.classes[classId].id,
      errors: errors ? [faker.hacker.phrase(), faker.hacker.phrase()] : [],
      warnings: warnings ? [faker.hacker.phrase(), faker.hacker.phrase()] : [],
      selected: !errors,
    };
  }


  createVoucher(contactId: string, productId: string, errors: boolean = false, warnings: boolean = false): Voucher {
    return {
      contactId: this.contacts.entities.contact[contactId].id,
      productId: this.products.entities.products[productId].id,
      errors: errors ? [faker.hacker.phrase(), faker.hacker.phrase()] : [],
      warnings: warnings ? [faker.hacker.phrase(), faker.hacker.phrase()] : [],
      price: this.products.entities.products[productId].price,
      value: this.products.entities.products[productId].price,
      classes: [faker.commerce.productName(), faker.commerce.productName(), faker.commerce.productName(), faker.commerce.productName(), faker.commerce.productName()],
      selected: !errors,
      isEditablePrice: true,
    };
  }

  createMembership(contactId: string, productId: string, errors: boolean = false, warnings: boolean = false): Membership {
    return {
      contactId: this.contacts.entities.contact[contactId].id,
      productId: this.products.entities.products[productId].id,
      errors: errors ? [faker.hacker.phrase(), faker.hacker.phrase()] : [],
      warnings: warnings ? [faker.hacker.phrase(), faker.hacker.phrase()] : [],
      price: this.products.entities.products[productId].price,
      selected: !errors,
    };
  }

  createArticle(contactId: string, productId: string, errors: boolean = false, warnings: boolean = false): Article {
    return {
      contactId: this.contacts.entities.contact[contactId].id,
      productId: this.products.entities.products[productId].id,
      errors: errors ? [faker.hacker.phrase(), faker.hacker.phrase()] : [],
      warnings: warnings ? [faker.hacker.phrase(), faker.hacker.phrase()] : [],
      price: this.products.entities.products[productId].price,
      selected: !errors,
    };
  }

  createWaitingList(contactId: string, courseId: string, errors: boolean = false, warnings: boolean = false): WaitingList {
    return {
      contactId: this.contacts.entities.contact[contactId].id,
      courseId: this.waitingCourses.entities.waitingCourses[courseId].id,
      errors: errors ? [faker.hacker.phrase(), faker.hacker.phrase()] : [],
      warnings: warnings ? [faker.hacker.phrase(), faker.hacker.phrase()] : [],
      selected: !errors,
    };
  }

  addContact(contact: Contact): string {
    const nc = normalize([contact], ContactsSchema);
    this.contacts.result = [...this.contacts.result, ...nc.result];
    this.contacts.entities.contact = {...this.contacts.entities.contact, ...nc.entities.contact};
    localForage.setItem("MockDB", this).catch(e => {
      console.error(e);
    });
    return contact.id;
  }

  getContactByIndex(index: number): Contact {
    return this.contacts.entities.contact[this.contacts.result[index]];
  }

  getContactByDetails(firstName: String, lastName: String, email: String): Contact {
    return L.find(this.contacts.entities.contact,
                  (c: Contact) => c.firstName === firstName && c.lastName == lastName && c.email == email);
  }


  getCourseClassByIndex(index: number): CourseClass {
    return this.classes.entities.classes[this.classes.result[index]];
  }

  getProductClassByIndex(index: number): Product {
    return this.products.entities.products[this.products.result[index]];
  }

  getContactById(id: string): Contact {
    return this.contacts.entities.contact[id];
  }

  getCourseClassById(id: string): CourseClass {
    return this.classes.entities.classes[id];
  }

  getProductClassById(id: string): Product {
    return this.products.entities.products[id];
  }

  getWaitingCourseById(id: string): Course {
    return this.waitingCourses.entities.waitingCourses[id];
  }

  getFieldByKey(key: string): Field {
    return this.fields.find((f: Field) => f.key === key);
  }

  getFieldHeadingBy(keys: string[]): FieldHeading {
    const result: FieldHeading = new FieldHeading();
    result.name = faker.commerce.department();
    result.description = faker.hacker.phrase();
    result.fields = keys.map((k: string) => this.getFieldByKey(k));
    result.ordering = 0;
    return result;
  }

  searchCountryBy(search: string): Item[] {
    return this.countries.filter((i: Item) => i.value.toLowerCase().startsWith(search));
  }
}

