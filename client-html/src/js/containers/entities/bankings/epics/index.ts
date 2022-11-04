import { combineEpics } from "redux-observable";
import { EpicReconcileBanking } from "./EpicReconcileBanking";
import { EpicGetDepositPayments } from "./EpicGetDepositPayments";
import { EpicGetDepositAccounts } from "./EpicGetDepositAccounts";
import { EpicInitDeposit } from "./EpicInitDeposit";

export const EpicBankings = combineEpics(
  EpicReconcileBanking,
  EpicGetDepositPayments,
  EpicGetDepositAccounts,
  EpicInitDeposit
);