import { combineEpics } from "redux-observable";
import { EpicGetRecords } from "./EpicGetRecords";
import { EpicQuickSearchConcessionType, EpicQuickSearchCorporatePasses } from "./EpicsEntityQuickSearch";

export const EpicRecords = combineEpics(EpicQuickSearchConcessionType, EpicQuickSearchCorporatePasses, EpicGetRecords);
