/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ClassCost, ClassCostApi } from "@api/model";
import { DefaultHttpService } from "../../../../../../common/services/HttpService";

class CourseClassCostService {
  readonly service = new DefaultHttpService();

  readonly courseClassCostApi = new ClassCostApi(this.service);

  public getCourseClassCosts(classId: number): Promise<ClassCost[]> {
    return this.courseClassCostApi.get(classId);
  }

  public deleteCourseClassCost(id: number): Promise<any> {
    return this.courseClassCostApi._delete(id);
  }

  public postCourseClassCost(cost: ClassCost): Promise<any> {
    return this.courseClassCostApi.create(cost);
  }

  public putCourseClassCost(cost: ClassCost): Promise<any> {
    return this.courseClassCostApi.update(cost.id, cost);
  }

  public validateDelete(id: number): Promise<any> {
    return this.service.DELETE(`/v1/list/entity/courseClass/budget/${id}`, {
      headers: { "X-validate-only": "true" }
    });
  }

  public validatePost(classCost: ClassCost): Promise<any> {
    return this.service.POST(`/v1/list/entity/courseClass/budget`, classCost, {
      headers: { "X-validate-only": "true" }
    });
  }

  public validatePut(classCost: ClassCost): Promise<any> {
    return this.service.PUT(`/v1/list/entity/courseClass/budget/${classCost.id}`, classCost, {
      headers: { "X-validate-only": "true" }
    });
  }
}

export default new CourseClassCostService();
