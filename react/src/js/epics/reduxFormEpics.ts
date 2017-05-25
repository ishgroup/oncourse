import {combineEpics} from "redux-observable";
import {FULFILLED, REJECTED} from "../common/actions/ActionUtils";
import {FormErrors, startSubmit, stopSubmit} from "redux-form";
import {Injector} from "../injector";
import {Observable} from "rxjs";
import {normalize} from "normalizr";
import {ContactsSchema} from "../NormalizeSchema";
import {mapError, mapPayload} from "./epicsUtils";
import {ValidationError} from "../model/common/ValidationError";
import {IshAction} from "../actions/IshAction";
import {IshActions} from "../constants/IshActions";

const {
  contactApi
} = Injector.of();

export const reduxFormEpics = combineEpics(
  createStartSubmitAddContactFormEpic(IshActions.SUBMIT_ADD_CONTACT_FORM),
  createStartSubmitFormEpic(),
  createStopSubmitFormEpic()
);

function createStartSubmitAddContactFormEpic(actionType) {
  return action$ => action$
    .ofType(actionType)
    .mergeMap(action => Observable
      .fromPromise(contactApi.createOrGetContact(action.payload))
      .map(payload => normalize(payload, ContactsSchema))
      .map(payload => ({...mapPayload(actionType)(payload), ...mapMeta(action)}))
      .catch(payload => mapError(actionType)(payload).map(error => ({...error, ...mapMeta(action)})))
    );
}

function createStartSubmitFormEpic() {
  return action$ => action$
    .ofType(
      IshActions.SUBMIT_ADD_CONTACT_FORM
    )
    .map(action => startSubmit(action.meta.form))
}

function createStopSubmitFormEpic() {
  return action$ => action$
    .ofType(
      REJECTED(IshActions.SUBMIT_ADD_CONTACT_FORM),
      FULFILLED(IshActions.SUBMIT_ADD_CONTACT_FORM)
    )
    .map(action => {
      if (isValidationError(action)) {
        return stopSubmit(action.meta.form, convertToReduxFormErrors(action.payload.data))
      } else {
        return stopSubmit(action.meta.form)
      }
    })
}

function convertToReduxFormErrors<T>(backendError: ValidationError): FormErrors<T> {
  const errors = {};

  backendError.fieldsErrors.forEach(field => {
    errors[field.name] = field.error;
  });

  errors["_error"] = backendError.formErrors.join(" ");

  return errors;
}

function isValidationError(action: IshAction<any>): boolean {
  return !!(action.error && action.payload.data.formErrors);
}

function mapMeta({meta: {form}}) {
  return {meta: {form}};
}
