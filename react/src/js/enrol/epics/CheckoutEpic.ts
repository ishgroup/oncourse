import {combineEpics} from "redux-observable";
import {Observable} from "rxjs";

import {IshActions} from "../../constants/IshActions";
import {Injector} from "../../injector";
import {normalize} from "normalizr";
import {mapError, mapPayload} from "../../epics/epicsUtils";
import {ContactSchema} from "../../NormalizeSchema";
import InitEpic from "./InitEpic";
import {EpicAddContact} from "./EpicAddContact";
import MessagesShowEpic from "./MessagesShowEpic";
import PhaseChangeEpic from "./PhaseChangeEpic";
import OpenContactEditEpic from "./OpenContactDetailsEpic";
import {EpicOpenSummary} from "./EpicOpenSummary";
import {EpicItemSelect} from "./EpicItemSelect";
import {EpicUpdateAmount} from "./EpicUpdateAmount";
import {EpicProceedToPayment} from "./EpicProceedToPayment";
import {EpicMakePayment} from "./EpicMakePayment";

const {
  contactApi
} = Injector.of();

export const CheckoutEpic = combineEpics(
  InitEpic,
  MessagesShowEpic,
  PhaseChangeEpic,
  EpicAddContact,
  OpenContactEditEpic,
  EpicOpenSummary,
  EpicItemSelect,
  EpicUpdateAmount,
  EpicProceedToPayment,
  EpicMakePayment,
  createGetOrCreateContactEpic(IshActions.GET_OR_CREATE_CONTACT)
);

function createGetOrCreateContactEpic(actionType) {
  return action$ => action$
    .ofType(actionType)
    .mergeMap(action => Observable
      .fromPromise(contactApi.createOrGetContact(action.payload))
      .map(payload => normalize(payload, ContactSchema))
      .map(mapPayload(actionType))
      .catch(mapError(actionType))
    );
}
