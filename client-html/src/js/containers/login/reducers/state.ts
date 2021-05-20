import { PasswordComplexity } from "@api/model";

export interface LoginState {
  isTOTP?: boolean;
  isNewTOTP?: boolean;
  isBasic?: boolean;
  isNewPassword?: boolean;
  strongPasswordValidation?: boolean;
  isEnableTOTP?: boolean;
  isOptionalTOTP?: boolean;
  isKickOut?: boolean;
  isUpdatePassword?: boolean;
  withNetworkFields?: boolean;
  totpUrl?: string;
  passwordChangeMessage?: string;
  passwordComplexity?: PasswordComplexity;
  email?: string;
}
