import { combineEpics } from "redux-observable";
import { EpicApiTokens } from "../containers/api-tokens/epics";
import { EpicUserRoles } from "../containers/user-roles/epics";
import { EpicUsers } from "../containers/users/epics";

export const EpicSecurity = combineEpics(EpicUserRoles, EpicUsers, EpicApiTokens);
