import { ValidationError } from "@api/model";

export interface Fetch {
  success?: boolean;
  pending?: boolean;
  number?: number;
  message?: string;
  persist?: boolean;
  formError?: ValidationError;
  hideIndicator?: boolean;
}
