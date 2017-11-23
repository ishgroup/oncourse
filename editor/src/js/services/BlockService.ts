import {BlockApi} from "../http/BlockApi";
import {Block} from "../model";
import {State} from "../reducers/state";
import {DefaultHttpService} from "../common/services/HttpService";

class BlockService {
  readonly blockApi = new BlockApi(new DefaultHttpService());

  public getBlocks(): Promise<Block[]> {
    return this.blockApi.getBlocks();
  }

  public saveBlock(props, state: State): Promise<Block> {
    return this.blockApi.saveBlock(this.buildSaveBlockRequest(props, state));
  }

  public addBlock(): Promise<Block> {
    return this.blockApi.addBlock();
  }

  public deleteBlock(title): Promise<Block[]> {
    return this.blockApi.deleteBlock(title);
  }

  public buildSaveBlockRequest(props, state: State) {
    const block = state.block.items.find(p => p.id === props.id);
    const request: Block = new Block();
    const newBlock: Block = {...block, ...props};

    request.content = newBlock.content;
    request.title = newBlock.title;

    return request;
  }

}

export default new BlockService();
