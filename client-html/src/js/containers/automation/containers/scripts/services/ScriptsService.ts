/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { AutomationConfigs, ExecuteScriptRequest, Script, ScriptApi } from "@api/model";
import { AxiosInstance } from "axios";
import { defaultAxios } from "../../../../../common/services/DefaultHttpClient";
import { DefaultHttpService } from "../../../../../common/services/HttpService";

class ScriptsService {
  readonly service = new DefaultHttpService();

  readonly client: AxiosInstance = defaultAxios;

  readonly scriptApi = new ScriptApi(this.service);

  public getScriptItem(id: number): Promise<Script> {
    return this.scriptApi.get(id);
  }

  public saveScriptItem(id: number, script: Script): Promise<any> {
    return this.scriptApi.update(id, script);
  }

  public createScriptItem(script: Script): Promise<any> {
    return this.scriptApi.create(script);
  }

  public patchScriptItem(id: number, script: Script): Promise<any> {
    return this.scriptApi.patch(id, script);
  }

  public deleteScriptItem(id: number): Promise<any> {
    return this.scriptApi._delete(id);
  }

  public runScript(executeScriptRequest: ExecuteScriptRequest): Promise<string> {
    return this.scriptApi.execute({
      ...executeScriptRequest
    } as ExecuteScriptRequest);
  }

  public getRunScriptResultResponse(processId: string): Promise<string> {
    return this.client.get(`/v1/list/entity/script/execute/${processId}`, { headers: {}, params: {} });
  }

  public getRunScriptResult(processId: string): Promise<string> {
    return this.scriptApi.getResult(processId);
  }

  public getScriptUpdate(name: string): Promise<any> {
    const path = `https://raw.githubusercontent.com/ari/oncourse-scripts/master/scripts/${name
      .trim()
      .replace(/\s/g, "-")}.groovy`;

    return this.service.GET(path);
  }

  public getConfigs(id: number): Promise<string> {
    return this.scriptApi.getConfigs(id);
  }

  public updateConfigs(id: number, scriptConfigs: AutomationConfigs): Promise<any> {
    return this.scriptApi.updateConfigs(id, scriptConfigs);
  }
}

export default new ScriptsService();
