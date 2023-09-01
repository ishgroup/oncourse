import { Banking, BankingApi } from "@api/model";
import { DefaultHttpService } from "../../../../common/services/HttpService";

class BankingService {
  readonly bankingApi = new BankingApi(new DefaultHttpService());

  public createBanking(banking: Banking): Promise<any> {
    return this.bankingApi.create(banking);
  }

  public getBanking(id: number): Promise<any> {
    return this.bankingApi.get(id);
  }

  public updateBanking(id: number, banking: Banking): Promise<any> {
    return this.bankingApi.update(id, banking);
  }

  public removeBanking(id: number): Promise<any> {
    return this.bankingApi.remove(id);
  }

  public reconcileBankings(ids: number[]): Promise<any> {
    return this.bankingApi.reconcile(ids);
  }

  public getDepositPayments(accountId: number, siteId: number): Promise<any> {
    return this.bankingApi.getDepositPayments(accountId, siteId);
  }
}

export default new BankingService();
