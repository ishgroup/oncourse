import React from "react";
import {
  CanvasFields,
  CloudAssessFields,
  CoassembleFields,
  MailchimpFields,
  MicropowerFields,
  MoodleFields,
  MYOBFields,
  SurveyGizmoFields,
  SurveyMonkeyFields,
  XeroFields,
  AmazonS3Fields,
  AzureFields,
  ServiceNSWFields,
  KronosFields
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
    | SurveyGizmoFields
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
}

export interface IntegrationTypeSchema {
  name: string;
  image: string;
  form: React.ComponentType<any>;
  description: React.ReactNode;
}

export interface IntegrationTypesModel {
  [key: number]: IntegrationTypeSchema
}
