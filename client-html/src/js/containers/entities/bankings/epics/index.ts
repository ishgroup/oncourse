import { combineEpics } from "redux-observable";
import { EpicGetBanking } from "./EpicGetBanking";
import { EpicReconcileBanking } from "./EpicReconcileBanking";
import { EpicGetDepositPayments } from "./EpicGetDepositPayments";
import { EpicGetDepositAccounts } from "./EpicGetDepositAccounts";
import { EpicInitDeposit } from "./EpicInitDeposit";
import { EpicUpdateBanking } from "./EpicUpdateBanking";

export const EpicBankings = combineEpics(
  EpicGetBanking,
  EpicReconcileBanking,
  EpicGetDepositPayments,
  EpicGetDepositAccounts,
  EpicInitDeposit,
  EpicUpdateBanking
);