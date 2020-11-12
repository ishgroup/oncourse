import { combineEpics } from "redux-observable";
import { EpicGetHolidays } from "./EpicGetHolidays";
import { EpicSaveHolidays } from "./EpicSaveHolidays";
import { EpicDeleteHolidaysItem } from "./EpicDeleteHolidaysItem";

export const EpicHolidays = combineEpics(EpicGetHolidays, EpicSaveHolidays, EpicDeleteHolidaysItem);
