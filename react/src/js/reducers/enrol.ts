import {Action, combineReducers} from "redux";
import {FULFILLED, IshActions, REJECTED} from "../constants/IshActions";
import {IshAction} from "../actions/IshAction";
import {EnrolState, Normalized, PayerState} from "../services/IshState";
import {Contact} from "../model/Contact";
import {ValidationError} from "../model/ValidationError";
import {RestError} from "../types";

export const enrolReducer = combineReducers<EnrolState>({
  payer: combineReducers<PayerState>({
    entity: payerReducer,
    error: payerErrorReducer
  })
});

function payerReducer(state = {}, action: IshAction<Normalized<Contact>>) {
  switch (action.type) {
    case FULFILLED(IshActions.GET_OR_CREATE_CONTACT):
    case FULFILLED(IshActions.SUBMIT_ADD_CONTACT_FORM):
      return action.payload.entities.contacts[action.payload.result];
    default:
      return state;
  }
}

function payerErrorReducer(state = {}, action: IshAction<RestError<ValidationError>>) {
  switch (action.type) {
    case REJECTED(IshActions.GET_OR_CREATE_CONTACT):
    case REJECTED(IshActions.SUBMIT_ADD_CONTACT_FORM):
      // Check if validation error response
      if (action.payload.status === 400 && action.payload.data.formErrors) {
        return action.payload.data;
      }
      return state;
    case FULFILLED(IshActions.GET_OR_CREATE_CONTACT):
    case FULFILLED(IshActions.SUBMIT_ADD_CONTACT_FORM):
      return {};
    default:
      return state;
  }
}
