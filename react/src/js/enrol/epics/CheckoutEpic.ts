import {combineEpics} from "redux-observable";
import {Observable} from "rxjs";

import {IshActions} from "../../constants/IshActions";
import {Injector} from "../../injector";
import {normalize} from "normalizr";
import {mapError, mapPayload} from "../../epics/epicsUtils";
import {contactsSchema} from "../../schema";
import InitEpic from "./InitEpic";
import ContactAddEpic from "./ContactAddEpic";
import MessagesSetEpic from "./MessagesSetEpic";
import PhaseChangeEpic from "./PhaseChangeEpic";
import OpenContactDetailsEpic from "./OpenContactDetailsEpic";
import OpenSummaryEpic from "./OpenSummaryEpic";

const {
  contactApi
} = Injector.of();

export const CheckoutEpic = combineEpics(
  InitEpic,
  PhaseChangeEpic,
  ContactAddEpic,
  OpenContactDetailsEpic,
  OpenSummaryEpic,
  MessagesSetEpic,
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
