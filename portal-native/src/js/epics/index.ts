import { combineEpics } from 'redux-observable';
import EpicSignIn from './login/EpicSignIn';
import EpicEmailLogin from './login/EpicEmailLogin';
import EpicGetThirdPartyClientIds from './thirdParty/EpicGetThirdPartyClientIds';

export const EpicRoot = combineEpics(
  EpicSignIn,
  EpicEmailLogin,
  EpicGetThirdPartyClientIds
);
