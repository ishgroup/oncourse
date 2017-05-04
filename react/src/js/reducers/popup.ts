import {Actions} from "../web/actions/Actions";

const initialState = {
  content: null
};

const handleActions = {
  [Actions.UPDATE_POPUP](state, action) {
    return {
      ...state,
      content: action.content
    };
  },

  [Actions.HIDE_POPUP](state) {
    return {
      ...state,
      content: null
    };
  }
};

export default function (state = initialState, action) {
  return handleActions[action.type] ? handleActions[action.type](state, action) : state;
}
