/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { AttendanceApi, CourseClassApi, StudentAttendance, TrainingPlan } from "@api/model";
import { DefaultHttpService } from "../../../../../../common/services/HttpService";

class CourseClassAttendanceService {
  readonly service = new DefaultHttpService();

  readonly courseClassApi = new CourseClassApi(this.service);

  readonly attendanceApi = new AttendanceApi(this.service);

  public getStudentAttendance(id: number): Promise<StudentAttendance[]> {
    return this.attendanceApi.get(id);
  }

  public updateStudentAttendance(id: number, studentAttendance: StudentAttendance[]): Promise<any> {
    return this.attendanceApi.update(id, studentAttendance);
  }

  public validateUpdateStudentAttendance(id: number, studentAttendance: StudentAttendance[]): Promise<any> {
    return this.service.POST(`/v1/list/entity/courseClass/attendance/student/${id}`, studentAttendance, {
      headers: { "X-validate-only": "true" }
    });
  }

  public getTrainingPlans(id: number): Promise<TrainingPlan[]> {
    return this.courseClassApi.getTrainingPlan(id);
  }

  public updateTrainingPlans(id: number, trainingPlans: TrainingPlan[]): Promise<any> {
    return this.courseClassApi.updateTrainingPlan(id, trainingPlans);
  }

  public validateUpdateTrainingPlans(id: number, trainingPlans: TrainingPlan[]): Promise<any> {
    return this.service.POST(`/v1/list/entity/courseClass/trainingPlan/${id}`, trainingPlans, {
      headers: { "X-validate-only": "true" }
    });
  }
}

export default new CourseClassAttendanceService();
