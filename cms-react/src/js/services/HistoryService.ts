import {History} from "../model";
import {HistoryApi} from "../http/HistoryApi";

class HistoryService {
  readonly historyApi = new HistoryApi();

  public getVersions(): Promise<History> {
    return this.historyApi.getVersions();
  }
}

export default new HistoryService();
