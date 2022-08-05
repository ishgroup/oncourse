import React from "react";
import {
  CanvasFields,
  CloudAssessFields,
  CoassembleFields,
  MailchimpFields,
  MicropowerFields,
  MoodleFields,
  MYOBFields,
  AlchemerFields,
  SurveyMonkeyFields,
  XeroFields,
  AmazonS3Fields,
  AzureFields, ServiceNSWFields
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
}

export interface IntegrationTypeSchema {
  name: string;
  image: string;
  form: React.ComponentType<any>;
  formName: string;
  description: React.ReactNode;
}

export interface IntegrationTypesModel {
  [key: number]: IntegrationTypeSchema
}
