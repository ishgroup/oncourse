import {IAction} from "../../../../../actions/IshAction";
import {BlocksState} from "./State";
import {
  ADD_BLOCK_FULFILLED,
  DELETE_BLOCK_FULFILLED,
  GET_BLOCKS_FULFILLED, SAVE_BLOCK_FULFILLED,
} from "../actions";

export const blockReducer = (state: BlocksState = new BlocksState(), action: IAction<any>): BlocksState => {
  switch (action.type) {

    case GET_BLOCKS_FULFILLED:
      return {
        ...state,
        items: action.payload,
      };

    case SAVE_BLOCK_FULFILLED: {
      const {id, ...props} = action.payload;

      const ns = {
        ...state,
        items: state.items.map(block => block.id === id ? {...block, ...props} : block),
      };

      return ns;
    }

    case ADD_BLOCK_FULFILLED: {
      const block = action.payload;

      return {
        ...state,
        items: state.items.concat(block),
      };
    }

    case DELETE_BLOCK_FULFILLED: {
      const title = action.payload;
      const newBlocks = state.items.filter(block => block.title !== title);

      return {
        ...state,
        items: newBlocks,
      };
    }

    default:
      return state;
  }
};
