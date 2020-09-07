import {Block} from "../../../../../model";

export interface BlockState extends Block {
  id: number;
  contentMode?: string;
}

export class BlocksState {
  items: BlockState[] = [];
}
