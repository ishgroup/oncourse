import {SuggestionsState} from "../../services/IshState";
import {IshAction} from "../../actions/IshAction";
import {combineReducers} from "redux";
import {FULFILLED} from "../../common/actions/ActionUtils";
import {Actions} from "../actions/Actions";

export const suggestionsReducer = combineReducers({
  entities: byId,
  result: allIds,
});

function allIds(state:any = [], action: IshAction<SuggestionsState>):any {
  switch (action.type) {
    case FULFILLED(Actions.REQUEST_SUGGESTION):
      return [
        ...state,
        ...action.payload.result
          .filter(t => !state.includes(t)), // dedup
      ];
    default:
      return state;
  }
}

function byId(state:any = {}, action: IshAction<SuggestionsState>): any {
  switch (action.type) {
    case FULFILLED(Actions.REQUEST_SUGGESTION):
      return {
        ...state,
        ...action.payload.entities.suggestions,
      };
    default:
      return state;
  }
}
