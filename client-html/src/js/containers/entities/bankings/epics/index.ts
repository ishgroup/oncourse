import { combineEpics } from "redux-observable";
import { EpicGetBanking } from "./EpicGetBanking";
import { EpicDeleteBanking } from "./EpicDeleteBanking";
import { EpicReconcileBanking } from "./EpicReconcileBanking";
import { EpicGetDepositPayments } from "./EpicGetDepositPayments";
import { EpicGetDepositAccounts } from "./EpicGetDepositAccounts";
import { EpicInitDeposit } from "./EpicInitDeposit";
import { EpicUpdateBanking } from "./EpicUpdateBanking";
import { EpicCreateBanking } from "./EpicCreateBanking";

export const EpicBankings = combineEpics(
  EpicGetBanking,
  EpicDeleteBanking,
  EpicReconcileBanking,
  EpicGetDepositPayments,
  EpicGetDepositAccounts,
  EpicInitDeposit,
  EpicUpdateBanking,
  EpicCreateBanking
);
