import {HttpService} from "../common/services/HttpService";
import {Block} from "../model/Block";
import {CommonError} from "../model/common/CommonError";

export class BlockApi {
  constructor(private http: HttpService) {
  }

  addBlock(): Promise<Block> {
    return this.http.POST(`/addBlock`);
  }
  deleteBlock(id: string): Promise<any> {
    return this.http.POST(`/deleteBlock/${id}`);
  }
  getBlocks(): Promise<Block[]> {
    return this.http.GET(`/getBlocks`);
  }
  saveBlock(saveBlockRequest: Block): Promise<Block> {
    return this.http.POST(`/saveBlock`, saveBlockRequest);
  }
}
