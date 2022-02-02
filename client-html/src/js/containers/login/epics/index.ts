import { combineEpics } from "redux-observable";
import { EpicPostAuthentication } from "./EpicPostAuthentication";
import { EpicUpdatePassword } from "./EpicUpdatePassword";
import { EpicValidatePassword } from "./EpicValidatePassword";
import { EpicGetEmailByToken } from "./EpicGetEmailByToken";
import { EpicCreatePassword } from "./EpicCreatePassword";
import { EpicGetUser } from "./EpicGetUser";

export const EpicLogin = combineEpics(
  EpicPostAuthentication,
  EpicGetUser,
  EpicUpdatePassword,
  EpicValidatePassword,
  EpicGetEmailByToken,
  EpicCreatePassword
);
