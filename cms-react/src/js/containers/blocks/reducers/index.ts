import {IAction} from "../../../actions/IshAction";
import {BlocksState} from "./State";
import {
  DELETE_BLOCK_FULFILLED,
  GET_BLOCKS_FULFILLED, SAVE_BLOCK_FULFILLED,
} from "../actions";

export const blockReducer = (state: BlocksState = new BlocksState(), action: IAction<any>): BlocksState => {
  switch (action.type) {

    case GET_BLOCKS_FULFILLED:
      return {
        ...state,
        blocks: action.payload,
      };

    case SAVE_BLOCK_FULFILLED: {
      const {id, ...props} = action.payload;

      return {
        ...state,
        blocks: state.blocks.map(block => block.id === id ? {...block, ...props} : block),
      };
    }

    case DELETE_BLOCK_FULFILLED: {
      const id = action.payload;
      const index = state.blocks.findIndex(block => block.id === id);
      const newBlocks = state.blocks;

      if (index !== -1) {
        newBlocks.splice(index, 1);
      }

      return {
        ...state,
        blocks: newBlocks,
      };
    }

    default:
      return state;
  }
};
