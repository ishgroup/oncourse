import { IAction } from "../../../common/actions/IshAction";
import {
  CREATE_TAG_REQUEST_FULFILLED,
  DELETE_TAG_REQUEST_FULFILLED,
  GET_ALL_TAGS_FULFILLED,
  GET_ENTITY_TAGS_REQUEST_FULFILLED,
  UPDATE_TAG_REQUEST_FULFILLED
} from "../actions";
import { TagsState } from "./state";

class TagsInitialState implements TagsState {
  allTags = [];

  entityTags = {};
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

    default:
      return state;
  }
};
