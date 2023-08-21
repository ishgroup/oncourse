import { combineEpics } from "redux-observable";
import { EpicDeleteHolidaysItem } from "./EpicDeleteHolidaysItem";
import { EpicGetHolidays } from "./EpicGetHolidays";
import { EpicSaveHolidays } from "./EpicSaveHolidays";

export const EpicHolidays = combineEpics(EpicGetHolidays, EpicSaveHolidays, EpicDeleteHolidaysItem);
