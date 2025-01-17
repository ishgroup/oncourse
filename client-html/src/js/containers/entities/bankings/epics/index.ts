import { combineEpics } from "redux-observable";
import { EpicGetDepositAccounts } from "./EpicGetDepositAccounts";
import { EpicGetDepositPayments } from "./EpicGetDepositPayments";
import { EpicInitDeposit } from "./EpicInitDeposit";
import { EpicReconcileBanking } from "./EpicReconcileBanking";

export const EpicBankings = combineEpics(
  EpicReconcileBanking,
  EpicGetDepositPayments,
  EpicGetDepositAccounts,
  EpicInitDeposit
);