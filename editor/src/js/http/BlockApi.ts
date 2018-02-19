import {HttpService} from "../common/services/HttpService";
import {Block} from "../model/Block";
import {CommonError} from "../model/common/CommonError";

export class BlockApi {
  constructor(private http: HttpService) {
  }

  addBlock(): Promise<Block> {
    return this.http.POST(`/block.create`);
  }
  deleteBlock(id: string): Promise<any> {
    return this.http.POST(`/block.delete/${id}`);
  }
  getBlocks(): Promise<Block[]> {
    return this.http.GET(`/block.list`);
  }
  saveBlock(saveBlockRequest: Block): Promise<Block> {
    return this.http.POST(`/block.update`, saveBlockRequest);
  }
}
