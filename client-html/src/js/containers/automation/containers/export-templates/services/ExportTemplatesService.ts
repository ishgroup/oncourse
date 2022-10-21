import { DefaultHttpService } from "../../../../../common/services/HttpService";
import { ExportTemplateApi, ExportTemplate } from "@api/model";

class ExportTemplatesService {
  readonly exportTemplatesApi = new ExportTemplateApi(new DefaultHttpService());

  public get(id: number): Promise<ExportTemplate> {
    return this.exportTemplatesApi.get(id);
  }

  public update(id: number, exportTemplate: ExportTemplate): Promise<any> {
    return this.exportTemplatesApi.update(id, exportTemplate);
  }

  public updateInternal(exportTemplate: ExportTemplate): Promise<any> {
    return this.exportTemplatesApi.updateInternal(exportTemplate);
  }

  public create(exportTemplate: ExportTemplate): Promise<any> {
    return this.exportTemplatesApi.create(exportTemplate);
  }

  public remove(id: number): Promise<any> {
    return this.exportTemplatesApi.remove(id);
  }

  public getConfigs(id: number): Promise<string> {
    return this.exportTemplatesApi.getConfigs(id);
  }

  public updateConfigs(id: number, config: string): Promise<any> {
    return this.exportTemplatesApi.updateConfigs(id, config);
  }
}

export default new ExportTemplatesService();
