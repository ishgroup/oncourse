import {History} from "../model";
import {HistoryApi} from "../http/HistoryApi";

class HistoryService {
  readonly historyApi = new HistoryApi();

  public getHistory(): Promise<History> {
    return this.historyApi.getHistory();
  }
}

export default new HistoryService();
