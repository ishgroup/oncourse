import {Block} from "../../../../../model";

export interface BlockState extends Block {
  id: number;
}

export class BlocksState {
  items: BlockState[] = [];
}
