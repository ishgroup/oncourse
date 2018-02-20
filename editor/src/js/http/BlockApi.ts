import {HttpService} from "../common/services/HttpService";
import {Block} from "../model/Block";
import {CommonError} from "../model/common/CommonError";

export class BlockApi {
  constructor(private http: HttpService) {
  }

  blockCreatePost(): Promise<Block> {
    return this.http.POST(`/block.create`);
  }
  blockDeleteIdPost(id: string): Promise<any> {
    return this.http.POST(`/block.delete/${id}`);
  }
  blockListGet(): Promise<Block[]> {
    return this.http.GET(`/block.list`);
  }
  blockUpdatePost(saveBlockRequest: Block): Promise<Block> {
    return this.http.POST(`/block.update`, saveBlockRequest);
  }
}
