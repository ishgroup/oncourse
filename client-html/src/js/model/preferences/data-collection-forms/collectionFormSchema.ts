export interface collectionFormSchema {
  name: string;
  type: collectionFormTypes;
  headings?: collectionFormHeading[];
  fields?: collectionFormField[];
}

export interface collectionFormHeading {
  name: string;
  description: string;
  fields?: collectionFormField[];
}

export interface collectionFormField {
  type: collectionFormFieldTypes;
  mandatory: boolean;
  label?: string;
  helpText?: string;
}

export enum collectionFormTypesEnum {
  "Application",
  "Enrolment",
  "Waiting List",
  "Survey"
}

export type collectionFormTypes = keyof typeof collectionFormTypesEnum;

export enum collectionFormFieldTypesEnum {
  "ABN",
  "Business phone number",
  "Citizenship",
  "Country",
  "Country of birth",
  "Date of Birth",
  "Disability type",
  "E-mail",
  "English proficiency",
  "Enrolment Enrolment",
  "Fax number",
  "Gender",
  "Highest school level",
  "Home phone number",
  "Indigenous Status",
  "Labour force status",
  "Language spoken at Home",
  "Mobile phone number",
  "Pasport Fields",
  "Post",
  "Postcode",
  "Prior education code",
  "SMS",
  "Special needs",
  "State",
  "Still at school",
  "Street",
  "Suburb",
  "Year school completed"
}

export type collectionFormFieldTypes = keyof typeof collectionFormFieldTypesEnum;

export enum collectionFormFieldRequirementEnum {
  "Optional",
  "Mandatory"
}

// export type collectionFormFieldRequirementTypes = keyof typeof collectionFormFieldRequirementEnum;
