import { Qualification, QualificationApi } from "@api/model";
import { DefaultHttpService } from "../../../../common/services/HttpService";

class QualificationService {
  readonly qualificationApi = new QualificationApi(new DefaultHttpService());

  public getQualification(id: number): Promise<any> {
    return this.qualificationApi.get(id);
  }

  public updateQualification(id: number, qualification: Qualification): Promise<any> {
    return this.qualificationApi.update(id, qualification);
  }

  public removeQualification(id: number): Promise<any> {
    return this.qualificationApi.remove(id);
  }

  public createQualification(qualification: Qualification): Promise<any> {
    return this.qualificationApi.create(qualification);
  }
}

export default new QualificationService();
