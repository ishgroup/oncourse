import { combineEpics } from "redux-observable";
import { EpicGetUserRoles } from "./EpicGetUserRoles";
import { EpicDeleteUserRole } from "./EpicDeleteUserRole";
import { EpicUpdateUserRole } from "./EpicUpdateUserRole";

export const EpicUserRoles = combineEpics(EpicGetUserRoles, EpicDeleteUserRole, EpicUpdateUserRole);
