import {
  AvetmissExportApi,
  AvetmissExportOutcome,
  AvetmissExportRequest,
  AvetmissExportSettings,
  ControlApi
} from "@api/model";
import { DefaultHttpService } from "../../../common/services/HttpService";

class AvetmissExportService {
  readonly service = new DefaultHttpService();
  readonly avetmissExportApi = new AvetmissExportApi(this.service);
  readonly avetmissControlApi = new ControlApi(this.service);

  public getExportOutcomesProcessID(settings: AvetmissExportSettings): Promise<string> {
    return this.avetmissExportApi.findExportOutcomes(settings);
  }

  public getExportOutcomes(processId: string): Promise<AvetmissExportOutcome[]> {
    return this.avetmissExportApi.getExportOutcomes(processId);
  }

  public exportAvetmiss8(requestParameters: AvetmissExportRequest): Promise<string> {
    return this.avetmissExportApi.exportAvetmiss8(requestParameters);
  }

  public getExport(processId: string): Promise<any> {
    return this.avetmissExportApi.getExport(processId);
  }

  public getExportStatus(exportID: string): Promise<any> {
    return this.avetmissControlApi.getStatus(exportID);
  }
}

export default new AvetmissExportService();
