import { DefaultHttpService } from "../../../../common/services/HttpService";
import { Transaction, TransactionApi } from "@api/model";
import { format } from "date-fns";
import { YYYY_MM_DD_MINUSED } from  "ish-ui";

class TransactionService {
  readonly transactionApi = new TransactionApi(new DefaultHttpService());

  public getTransaction(id: number): Promise<Transaction> {
    return this.transactionApi.get(id);
  }

  public createTransaction(transaction: Transaction): Promise<any> {
    transaction.transactionDate = format(new Date(transaction.transactionDate), YYYY_MM_DD_MINUSED);
    return this.transactionApi.create(transaction);
  }
}

export default new TransactionService();
