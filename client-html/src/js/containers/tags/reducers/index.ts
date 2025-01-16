import { IAction } from "../../../common/actions/IshAction";
import {
  GET_ALL_TAGS_FULFILLED,
  GET_ENTITY_SPECIAL_TAGS_REQUEST_FULFILLED,
  GET_ENTITY_TAGS_REQUEST_FULFILLED,
} from "../actions";
import { TagsState } from "./state";

class TagsInitialState implements TagsState {
  allTags = [];

  allChecklists = [];

  entityTags = {};

  entitySpecialTags = {};
}

export const tagsReducer = (state: TagsState = new TagsInitialState(), action: IAction<any>): any => {
  switch (action.type) {
    case GET_ALL_TAGS_FULFILLED: {
      return {
        ...state,
        ...action.payload
      };
    }

    case GET_ENTITY_SPECIAL_TAGS_REQUEST_FULFILLED: {
      const { tags, entityName } = action.payload;

      return {
        ...state,
        entitySpecialTags: {
          ...state.entityTags,
          [entityName]: tags
        }
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
