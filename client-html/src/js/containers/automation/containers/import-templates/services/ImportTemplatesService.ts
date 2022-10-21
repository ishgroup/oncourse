import { ImportApi, ImportModel } from "@api/model";
import { DefaultHttpService } from "../../../../../common/services/HttpService";

class ImportTemplatesService {
  readonly service = new DefaultHttpService();

  readonly importTemplatesApi = new ImportApi(this.service);

  public get(id: number): Promise<ImportModel> {
    return this.importTemplatesApi.get(id);
  }

  public update(id: number, importTemplate: ImportModel): Promise<any> {
    return this.importTemplatesApi.update(id, importTemplate);
  }

  public updateInternal(importTemplate: ImportModel): Promise<any> {
    return this.importTemplatesApi.updateInternal(importTemplate);
  }

  public create(importTemplate: ImportModel): Promise<any> {
    return this.importTemplatesApi.create(importTemplate);
  }

  public remove(id: number): Promise<any> {
    return this.importTemplatesApi.remove(id);
  }

  public execute(request, files): Promise<any> {
    const headers = { 'Content-Type': "multipart/form-data; charset=utf-8; boundary=" + Math.random().toString().substr(2) };
    const body = new FormData();

    body.append("request", JSON.stringify(request));

    const filesKey = "files";
    files.forEach(f => {
      body.append(filesKey, f, f.name);
    });

    return this.service.POST(`/v1/list/entity/import/execution`, body, {headers});
  }

  public getConfigs(id: number): Promise<string> {
    return this.importTemplatesApi.getConfigs(id);
  }

  public updateConfigs(id: number, config: string): Promise<any> {
    return this.importTemplatesApi.updateConfigs(id, config);
  }
}

export default new ImportTemplatesService();
