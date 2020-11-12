import { ActionsObservable, Epic } from "redux-observable";
import { mergeMap } from "rxjs/operators";
import { State } from "../../../reducers/state";
import { UPDATE_TAG_EDIT_VIEW_STATE } from "../actions";
import { initialize } from "redux-form";

export const EpicUpdateTagEditViewState: Epic<any, State> = (action$: ActionsObservable<any>): any => {
  return action$.ofType(UPDATE_TAG_EDIT_VIEW_STATE).pipe(
    mergeMap(action => {
      const { item } = action.payload;

      return [initialize("TagItemForm", item)];
    })
  );
};
