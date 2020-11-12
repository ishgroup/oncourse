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
  XeroFields
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
    | CoassembleFields;
}

export interface IntegrationTypeSchema {
  type: number;
  name: string;
  description: string;
}
