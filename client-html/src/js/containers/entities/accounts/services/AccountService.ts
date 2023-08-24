import { Account, AccountApi } from "@api/model";
import { DefaultHttpService } from "../../../../common/services/HttpService";

class AccountService {
  readonly accountApi = new AccountApi(new DefaultHttpService());

  public createAccount(account: Account): Promise<any> {
    return this.accountApi.create(account);
  }

  public getAccount(id: number): Promise<any> {
    return this.accountApi.get(id);
  }

  public updateAccount(id: number, account: Account): Promise<any> {
    return this.accountApi.update(id, account);
  }

  public removeAccount(id: number): Promise<any> {
    return this.accountApi.remove(id);
  }

  public getDepositAccounts(): Promise<any> {
    return this.accountApi.getDepositAccounts();
  }
}

export default new AccountService();
