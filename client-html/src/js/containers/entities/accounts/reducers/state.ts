import { Account } from "@api/model";

export interface AccountsState {
  items: Account[];
  incomeItems: Account[];
  updatingIncomeItems: boolean;
  liabilityItems: Account[];
  updatingLiabilityItems: boolean;
}
