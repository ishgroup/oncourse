import {Epic, ActionsObservable} from "redux-observable";
import {MiddlewareAPI} from "redux";
import {Observable} from "rxjs/Observable";
import * as L from "lodash";
import {IAction} from "../../actions/IshAction";
import "rxjs";
import {IshState} from "../../services/IshState";
import {REWRITE_CONTACT_NODE_TO_STATE, UPDATE_ENROLMENT_FIELDS} from "../containers/summary/actions/Actions";
import {toFormKey} from "../../components/form/FieldFactory";

export const EpicUpdateEnrolmentFields: Epic<any, any> = (action$: ActionsObservable<any>, store: MiddlewareAPI<IshState>): Observable<any> => {

  // TODO: Will be optimized
  return action$.ofType(UPDATE_ENROLMENT_FIELDS).flatMap((action: IAction<any>): any => {
    const state = store.getState();
    const result = [];

    // form name `[contactId]-[classId]` format. The same as enrolmentId in ContactNodes
    const form = action.payload;

    if (state.form[form] && state.form[form].values) {
      const values = state.form[form].values;

      const newFields = state.checkout.summary.entities.enrolments[form].fields.map(field =>
        ({...field, value: values[toFormKey(field.key)] || field.value}),
      );

      const newState = L.cloneDeep(state.checkout.summary);
      newState.entities.enrolments[form].fields = newFields;

      result.push({
        type: REWRITE_CONTACT_NODE_TO_STATE,
        payload: newState,
      });
    }

    return result;
  });
};
