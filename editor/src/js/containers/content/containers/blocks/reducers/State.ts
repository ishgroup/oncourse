import {Block, ContentMode} from "../../../../../model";

export interface BlockState extends Block {
  id: number;
  renderHTML?: string;
  contentMode?: ContentMode;
}

export class BlocksState {
  items: BlockState[] = [];
}
