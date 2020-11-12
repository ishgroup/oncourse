import { ValidationError } from "@api/model";

export interface Fetch {
  success?: boolean;
  pending?: boolean;
  number?: number;
  message?: string;
  formError?: ValidationError;
  hideIndicator?: boolean;
}
