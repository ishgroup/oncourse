import { LoginRequest } from '@api/model';

export interface LoginState {
  isLogged: boolean;
  loading: boolean;
}

export enum LoginStages {
  'Login',
  'EmaiConfirm',
  'PasswordConfirm',
  'PasswordReset'
}

export type LoginSubmitBy = 'LoginEmail' | 'SignIn';

export type LoginValues = LoginRequest & { confirmPassword?: string, submitBy?: LoginSubmitBy };

export type LoginStage = keyof typeof LoginStages;
