import React from "react";
import {
  AlchemerFields,
  AmazonS3Fields,
  AzureFields,
  CanvasFields,
  CloudAssessFields,
  CoassembleFields,
  KronosFields,
  MailchimpFields,
  MicropowerFields,
  MoodleFields,
  MYOBFields, OktaFields,
  ServiceNSWFields,
  SurveyMonkeyFields,
  XeroFields
} from "./IntegrationsFields";

export interface IntegrationSchema {
  type: number;
  id: number;
  name: string;
  configured?: boolean;
  verificationCode?: string;
  created: string;
  modified: string;
  fields:
    | MoodleFields
    | MailchimpFields
    | MYOBFields
    | AlchemerFields
    | SurveyMonkeyFields
    | XeroFields
    | CanvasFields
    | CloudAssessFields
    | MicropowerFields
    | CoassembleFields
    | AmazonS3Fields
    | AzureFields
    | ServiceNSWFields
    | KronosFields
    | OktaFields
}

export interface IntegrationTypeSchema {
  name: string;
  image: string;
  form: React.ComponentType<any>;
  formName: string;
  description: React.ReactNode;
}

export enum IntegrationTypesEnum {
  'Moodle' = 1,
  'Mailchimp',
  'SurveyMonkey',
  'Alchemer',
  'Xero',
  'MYOB',
  'Cloud Assess',
  'Canvas',
  'Micropower',
  'USI Agency',
  'VET Student Loans',
  'Google Classroom',
  'Coassemble',
  'TalentLMS',
  'LearnDash',
  'Amazon S3',
  'Microsoft Azure',
  'Service NSW voucher',
  'Kronos',
  'Okta'
}

export type IntegrationTypesModel = {
  [key in IntegrationTypesEnum]: IntegrationTypeSchema
}
