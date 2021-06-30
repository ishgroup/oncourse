import {Epic, ActionsObservable} from "redux-observable";
import {MiddlewareAPI} from "redux";
import {Observable} from "rxjs/Observable";
import * as L from "lodash";

import {IAction} from "../../actions/IshAction";
import "rxjs";
import {IshState} from "../../services/IshState";
import {REWRITE_CONTACT_NODE_TO_STATE, UPDATE_ENROLMENT_FIELDS} from "../containers/summary/actions/Actions";
import {toFormKey} from "../../components/form/FieldFactory";
import {Field, DataType} from "../../model";

export const EpicUpdateEnrolmentFields: Epic<any, any> = (action$: ActionsObservable<any>, store: MiddlewareAPI<any, IshState>): Observable<any> => {

  // TODO: Will be optimized
  return action$.ofType(UPDATE_ENROLMENT_FIELDS).flatMap((action: IAction<any>): any => {
    const state = store.getState();
    const result = [];

    // form name `[contactId]-[classId]` format. The same as enrolmentId in ContactNodes
    const {form, type} = action.payload;

    if (state.form[form] && state.form[form].values) {

      const values = state.form[form].values;
      const headings = state.checkout.summary.entities[type][form]?.fieldHeadings || [];

      headings.forEach(h => {
        h.fields.forEach((f: Field, i) => {
          const formKey = toFormKey(f.key,i);

          f.value = values[formKey] && values[formKey].key || values[formKey] || null;
          if (f.value == null && f.dataType === DataType.BOOLEAN) {
            f.value = 'false';
          }
        });
      });

      const newState = L.cloneDeep(state.checkout.summary);
      newState.entities[type][form].fieldHeadings = headings;

      result.push({
        type: REWRITE_CONTACT_NODE_TO_STATE,
        payload: newState,
      });
    }

    return result;
  });
};
