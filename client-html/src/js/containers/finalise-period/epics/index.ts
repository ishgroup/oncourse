import { combineEpics } from "redux-observable";
import { EpicGetFinaliseInfo } from "./EpicGetFinaliseInfo";
import { EpicUpdateFinaliseDate } from "./EpicUpdateFinaliseDate";

export const EpicFanalise = combineEpics(EpicUpdateFinaliseDate, EpicGetFinaliseInfo);
