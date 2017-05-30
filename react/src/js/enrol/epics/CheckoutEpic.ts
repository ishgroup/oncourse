import {combineEpics} from "redux-observable";
import {Observable} from "rxjs";

import {IshActions} from "../../constants/IshActions";
import {Injector} from "../../injector";
import {normalize} from "normalizr";
import {mapError, mapPayload} from "../../epics/epicsUtils";
import {ContactsSchema} from "../../NormalizeSchema";
import InitEpic from "./InitEpic";
import {EpicAddContact} from "./EpicAddContact";
import MessagesShowEpic from "./MessagesShowEpic";
import PhaseChangeEpic from "./PhaseChangeEpic";
import OpenContactEditEpic from "./OpenContactDetailsEpic";
import OpenSummaryEpic from "./OpenSummaryEpic";
import {EpicItemSelect} from "./EpicItemSelect";
import {EpicUpdateAmount} from "./EpicUpdateAmount";
import {EpicProceedToPayment} from "./EpicProceedToPayment";

const {
  contactApi
} = Injector.of();

export const CheckoutEpic = combineEpics(
  InitEpic,
  MessagesShowEpic,
  PhaseChangeEpic,
  EpicAddContact,
  OpenContactEditEpic,
  OpenSummaryEpic,
  EpicItemSelect,
  EpicUpdateAmount,
  EpicProceedToPayment,
  createGetOrCreateContactEpic(IshActions.GET_OR_CREATE_CONTACT)
);

function createGetOrCreateContactEpic(actionType) {
  return action$ => action$
    .ofType(actionType)
    .mergeMap(action => Observable
      .fromPromise(contactApi.createOrGetContact(action.payload))
      .map(payload => normalize(payload, ContactsSchema))
      .map(mapPayload(actionType))
      .catch(mapError(actionType))
    );
}
