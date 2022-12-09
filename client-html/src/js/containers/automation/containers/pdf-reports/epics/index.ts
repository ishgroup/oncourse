import { combineEpics } from "redux-observable";
import { EpicGetPdfReportsList } from "./EpicGetPdfReportsList";
import { EpicGetPdfReport } from "./EpicGetPdfReport";
import { EpicCreatePdfReport } from "./EpicCreatePdfReport";
import { EpicRemovePdfReport } from "./EpicRemovePdfReport";
import { EpicUpdatePdfReport } from "./EpicUpdatePdfReport";
import { EpicUpdateInternalPdfReport } from "./EpicUpdateInternalPdfReport";
import { EpicGetFullScreenPreview } from "./EpicGetFullScreenPreview";

export const EpicPdfReports = combineEpics(
  EpicGetPdfReportsList,
  EpicGetPdfReport,
  EpicCreatePdfReport,
  EpicRemovePdfReport,
  EpicUpdatePdfReport,
  EpicUpdateInternalPdfReport,
  EpicGetFullScreenPreview
);
