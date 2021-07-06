import configureStore from 'redux-mock-store';
import {createEpicMiddleware} from "redux-observable";
import {EpicRoot} from "../../../../js/EpicRoot";
import {
  changePhase, epicRemoveContact, removeContact, removeRedeemVoucher, setPayer,
} from "../../../../js/enrol/actions/Actions";
import {state as singleContactState} from "../../mock/singleContactState";
import {state as multipleContactsState} from "../../mock/multipleContactsState";
import {Phase} from "../../../../js/enrol/reducers/State";
import {
  getAllContactNodesFromBackend, removeContactFromSummary,
} from "../../../../js/enrol/containers/summary/actions/Actions";


const middlewares = [createEpicMiddleware(EpicRoot)];
const mockStore = configureStore(middlewares);


describe('Remove contacts:', () => {

  it('last contact', () => {
    const store = mockStore(singleContactState);
    const contactId = singleContactState.checkout.contacts.result[0];

    store.dispatch(epicRemoveContact(contactId));

    const actions = store.getActions();

    expect(actions.length).toEqual(6);
    expect(actions[0]).toEqual(epicRemoveContact(contactId));
    expect(actions[1]).toEqual(setPayer(null));
    expect(actions[2]).toEqual(changePhase(Phase.AddContact));
    expect(actions[3]).toEqual(removeContact(contactId));
    expect(actions[4]).toEqual(removeContactFromSummary(contactId));
    expect(actions[5]).toEqual(getAllContactNodesFromBackend());

  });

  it('not last, without redeem voucher, not a payer', () => {
    const store = mockStore(multipleContactsState);
    const contactId = multipleContactsState.checkout.contacts.result[0];

    store.dispatch(epicRemoveContact(contactId));

    const actions = store.getActions();

    expect(actions.length).toEqual(4);
    expect(actions[0]).toEqual(epicRemoveContact(contactId));
    expect(actions[1]).toEqual(removeContact(contactId));
    expect(actions[2]).toEqual(removeContactFromSummary(contactId));
    expect(actions[3]).toEqual(getAllContactNodesFromBackend());

  });

  it('not last, with redeem voucher, is a payer', () => {
    const store = mockStore(multipleContactsState);
    const contactId = multipleContactsState.checkout.contacts.result[2];
    const voucher = multipleContactsState.checkout.redeemVouchers &&
      multipleContactsState.checkout.redeemVouchers.length &&
      multipleContactsState.checkout.redeemVouchers.find(v => v.payer && v.payer.id === contactId);
    const nextPayerId = multipleContactsState.checkout.summary.result.find(id => id !== contactId);

    store.dispatch(epicRemoveContact(contactId));

    const actions = store.getActions();
    expect(actions.length).toEqual(6);
    expect(actions[0]).toEqual(epicRemoveContact(contactId));
    expect(actions[1]).toEqual(setPayer(nextPayerId as any));
    expect(actions[2]).toEqual(removeRedeemVoucher(voucher));
    expect(actions[3]).toEqual(removeContact(contactId));
    expect(actions[4]).toEqual(removeContactFromSummary(contactId));
    expect(actions[5]).toEqual(getAllContactNodesFromBackend());

  });


});
