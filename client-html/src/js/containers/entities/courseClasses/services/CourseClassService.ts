/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import {
  CourseClassDuplicate,
  CourseClassApi,
  CourseClass,
  CancelCourseClass, Diff
} from "@api/model";
import { DefaultHttpService } from "../../../../common/services/HttpService";

class CourseClassService {
  readonly courseClassApi = new CourseClassApi(new DefaultHttpService());

  public duplicate(courseClassDuplicate: CourseClassDuplicate): Promise<any> {
    return this.courseClassApi.duplicateClass(courseClassDuplicate);
  }

  public getCourseClass(id: number): Promise<CourseClass> {
    return this.courseClassApi.get(id);
  }

  public updateCourseClass(id: number, courseClass: CourseClass): Promise<any> {
    return this.courseClassApi.update(id, courseClass);
  }

  public createCourseClass(courseClass: CourseClass): Promise<number> {
    return this.courseClassApi.create(courseClass);
  }

  public deleteCourseClass(id: number): Promise<any> {
    return this.courseClassApi.remove(id);
  }

  public cancelClass(cancelCourseClass: CancelCourseClass): Promise<any> {
    return this.courseClassApi.cancelClass(cancelCourseClass);
  }

  public bulkChange(diff: Diff): Promise<any> {
    return this.courseClassApi.bulkChange(diff);
  }
}

export default new CourseClassService();
