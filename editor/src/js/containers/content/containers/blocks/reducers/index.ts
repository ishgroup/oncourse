import {IAction} from "../../../../../actions/IshAction";
import {BlocksState} from "./State";
import {
  ADD_BLOCK_FULFILLED,
  DELETE_BLOCK_FULFILLED,
  GET_BLOCKS_FULFILLED, SAVE_BLOCK_FULFILLED, SET_BLOCK_CONTENT_MODE,
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

    case SET_BLOCK_CONTENT_MODE: {
      const {id, contentMode} = action.payload;

      return {
        ...state,
        items: state.items.map(b =>
        b.id === id ? {...b, contentMode} : b),
      };
    }

    case DELETE_BLOCK_FULFILLED: {
      const id = action.payload;
      const newBlocks = state.items.filter(block => block.id !== id);

      return {
        ...state,
        items: newBlocks,
      };
    }

    default:
      return state;
  }
};
