import { DefaultHttpService } from "../../../common/services/HttpService";
import { AuditApi } from "@api/model";

class AuditsService {
  readonly auditApi = new AuditApi(new DefaultHttpService());

  public getAuditItem(id: number): Promise<any> {
    return this.auditApi.get(id);
  }
}

export default new AuditsService();
