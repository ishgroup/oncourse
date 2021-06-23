import {combineEpics} from "redux-observable";
import {Observable} from "rxjs";
import {IshActions} from "../../constants/IshActions";
import {Injector} from "../../injector";
import {normalize} from "normalizr";
import {mapError, mapPayload} from "../../common/epics/EpicUtils";
import {ContactSchema} from "../../NormalizeSchema";
import {EpicInit} from "./EpicInit";
import MessagesShowEpic from "./MessagesShowEpic";
import {AddContactToSummary} from "../containers/summary/epics/EpicAddContactToSummary";
import {EpicItemSelect} from "./EpicItemSelect";
import {EpicUpdateAmount} from "./EpicUpdateAmount";
import {EpicChangePayNow} from "./EpicChangePayNow";
import {EpicProceedToPayment} from "./EpicProceedToPayment";
import {EpicPayment} from "../containers/payment/epics/EpicPayment";
import {EpicGetConcessionTypes} from "../containers/concession/epics/EpicGetConcessionTypes";
import {GetContactConcessions} from "../containers/concession/epics/EpicGetContactConcessions";
import {FinishCheckoutProcess} from "./EpicFinishCheckoutProcess";
import {EpicResult} from "../containers/result/epics/EpicResult";
import {EpicAddCode} from "./EpicAddCode";
import {EpicUpdateSummary} from "./EpicLoadCheckoutModel";
import {GetContactNode} from "./EpicGetContactNode";
import {GetAllContactNodes} from "./EpicGetAllContactNodes";
import {SubmitAddContact, AddPayerFromVoucher} from "../containers/contact-add/epics/EpicSubmitAddContact";
import {EpicUpdateParentChilds} from "../containers/summary/epics/EpicUpdateParentChilds";
import {OpenEditContact} from "../containers/contact-edit/epics/EpicOpenEditContact";
import {SubmitEditContact} from "../containers/contact-edit/epics/EpicSubmitEditContact";
import {EpicToggleRedeemVoucher} from "./EpicToggleRedeemVoucher";
import {EpicUpdateEnrolmentFields} from "./EpicUpdateEnrolmentFields";
import {EpicProcessingMandatoryFields} from "./EpicProcessingMandatoryFields";
import {EpicRemoveContact} from "./EpicRemoveContact";
import {EpicStoreCartState} from "./EpicStoreCartState";
import EpicRefreshRedeemVouchers from './EpicRefreshRedeemVouchers';
import EpicToggleRedeemVoucherProduct from './EpicToggleRedeemVoucherProduct';

const {
  contactApi,
} = Injector.of();

export const EpicCheckout = combineEpics(
  EpicInit,
  MessagesShowEpic,
  SubmitAddContact, AddPayerFromVoucher,
  SubmitEditContact,
  OpenEditContact,
  GetContactNode,
  GetAllContactNodes,
  EpicAddCode,
  AddContactToSummary,
  EpicRemoveContact,
  EpicItemSelect,
  EpicUpdateAmount,
  EpicChangePayNow,
  EpicUpdateSummary,
  EpicRefreshRedeemVouchers,
  EpicProceedToPayment,
  EpicPayment,
  EpicGetConcessionTypes, GetContactConcessions,
  FinishCheckoutProcess,
  EpicResult,
  EpicToggleRedeemVoucher,
  EpicToggleRedeemVoucherProduct,
  EpicUpdateParentChilds,
  createGetOrCreateContactEpic(IshActions.GET_OR_CREATE_CONTACT),
  EpicUpdateEnrolmentFields,
  EpicProcessingMandatoryFields,
  EpicStoreCartState
);

function createGetOrCreateContactEpic(actionType) {
  return action$ => action$
    .ofType(actionType)
    .mergeMap(action => Observable
      .fromPromise(contactApi.createOrGetContact(action.payload))
      .map(payload => normalize(payload, ContactSchema))
      .map(mapPayload(actionType))
      .catch(mapError(actionType)),
    );
}
