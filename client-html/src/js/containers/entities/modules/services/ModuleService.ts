import { Module, ModuleApi } from "@api/model";
import { DefaultHttpService } from "../../../../common/services/HttpService";

class ModuleService {
  readonly moduleApi = new ModuleApi(new DefaultHttpService());

  public getModule(id: number): Promise<any> {
    return this.moduleApi.get(id);
  }

  public updateModule(id: number, module: Module): Promise<any> {
    return this.moduleApi.update(id, module);
  }

  public createModule(module: Module): Promise<any> {
    return this.moduleApi.create(module);
  }

  public removeModule(id: number): Promise<any> {
    return this.moduleApi.remove(id);
  }
}

export default new ModuleService();
