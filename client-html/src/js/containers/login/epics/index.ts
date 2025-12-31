import { combineEpics } from "redux-observable";
import { EpicCreatePassword } from "./EpicCreatePassword";
import { EpicGetEmailByToken } from "./EpicGetEmailByToken";
import { EpicGetSSOIntegrations } from "./EpicGetSSOIntegrations";
import { EpicGetUser } from "./EpicGetUser";
import { EpicKickOutWithSSO } from './EpicKickOutWithSSO';
import { EpicPostAuthentication } from "./EpicPostAuthentication";
import { EpicPostSSOAuthentication } from "./EpicPostSSOAuthentification";
import { EpicUpdatePassword } from "./EpicUpdatePassword";
import { EpicValidatePassword } from "./EpicValidatePassword";

export const EpicLogin = combineEpics(
  EpicPostAuthentication,
  EpicGetUser,
  EpicUpdatePassword,
  EpicValidatePassword,
  EpicGetEmailByToken,
  EpicCreatePassword,
  EpicKickOutWithSSO,
  EpicGetSSOIntegrations,
  EpicPostSSOAuthentication
);
