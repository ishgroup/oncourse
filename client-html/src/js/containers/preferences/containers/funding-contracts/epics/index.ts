import { combineEpics } from "redux-observable";
import { EpicGetFundingContracts } from "./EpicGetFundingContracts";
import { EpicSaveFundingContracts } from "./EpicSaveFundingContracts";
import { EpicDeleteFundingContract } from "./EpicDeleteFundingContract";

export const EpicFundingContracts = combineEpics(
  EpicGetFundingContracts,
  EpicSaveFundingContracts,
  EpicDeleteFundingContract
);
