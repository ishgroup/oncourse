import { PayrollApi, PayrollRequest, WagesToProcess } from "@api/model";
import { DefaultHttpService } from "../../../../common/services/HttpService";

class PayrollService {
  readonly payrollApi = new PayrollApi(new DefaultHttpService());

  public execute(entity: string, confirm: boolean, payrollRequest: PayrollRequest): Promise<string> {
    return this.payrollApi.execute(entity, confirm, payrollRequest);
  }

  public prepare(entity: string, payrollRequest: PayrollRequest): Promise<WagesToProcess> {
    return this.payrollApi.prepare(entity, payrollRequest);
  }
}

export default new PayrollService();
