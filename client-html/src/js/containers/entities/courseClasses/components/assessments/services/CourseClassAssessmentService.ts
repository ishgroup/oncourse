/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { AssessmentClassApi, AssessmentClass } from "@api/model";
import { DefaultHttpService } from "../../../../../../common/services/HttpService";

class CourseClassAssessmentService {
  readonly service = new DefaultHttpService();

  readonly assessmentApi = new AssessmentClassApi(this.service);

  public getCourseClassAssessments(id: number): Promise<AssessmentClass[]> {
    return this.assessmentApi.getClassAssessments(id);
  }

  public deleteCourseClassAssessment(id: number): Promise<any> {
    return this.assessmentApi.remove(id);
  }

  public createCourseClassAssessment(assessment: AssessmentClass): Promise<any> {
    return this.assessmentApi.create(assessment);
  }

  public updateCourseClassAssessment(assessment: AssessmentClass): Promise<any> {
    return this.assessmentApi.update(assessment.id, assessment);
  }

  public validateDelete(id: number): Promise<any> {
    return this.service.DELETE(`/v1/list/entity/courseClass/assessment/${id}`, {
      headers: { "X-validate-only": "true" }
    });
  }

  public validatePost(assessment: AssessmentClass): Promise<any> {
    return this.service.POST(`/v1/list/entity/courseClass/assessment`, assessment, {
      headers: { "X-validate-only": "true" }
    });
  }

  public validatePut(assessment: AssessmentClass): Promise<any> {
    return this.service.PUT(`/v1/list/entity/courseClass/assessment/${assessment.id}`, assessment, {
      headers: { "X-validate-only": "true" }
    });
  }
}

export default new CourseClassAssessmentService();
