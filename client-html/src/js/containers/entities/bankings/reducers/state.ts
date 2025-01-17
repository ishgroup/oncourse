import { Account, Payment } from "@api/model";

export interface BankingState {
  payments?: Payment[];
  accounts?: Account[];
  sites?: { id: number; name: string }[];
  selectedAccountId: number;
  printReport?: boolean;
}
