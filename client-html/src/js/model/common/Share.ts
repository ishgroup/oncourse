import { ExportTemplate, Report, ReportOverlay } from "@api/model";

export interface ShareState {
  exportTemplates: ExportTemplate[];
  exportTemplatesFetching: boolean;
  pdfReports: Report[];
  pdfReportsFetching: boolean;
  overlays: ReportOverlay[];
  validating?: boolean;
}

export type TemplateOutputDisplayName = "Excel" | "JSON" | "XML" | "Calendar" | "Text" | "PDF";
