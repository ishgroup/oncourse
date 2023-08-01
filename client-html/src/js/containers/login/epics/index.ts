import { combineEpics } from "redux-observable";
import { EpicCreatePassword } from "./EpicCreatePassword";
import { EpicGetEmailByToken } from "./EpicGetEmailByToken";
import { EpicGetUser } from "./EpicGetUser";
import { EpicPostAuthentication } from "./EpicPostAuthentication";
import { EpicUpdatePassword } from "./EpicUpdatePassword";
import { EpicValidatePassword } from "./EpicValidatePassword";

export const EpicLogin = combineEpics(
  EpicPostAuthentication,
  EpicGetUser,
  EpicUpdatePassword,
  EpicValidatePassword,
  EpicGetEmailByToken,
  EpicCreatePassword
);
