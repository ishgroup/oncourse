import { IAction } from "../../../common/actions/IshAction";
import {
  GET_ALL_TAGS_FULFILLED,
  UPDATE_TAG_REQUEST_FULFILLED,
  CREATE_TAG_REQUEST_FULFILLED,
  DELETE_TAG_REQUEST_FULFILLED,
  GET_ENTITY_TAGS_REQUEST_FULFILLED,
  UPDATE_TAG_EDIT_VIEW_STATE
} from "../actions";
import { TagsState } from "./state";

class TagsInitialState implements TagsState {
  allTags = [];
  entityTags = {};
  editView = {
    open: false,
    parent: null
  };
}

export const tagsReducer = (state: TagsState = new TagsInitialState(), action: IAction<any>): any => {
  switch (action.type) {
    case GET_ALL_TAGS_FULFILLED:
    case UPDATE_TAG_REQUEST_FULFILLED:
    case CREATE_TAG_REQUEST_FULFILLED:
    case DELETE_TAG_REQUEST_FULFILLED: {
      const { allTags } = action.payload;

      return {
        ...state,
        allTags
      };
    }

    case GET_ENTITY_TAGS_REQUEST_FULFILLED: {
      const { tags, entityName } = action.payload;

      return {
        ...state,
        ...{ entityTags: { ...state.entityTags, ...{ [entityName]: tags } } }
      };
    }

    case UPDATE_TAG_EDIT_VIEW_STATE: {
      const { open, parent } = action.payload;

      return {
        ...state,
        editView: {
          ...state.editView,
          open,
          parent
        }
      };
    }

    default:
      return state;
  }
};
