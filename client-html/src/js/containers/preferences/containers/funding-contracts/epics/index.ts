import { combineEpics } from "redux-observable";
import { EpicDeleteFundingContract } from "./EpicDeleteFundingContract";
import { EpicGetFundingContracts } from "./EpicGetFundingContracts";
import { EpicSaveFundingContracts } from "./EpicSaveFundingContracts";

export const EpicFundingContracts = combineEpics(
  EpicGetFundingContracts,
  EpicSaveFundingContracts,
  EpicDeleteFundingContract
);
