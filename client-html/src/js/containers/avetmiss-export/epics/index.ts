import { combineEpics } from "redux-observable";
import { EpicGetAvetmiss8ExportOutcomes } from "./EpicGetAvetmiss8ExportOutcomes";
import { EpicGetAvetmiss8ExportID } from "./EpicGetAvetmiss8ExportID";
import { EpicProcessAvetmiss8Export } from "./EpicProcessAvetmiss8Export";
import { EpicProcessAvetmiss8Outcomes } from "./EpicProcessAvetmiss8Outcomes";
import { EpicGetAvetmiss8ExportResults } from "./EpicGetAvetmiss8ExportResults";
import { EpicGetAvetmiss8ExportOutcomesProcessID } from "./EpicGetAvetmiss8ExportOutcomesProcessID";
import { EpicGetFundingUploads } from "./EpicGetFundingUploads";
import { EpicUpdateFundingUpload } from "./EpicUpdateFundingUpload";
import { EpicGetActiveFundingContracts } from "./EpicGetActiveFundingContracts";

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
