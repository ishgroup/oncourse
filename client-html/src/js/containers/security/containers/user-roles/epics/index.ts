import { combineEpics } from "redux-observable";
import { EpicDeleteUserRole } from "./EpicDeleteUserRole";
import { EpicGetUserRoles } from "./EpicGetUserRoles";
import { EpicUpdateUserRole } from "./EpicUpdateUserRole";

export const EpicUserRoles = combineEpics(EpicGetUserRoles, EpicDeleteUserRole, EpicUpdateUserRole);
