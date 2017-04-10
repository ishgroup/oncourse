import {combineEpics} from "redux-observable";
import {IshActions} from "../constants/IshActions";
import {Observable} from "rxjs";
import {Injector} from "../injector";
import {normalize} from "normalizr";
import {mapError, mapPayload} from "./epicsUtils";
import {contactsSchema} from "../schema";

const {
  contactApi
} = Injector.of();

export const enrolEpics = combineEpics(
  createGetOrCreateContactEpic(IshActions.GET_OR_CREATE_CONTACT)
);

function createGetOrCreateContactEpic(actionType) {
  return action$ => action$
    .ofType(actionType)
    .mergeMap(action => Observable
      .fromPromise(contactApi.createOrGetContact(action.payload))
      .map(payload => normalize(payload, contactsSchema))
      .map(mapPayload(actionType))
      .catch(mapError(actionType))
    );
}
