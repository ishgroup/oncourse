import {BlockApi} from "../http/BlockApi";
import {Block} from "../model";
import {State} from "../reducers/state";

class BlockService {
  readonly blockApi = new BlockApi();

  public getBlocks(): Promise<Block[]> {
    return this.blockApi.getBlocks();
  }

  public saveBlock(props, state: State): Promise<Block[]> {
    return this.blockApi.saveBlock(this.buildSaveBlockRequest(props, state));
  }

  public addBlock(): Promise<Block> {
    return this.blockApi.addBlock();
  }

  public deleteBlock(id): Promise<Block[]> {
    return this.blockApi.deleteBlock(id);
  }

  public buildSaveBlockRequest(props, state: State) {
    const block = state.block.items.find(p => p.id === props.id);
    const request: Block = new Block();
    const newBlock = {...block, ...props};

    request.id = newBlock.id;
    request.html = newBlock.html;
    request.title = newBlock.title;

    return request;
  }

}

export default new BlockService();
