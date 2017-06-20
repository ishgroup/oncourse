import "rxjs";
import {Epic} from "redux-observable";
import {ContactFields} from "../../../../model/field/ContactFields";
import {IshState} from "../../../../services/IshState";
import {OPEN_EDIT_CONTACT, setFieldsToState} from "../actions/Actions";
import {Contact} from "../../../../model/web/Contact";
import CheckoutService from "../../../services/CheckoutService";
import {changePhase} from "../../../actions/Actions";
import {Phase} from "../../../reducers/State";
import {addContactToSummary} from "../../summary/actions/Actions";
import {Create, Request} from "../../../epics/EpicUtils";

const request: Request<ContactFields, IshState> = {
  type: OPEN_EDIT_CONTACT,
  getData: (contact: Contact, state: IshState) => CheckoutService.loadFields(contact, state),
  processData: (value: ContactFields, state: IshState) => {
    if (value.headings.length > 0) {
      return [setFieldsToState(value), changePhase(Phase.EditContact)];
    }
    else {
      return [addContactToSummary(state.checkout.contacts.entities.contact[value.contactId])];
    }
  }
};

export const OpenEditContact: Epic<any, IshState> = Create(request);