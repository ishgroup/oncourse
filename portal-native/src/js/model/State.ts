import { LoginState } from './Login';
import { MessageState } from './Message';
import { ThirdPartyState } from './ThirdParty';

export interface State {
  login: LoginState;
  message: MessageState;
  thirdParty: ThirdPartyState;
}
