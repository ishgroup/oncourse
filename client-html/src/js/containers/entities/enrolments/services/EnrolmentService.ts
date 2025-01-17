import { CancelEnrolment, Enrolment, EnrolmentApi } from "@api/model";
import { DefaultHttpService } from "../../../../common/services/HttpService";

class EnrolmentService {
  readonly enrolmentApi = new EnrolmentApi(new DefaultHttpService());

  public getEnrolment(id: number): Promise<any> {
    return this.enrolmentApi.get(id);
  }

  public updateEnrolment(id: number, enrolment: Enrolment): Promise<any> {
    return this.enrolmentApi.update(id, enrolment);
  }

  public cancelEnrolment(cancelEnrolment: CancelEnrolment): Promise<any> {
    return this.enrolmentApi.cancel(cancelEnrolment);
  }
}

export default new EnrolmentService();
