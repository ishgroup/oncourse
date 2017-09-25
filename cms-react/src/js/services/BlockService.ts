import {BlockApi} from "../http/BlockApi";
import {Block} from "../model";

class BlockService {
  readonly blockApi = new BlockApi();

  public getBlocks(): Promise<Block[]> {
    return this.blockApi.getBlocks();
  }

  public saveBlock(props, state): Promise<Block[]> {
    return this.blockApi.saveBlock(this.buildSaveBlockRequest(props, state));
  }

  public deleteBlock(id): Promise<Block[]> {
    return this.blockApi.deleteBlock(id);
  }

  public buildSaveBlockRequest(props, state) {
    const block = state.block.blocks.find(p => p.id === props.id);
    return {
      ...block,
      ...props,
    };
  }

}

export default new BlockService();
