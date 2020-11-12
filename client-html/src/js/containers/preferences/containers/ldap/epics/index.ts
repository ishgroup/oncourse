import { combineEpics } from "redux-observable";

import { EpicGetLdapConnection } from "./EpicGetLdapConnection";

export const EpicLdap = combineEpics(EpicGetLdapConnection);
