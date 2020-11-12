import { combineEpics } from "redux-observable";
import { EpicFindTimetableSessions } from "./EpicFindTimetableSessions";
import { EpicGetTimetableSessionsByIds } from "./EpicGetTimetableSessionsByIds";
import { EpicGetTimetableSessionsDays } from "./EpicGetTimetableSessionsDays";
import { EpicGetTimetableFilters } from "./EpicGetTimetableFilters";
import { EpicPostTimetableFilter } from "./EpicPostTimetableFilter";
import { EpicDeleteTimetableFilter } from "./EpicDeleteTimetableFilter";
import { EpicGetSessionTags } from "./EpicGetSessionTags";

export const EpicTimetable = combineEpics(
  EpicFindTimetableSessions,
  EpicGetTimetableSessionsByIds,
  EpicGetTimetableSessionsDays,
  EpicGetTimetableFilters,
  EpicPostTimetableFilter,
  EpicDeleteTimetableFilter,
  EpicGetSessionTags
);
