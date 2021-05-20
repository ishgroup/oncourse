import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_DUPLICATE_COURSE_CLASSES_SESSIONS, setDuplicateCourseClassesSessions } from "../actions";
import TimetableService from "../../../timetable/services/TimetableService";
import { TimetableSession } from "../../../../model/timetable";
import EntityService from "../../../../common/services/EntityService";

const request: EpicUtils.Request<
  { sessions: TimetableSession[]; from: Date; hasZeroWages?: boolean },
  string[]
> = {
  type: GET_DUPLICATE_COURSE_CLASSES_SESSIONS,
  getData: selection =>
    TimetableService.findTimetableSessionsForCourseClasses(selection.toString()).then(sessionsRes =>
      (sessionsRes.length ? TimetableService.getSessionTags(sessionsRes.map(s => s.id)) : Promise.resolve([])).then(
        tagsRes => {
          const sessions = sessionsRes.map((s, index) => ({ ...s, tags: tagsRes[index] }));

          return EntityService.getPlainRecords("CourseClass", "hasZeroWages", `id in (${selection.toString()})`).then(
            response => {
              const hasZeroWages = response.rows.some(r => r.values[0] === "true");

              return { sessions, from: sessions.length ? new Date(sessions[0].start) : new Date(), hasZeroWages };
            }
          );
        }
      )
    ),
  processData: ({ sessions, from, hasZeroWages }) => [setDuplicateCourseClassesSessions(sessions, from, hasZeroWages)]
};

export const EpicGetDuplicateCourseClassesSessions: Epic<any, any> = EpicUtils.Create(request);
