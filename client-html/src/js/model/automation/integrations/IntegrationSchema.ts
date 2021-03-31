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
  AzureFields, ServiceNSWFields
} from "./IntegrationsFields";

export interface IntegrationSchema {
  type: number;
  id: string;
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
}

export interface IntegrationTypeSchema {
  type: number;
  name: string;
  description: React.ReactNode;
}
