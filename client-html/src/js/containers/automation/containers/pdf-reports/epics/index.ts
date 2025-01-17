import { combineEpics } from "redux-observable";
import { EpicCreatePdfReport } from "./EpicCreatePdfReport";
import { EpicGetFullScreenPreview } from "./EpicGetFullScreenPreview";
import { EpicGetPdfReport } from "./EpicGetPdfReport";
import { EpicGetPdfReportsList } from "./EpicGetPdfReportsList";
import { EpicRemovePdfReport } from "./EpicRemovePdfReport";
import { EpicUpdateInternalPdfReport } from "./EpicUpdateInternalPdfReport";
import { EpicUpdatePdfReport } from "./EpicUpdatePdfReport";

export const EpicPdfReports = combineEpics(
  EpicGetPdfReportsList,
  EpicGetPdfReport,
  EpicCreatePdfReport,
  EpicRemovePdfReport,
  EpicUpdatePdfReport,
  EpicUpdateInternalPdfReport,
  EpicGetFullScreenPreview
);
