import {IAction} from "../../../../../actions/IshAction";
import {BlocksState} from "./State";
import {
  ADD_BLOCK_FULFILLED,
  ADD_BLOCK_REQUEST,
  DELETE_BLOCK_FULFILLED, DELETE_BLOCK_REQUEST,
  GET_BLOCKS_FULFILLED, GET_BLOCKS_REQUEST, SAVE_BLOCK_FULFILLED, SAVE_BLOCK_REQUEST,
} from "../actions";
import {UNHANDLED_ERROR} from "../../../../../common/actions";

export const blockReducer = (state: BlocksState = new BlocksState(), action: IAction<any>): BlocksState => {
  switch (action.type) {

    case GET_BLOCKS_REQUEST:
    case ADD_BLOCK_REQUEST:
    case DELETE_BLOCK_REQUEST:
    case SAVE_BLOCK_REQUEST: {
      return {
        ...state,
        fetching: true,
      };
    }

    case UNHANDLED_ERROR:
      return {
        ...state,
        fetching: false,
      };

    case GET_BLOCKS_FULFILLED:
      return {
        ...state,
        fetching: false,
        items: action.payload,
      };

    case SAVE_BLOCK_FULFILLED: {
      const {id, ...props} = action.payload;

      const ns = {
        ...state,
        fetching: false,
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
        fetching: false,
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
        fetching: false,
        items: newBlocks,
      };
    }

    default:
      return state;
  }
};
