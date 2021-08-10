import { LoginState } from './Login';
import { MessageState } from './Message';
import { ThirdPartyState } from './ThirdParty';
import { SessionState } from './Session';

export interface State {
  login: LoginState;
  message: MessageState;
  thirdParty: ThirdPartyState;
  sessions: SessionState;
}
