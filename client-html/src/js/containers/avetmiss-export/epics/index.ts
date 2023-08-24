import { combineEpics } from "redux-observable";
import { EpicGetActiveFundingContracts } from "./EpicGetActiveFundingContracts";
import { EpicGetAvetmiss8ExportID } from "./EpicGetAvetmiss8ExportID";
import { EpicGetAvetmiss8ExportOutcomes } from "./EpicGetAvetmiss8ExportOutcomes";
import { EpicGetAvetmiss8ExportOutcomesProcessID } from "./EpicGetAvetmiss8ExportOutcomesProcessID";
import { EpicGetAvetmiss8ExportResults } from "./EpicGetAvetmiss8ExportResults";
import { EpicGetFundingUploads } from "./EpicGetFundingUploads";
import { EpicProcessAvetmiss8Export } from "./EpicProcessAvetmiss8Export";
import { EpicProcessAvetmiss8Outcomes } from "./EpicProcessAvetmiss8Outcomes";
import { EpicUpdateFundingUpload } from "./EpicUpdateFundingUpload";

export const EpicAvetmissExport = combineEpics(
  EpicGetAvetmiss8ExportOutcomes,
  EpicGetAvetmiss8ExportID,
  EpicProcessAvetmiss8Export,
  EpicGetAvetmiss8ExportResults,
  EpicGetAvetmiss8ExportOutcomesProcessID,
  EpicProcessAvetmiss8Outcomes,
  EpicGetFundingUploads,
  EpicUpdateFundingUpload,
  EpicGetActiveFundingContracts
);
