import { DefaultHttpService } from "../../../../../services/HttpService";
import {
  ExportTemplateApi,
  ExportTemplate,
  ExportApi,
  ExportRequest
} from "@api/model";

class ExportService {
  readonly service = new DefaultHttpService();

  readonly exportTemplateApi = new ExportTemplateApi(this.service);

  readonly exportApi = new ExportApi(this.service);

  public getAllTemplates(entityName: string): Promise<ExportTemplate[]> {
    return this.exportTemplateApi.templates(entityName);
  }

  public runExport(exportRequest: ExportRequest): Promise<string> {
    return this.exportApi.execute(exportRequest.entityName, exportRequest);
  }

  public getExportResult(entityName: string, processId: string): Promise<any> {
    return this.exportApi.get(entityName, processId);
  }

  public deletePreview(id: number): Promise<any> {
    return this.exportTemplateApi.deletePreview(id);
  }
}

export default new ExportService();
