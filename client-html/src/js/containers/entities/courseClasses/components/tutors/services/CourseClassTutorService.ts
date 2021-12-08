/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { CourseClassTutor, CourseClassTutorApi } from "@api/model";
import { DefaultHttpService } from "../../../../../../common/services/HttpService";

class CourseClassTutorService {
  readonly service = new DefaultHttpService();
  readonly courseClassTutorApi = new CourseClassTutorApi(this.service);

  public getCourseClassTutors(id: number): Promise<CourseClassTutor[]> {
    return this.courseClassTutorApi.get(id);
  }

  public deleteCourseClassTutor(id: number): Promise<any> {
    return this.courseClassTutorApi._delete(id);
  }

  public postCourseClassTutor(tutor: CourseClassTutor): Promise<any> {
    return this.courseClassTutorApi.create(tutor);
  }

  public putCourseClassTutor(tutor: CourseClassTutor): Promise<CourseClassTutor[]> {
    return this.courseClassTutorApi.update(tutor.id, tutor);
  }

  public validateDelete(id: number): Promise<any> {
    return this.service.DELETE(`/v1/list/entity/courseClass/tutor/${id}`, {
      headers: { "X-validate-only":  "true" }
    });
  }

  public validatePost(tutor: CourseClassTutor): Promise<any> {
    return this.service.POST(`/v1/list/entity/courseClass/tutor`, tutor, {
      headers: { "X-validate-only":  "true" }
    });
  }

  public validatePut(tutor: CourseClassTutor): Promise<any> {
    return this.service.PUT(`/v1/list/entity/courseClass/tutor/${tutor.id}`, tutor, {
      headers: { "X-validate-only":  "true" }
    });
  }
}

export default new CourseClassTutorService();
