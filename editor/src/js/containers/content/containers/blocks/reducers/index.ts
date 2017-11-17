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

      if (!state.items.find(block => block.id === id)) {
        ns.items.push({id, ...props});
      }

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
      const id = action.payload;
      const index = state.items.findIndex(block => block.id === id);
      const newBlocks = state.items;

      if (index !== -1) {
        newBlocks.splice(index, 1);
      }

      return {
        ...state,
        items: newBlocks,
      };
    }

    default:
      return state;
  }
};
