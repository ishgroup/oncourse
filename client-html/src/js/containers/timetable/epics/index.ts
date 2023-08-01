import { combineEpics } from "redux-observable";
import { EpicDeleteTimetableFilter } from "./EpicDeleteTimetableFilter";
import { EpicFindTimetableSessions } from "./EpicFindTimetableSessions";
import { EpicGetSessionTags } from "./EpicGetSessionTags";
import { EpicGetTimetableFilters } from "./EpicGetTimetableFilters";
import { EpicGetTimetableSessionsByIds } from "./EpicGetTimetableSessionsByIds";
import { EpicGetTimetableSessionsDays } from "./EpicGetTimetableSessionsDays";
import { EpicPostTimetableFilter } from "./EpicPostTimetableFilter";

export const EpicTimetable = combineEpics(
  EpicFindTimetableSessions,
  EpicGetTimetableSessionsByIds,
  EpicGetTimetableSessionsDays,
  EpicGetTimetableFilters,
  EpicPostTimetableFilter,
  EpicDeleteTimetableFilter,
  EpicGetSessionTags
);
