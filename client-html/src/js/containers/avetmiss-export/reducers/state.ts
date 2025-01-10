import { AvetmissExportOutcome, AvetmissExportSettings, FundingSource, FundingUpload } from "@api/model";

export interface AvetmissExportState {
  contracts?: FundingSource[];
  uploads?: FundingUpload[];
  outcomes?: AvetmissExportOutcome[];
  settings?: AvetmissExportSettings;
  exportID?: string;
  outcomesID?: string;
}
