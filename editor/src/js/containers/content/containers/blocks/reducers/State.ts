import {Block} from "../../../../../model";

export class BlockState extends Block {
  id: number;
}

export class BlocksState {
  items: BlockState[] = [];
}
