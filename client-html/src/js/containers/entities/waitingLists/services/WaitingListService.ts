import { WaitingList, WaitingListApi } from "@api/model";
import { DefaultHttpService } from "../../../../common/services/HttpService";

class WaitingListService {
  readonly waitingListApi = new WaitingListApi(new DefaultHttpService());

  public createWaitingList(waitingList: WaitingList): Promise<any> {
    return this.waitingListApi.create(waitingList);
  }

  public getWaitingList(id: number): Promise<any> {
    return this.waitingListApi.get(id);
  }

  public updateWaitingList(id: number, waitingList: WaitingList): Promise<any> {
    return this.waitingListApi.update(id, waitingList);
  }
}

export default new WaitingListService();