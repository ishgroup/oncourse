import { combineEpics } from "redux-observable";
import { EpicGetAudit } from "./EpicGetAudit";

export const EpicAudits = combineEpics(EpicGetAudit);
