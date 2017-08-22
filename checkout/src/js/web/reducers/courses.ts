import {CoursesState} from "../../services/IshState";
import {IshAction} from "../../actions/IshAction";
import {combineReducers} from "redux";
import {FULFILLED} from "../../common/actions/ActionUtils";
import {Actions} from "../actions/Actions";

export const coursesReducer = combineReducers({
  entities: byId,
  result: allIds,
});

function allIds(state = [], action: IshAction<CoursesState>) {
  switch (action.type) {
    case FULFILLED(Actions.REQUEST_COURSE_CLASS):
    case Actions.PutClassToStore:
      return [
        ...state,
        ...action.payload.result
          .filter(t => !state.includes(t)), // dedup
      ];
    default:
      return state;
  }
}

function byId(state = {}, action: IshAction<CoursesState>) {
  switch (action.type) {
    case FULFILLED(Actions.REQUEST_COURSE_CLASS):
    case Actions.PutClassToStore:
      return {
        ...state,
        ...action.payload.entities.classes,
      };
    default:
      return state;
  }
}
