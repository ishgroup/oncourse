import "rxjs";
import {ActionsObservable, Epic} from "redux-observable";
import {IshState} from "../../../../services/IshState";
import {MiddlewareAPI} from "redux";
import {Observable} from "rxjs/Observable";
import {ADD_CONTACT_TO_SUMMARY, addContactNodeToState, getContactNodeFromBackend} from "../actions/Actions";
import {
  changePhase, setPayer, updateContactAddProcess, updateParentChilds,
} from "../../../actions/Actions";
import {addContact as addContactToCart} from "../../../../web/actions/Actions";
import {IAction} from "../../../../actions/IshAction";
import {Application, Contact, Enrolment} from "../../../../model";
import CheckoutService from "../../../services/CheckoutService";
import {addContact} from "../../contact-add/actions/Actions";
import {Phase} from "../../../reducers/State";

export const AddContactToSummary: Epic<any, IshState> = (action$: ActionsObservable<any>, store: MiddlewareAPI<IshState>): Observable<any> => {
  return action$.ofType(ADD_CONTACT_TO_SUMMARY).flatMap((action: IAction<{contact: Contact, uncheckItems?: boolean}>) => {
    const state: IshState = store.getState();

    const {type, parent, forChild} = state.checkout.contactAddProcess;
    const contact = action.payload.contact;
    const payerId = ((!contact.parentRequired || parent) && contact.id) || null;
    const ifParentRequired = contact.parentRequired && !parent;
    const nextPage = state.checkout.phase === Phase.ComplementEditContact ? Phase.Payment : state.checkout.page;
    const isAddParentPhase = (type === Phase.AddParent || type === Phase.ChangeParent);
    const wrongGuardianMessage = `This contact is under 18 and can not be the nominated guardian. 
    Add another contact who is over 18, or contact us to update the guardian details`;

    const allChilds = CheckoutService
      .getAllSingleChildIds(state.checkout)
      .concat(ifParentRequired ? contact.id : []);

    // Uncheck all enrolments for contact. Cases:
    // - if new contact is a parent/guardian for existing children
    // - if new contact is a payer
    const uncheckContactItems: boolean =
      (action.payload.uncheckItems ||
        type === Phase.AddContactAsPayer ||
        type === Phase.AddContactAsCompany ||
        type === Phase.ChangeParent ||
        type === Phase.AddParent
      );

    const result = [];

    if (!contact.id) return [changePhase(state.checkout.page)];

    // Set payer if it didn't exist
    if (!CheckoutService.hasPayer(state.checkout)) {
      result.push(setPayer(payerId));
      result.push(addContactToCart(contact));
    }

    // Force set payer from payment screen if payer age is valid or have parent
    if ((type === Phase.AddContactAsPayer || type === Phase.AddContactAsCompany) && payerId) {
      result.push(setPayer(payerId));
    }

    // Update parent for existing children or reassign new parent
    if ((allChilds.length || forChild) && !contact.parentRequired && isAddParentPhase) {
      result.push(updateParentChilds(contact.id, forChild ? [forChild] : allChilds));
    }

    // Add new contact to summary and update parentRequired flag
    result.push(addContact({
      ...contact,
      parent: parent || null,
      parentRequired: contact.parentRequired,
      warning: contact.parentRequired && isAddParentPhase && wrongGuardianMessage,
    }));

    const statePart = {
      enrolments: [],
      applications: []
    }

    state.cart.courses.result.forEach(key => {
      const cartEn = state.cart.courses.entities[key];
      if (cartEn.isAllowByApplication) {
        const app = new Application();
        app.contactId = contact.id;
        app.classId = cartEn.id;
        app.warnings = [];
        app.errors = [];
        app.selected = !uncheckContactItems;
        app.fieldHeadings = [];
        statePart.applications.push(app);
      } else {
        const en = new Enrolment();
        en.contactId = contact.id;
        en.classId = cartEn.id;
        en.allowRemove = null;
        en.courseId = null;
        en.errors = [];
        en.fieldHeadings = [];
        en.price = {...cartEn.price};
        en.relatedClassId = null;
        en.relatedProductId = null;
        en.selected = !uncheckContactItems;
        en.warnings = [];
        statePart.enrolments.push(en);
      }
    })

    result.push(addContactNodeToState({
      contactId: contact.id,
      articles: [],
      memberships: [],
      vouchers: [],
      waitingLists: [],
      ...statePart
    }));

    result.push(getContactNodeFromBackend(contact, uncheckContactItems));
    result.push(updateContactAddProcess({}, null, null));
    result.push(changePhase(nextPage));

    return result;
  });
};
