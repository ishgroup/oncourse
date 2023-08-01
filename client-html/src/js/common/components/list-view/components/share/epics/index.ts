import { combineEpics } from "redux-observable";
import { EpicAddPrintOverlay } from "./EpicAddPrintOverlay";
import { EpicDeleteExportTemplatePreview } from "./EpicDeleteExportTemplatePreview";
import { EpicDeletePDFReportPreview } from "./EpicDeletePDFReportPreview";
import { EpicGetExportResult } from "./EpicGetExportResult";
import { EpicGetExportTemplates } from "./EpicGetExportTemplates";
import { EpicGetOverlays } from "./EpicGetOverlays";
import { EpicGetPrintResult } from "./EpicGetPrintResult";
import { EpicGetShareList } from "./EpicGetShareList";
import { EpicStartExportProcess } from "./EpicStartExportProcess";
import { EpicStartPrintProcess } from "./EpicStartPrintProcess";

export const EpicShare = combineEpics(
  EpicGetOverlays,
  EpicGetShareList,
  EpicAddPrintOverlay,
  EpicGetPrintResult,
  EpicStartPrintProcess,
  EpicGetExportTemplates,
  EpicStartExportProcess,
  EpicGetExportResult,
  EpicDeletePDFReportPreview,
  EpicDeleteExportTemplatePreview
);
