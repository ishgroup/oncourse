import { LoginRequest } from '@api/model';

export interface LoginState {
  isLogged: boolean;
  loading: boolean;
  verificationUrl?: string;
  stage: LoginStages;
}

export enum LoginStages {
  'Login',
  'PasswordReset',
  'CreateAccount',
  'EmaiConfirm',
}

export type LoginStageName = keyof typeof LoginStages;

export type LoginRoute = {
  key: LoginStageName;
};

export type LoginSubmitBy = 'LoginEmail' | 'SignIn';

export type LoginValues = LoginRequest & { confirmPassword?: string, submitBy?: LoginSubmitBy };
