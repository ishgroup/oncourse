import {
 Diff, Payslip, PayslipApi, PayslipStatus 
} from "@api/model";
import { DefaultHttpService } from "../../../../common/services/HttpService";

class PayslipService {
  readonly payslipApi = new PayslipApi(new DefaultHttpService());

  public bulkChange(diff: Diff): Promise<any> {
    return this.payslipApi.bulkChange(diff);
  }

  public getPayslip(id: number): Promise<any> {
    return this.payslipApi.get(id);
  }

  public updatePayslip(id: number, payslip: Payslip): Promise<any> {
    return this.payslipApi.update(id, payslip);
  }

  public createPayslip(payslip: Payslip): Promise<any> {
    return this.payslipApi.create(payslip);
  }

  public removePayslip(id: number): Promise<any> {
    return this.payslipApi.remove(id);
  }

  public executePayslip(ids: number[], status: PayslipStatus): Promise<any> {
    return this.payslipApi.execute({ ids, status });
  }
}

export default new PayslipService();
