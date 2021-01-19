import { combineEpics } from "redux-observable";
import { EpicPostAuthentication } from "./EpicPostAuthentication";
import { EpicUpdatePassword } from "./EpicUpdatePassword";
import { EpicValidatePassword } from "./EpicValidatePassword";
import { EpicGetEmailByToken } from "./EpicGetEmailByToken";
import { EpicCreatePassword } from "./EpicCreatePassword";

export const EpicLogin = combineEpics(EpicPostAuthentication, EpicUpdatePassword, EpicValidatePassword, EpicGetEmailByToken, EpicCreatePassword);
