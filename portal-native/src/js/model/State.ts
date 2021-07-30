import { LoginState } from './Login';
import { MessageState } from './Message';

export interface State {
  login: LoginState;
  message: MessageState;
}
