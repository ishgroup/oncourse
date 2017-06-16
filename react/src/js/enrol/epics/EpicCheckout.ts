import {combineEpics} from "redux-observable";
import {Observable} from "rxjs";

import {IshActions} from "../../constants/IshActions";
import {Injector} from "../../injector";
import {normalize} from "normalizr";
import {mapError, mapPayload} from "../../epics/epicsUtils";
import {ContactSchema} from "../../NormalizeSchema";
import {EpicInit} from "./EpicInit";
import {EpicAddContact} from "./EpicAddContact";
import MessagesShowEpic from "./MessagesShowEpic";
import PhaseChangeEpic from "./PhaseChangeEpic";
import OpenContactEditEpic from "./OpenContactDetailsEpic";
import {EpicOpenSummary} from "./EpicOpenSummary";
import {EpicItemSelect} from "./EpicItemSelect";
import {EpicUpdateAmount} from "./EpicUpdateAmount";
import {EpicProceedToPayment} from "./EpicProceedToPayment";
import {EpicPayment} from "../containers/payment/epics/EpicPayment";
import {FinishCheckoutProcess} from "./EpicFinishCheckoutProcess";
import {EpicResult} from "../containers/result/epics/EpicResult";
import {EpicAddCode} from "./EpicAddCode";
import {EpicUpdateSummary} from "./EpicUpdateSummary";

const {
  contactApi
} = Injector.of();

export const EpicCheckout = combineEpics(
  EpicInit,
  MessagesShowEpic,
  PhaseChangeEpic,
  EpicAddContact,
  OpenContactEditEpic,
  EpicAddCode,
  EpicOpenSummary,
  EpicItemSelect,
  EpicUpdateAmount,
  EpicUpdateSummary,
  EpicProceedToPayment,
  EpicPayment,
  FinishCheckoutProcess,
  EpicResult,
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
