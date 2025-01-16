/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { DataCollectionForm, Field } from "@api/model";

export interface CollectionFormSchema {
  form: DataCollectionForm;
  items: CollectionFormItem[]
}

export type CollectionFormItem = CollectionFormHeading | CollectionFormField;

export interface CollectionFormHeading {
  baseType: "heading";
  description: string;
  name: string;
  parent?: string;
}

export interface CollectionFormField extends Field {
  baseType: "field";
  parent?: string;
}

export enum CollectionFormFieldTypesEnum {
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

