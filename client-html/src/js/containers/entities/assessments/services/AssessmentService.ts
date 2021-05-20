import { Assessment, AssessmentApi, Diff } from "@api/model";
import { DefaultHttpService } from "../../../../common/services/HttpService";

class AssessmentService {
  readonly assessmentApi = new AssessmentApi(new DefaultHttpService());

  public bulkChange(diff: Diff): Promise<any> {
    return this.assessmentApi.bulkChange(diff);
  }

  public getAssessment(id: number): Promise<any> {
    return this.assessmentApi.get(id);
  }

  public updateAssessment(id: number, assessment: Assessment): Promise<any> {
    return this.assessmentApi.update(id, assessment);
  }

  public createAssessment(assessment: Assessment): Promise<any> {
    return this.assessmentApi.create(assessment);
  }

  public removeAssessment(id: number): Promise<any> {
    return this.assessmentApi.remove(id);
  }
}

export default new AssessmentService();
