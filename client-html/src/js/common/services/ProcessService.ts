import { ControlApi, ProcessResult } from "@api/model";
import { DefaultHttpService } from "./HttpService";

class ProcessService {
  readonly controlApi = new ControlApi(new DefaultHttpService());

  public getProcessStatus(processId: string): Promise<ProcessResult> {
    return this.controlApi.getStatus(processId);
  }

  public interruptProcess(processId: string): Promise<ProcessResult> {
    return this.controlApi.interrupt(processId);
  }
}

export default new ProcessService();
