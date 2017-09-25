import {DefaultHttpService} from "../common/services/HttpService";
import {API} from "../constants/Config";

export class BlockApi {
  private http = new DefaultHttpService();

  getBlocks(): Promise<any> {
    return this.http.GET(API.GET_BLOCKS);
  }

  saveBlock(payload): Promise<any> {
    return this.http.POST(API.SAVE_BLOCK, payload);
  }

  deleteBlock(id): Promise<any> {
    return this.http.POST(API.DELETE_BLOCK, id);
  }
}
