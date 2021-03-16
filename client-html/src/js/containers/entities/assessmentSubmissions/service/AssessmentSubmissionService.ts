/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import {AssessmentSubmission, AssessmentSubmissionApi, Diff} from "@api/model";
import { DefaultHttpService } from "../../../../common/services/HttpService";

class AssessmentSubmissionService {
  readonly assessmentSubmissionApi = new AssessmentSubmissionApi(new DefaultHttpService());

  public getAssessmentSubmission(id: number): Promise<any> {
    return this.assessmentSubmissionApi.get(id);
  }

  public updateAssessmentSubmission(id: number, assessmentSubmission: AssessmentSubmission): Promise<any> {
    return this.assessmentSubmissionApi.update(id, assessmentSubmission);
  }

  public removeAssessmentSubmission(id: number): Promise<any> {
    return this.assessmentSubmissionApi.remove(id);
  }

  public bulkChange(diff: Diff): Promise<any> {
    return this.assessmentSubmissionApi.bulkChange(diff);
  }
}

export default new AssessmentSubmissionService();