import {BlockApi} from "../../../build/generated-sources";
import {Block} from "../model";
import {State} from "../reducers/state";
import {DefaultHttpService} from "../common/services/HttpService";

class BlockService {
  readonly blockApi = new BlockApi(new DefaultHttpService());

  public getBlocks(): Promise<Block[]> {
    return this.blockApi.getBlocks();
  }

  public getBlock(id: string): Promise<Block> {
    return this.blockApi.getBlockById(id);
  }

  public saveBlock(props, state: State): Promise<Block> {
    return this.blockApi.updateBlock(this.buildSaveBlockRequest(props, state));
  }

  public addBlock(): Promise<Block> {
    return this.blockApi.createBlock();
  }

  public deleteBlock(id): Promise<Block[]> {
    return this.blockApi.deleteBlock(id);
  }

  public buildSaveBlockRequest(props, state: State) {
    const block = state.block.items.find(p => p.id === props.id);
    const request: Block = {} as Block;
    const newBlock: Block = {...block, ...props};

    request.content = newBlock.content;
    request.title = newBlock.title;
    request.id = newBlock.id;

    return request;
  }

}

export default new BlockService();
