import { AuditApi } from "@api/model";
import { DefaultHttpService } from "../../../common/services/HttpService";

class AuditsService {
  readonly auditApi = new AuditApi(new DefaultHttpService());

  public getAuditItem(id: number): Promise<any> {
    return this.auditApi.get(id);
  }
}

export default new AuditsService();
