import { combineEpics } from "redux-observable";
import { EpicUserRoles } from "../containers/user-roles/epics";
import { EpicUsers } from "../containers/users/epics";
import { EpicApiTokens } from "../containers/api-tokens/epics";

export const EpicSecurity = combineEpics(EpicUserRoles, EpicUsers, EpicApiTokens);
