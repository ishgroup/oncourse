/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { SessionApi, Session, SessionWarning } from "@api/model";
import { DefaultHttpService } from "../../../../../../common/services/HttpService";

class CourseClassTimetableService {
  readonly service = new DefaultHttpService();

  readonly timetableApi = new SessionApi(this.service);

  public findTimetableSessionsForCourseClasses(id: number): Promise<Session[]> {
    return this.timetableApi.get(id);
  }

  public updateTimetableSessionsForCourseClass(classId: number, sessions: Session[]): Promise<any> {
    return this.timetableApi.update(classId, sessions);
  }

  public validateUpdate(classId: number, sessions: Session[]): Promise<SessionWarning[]> {
    return this.service.POST(`/v1/list/entity/courseClass/timetable/${classId || -1}`, sessions, {
      headers: { "X-validate-only":  "true" }
    });
  }
}

export default new CourseClassTimetableService();
