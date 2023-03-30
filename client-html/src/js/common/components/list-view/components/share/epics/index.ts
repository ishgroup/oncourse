import { combineEpics } from "redux-observable";
import { EpicGetOverlays } from "./EpicGetOverlays";
import { EpicGetShareList } from "./EpicGetShareList";
import { EpicAddPrintOverlay } from "./EpicAddPrintOverlay";
import { EpicGetPrintResult } from "./EpicGetPrintResult";
import { EpicStartPrintProcess } from "./EpicStartPrintProcess";
import { EpicGetExportTemplates } from "./EpicGetExportTemplates";
import { EpicStartExportProcess } from "./EpicStartExportProcess";
import { EpicGetExportResult } from "./EpicGetExportResult";
import { EpicDeleteExportTemplatePreview } from "./EpicDeleteExportTemplatePreview";
import { EpicDeletePDFReportPreview } from "./EpicDeletePDFReportPreview";

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
