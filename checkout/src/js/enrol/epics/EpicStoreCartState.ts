import {IshState} from "../../services/IshState";
import {REMOVE_ITEM_FROM_SUMMARY, REWRITE_CONTACT_NODE_TO_STATE,} from "../containers/summary/actions/Actions";
import { Store} from "redux";
import CartService from "../../services/CartService";
import {getCookie} from "../../common/utils/Cookie";
import {FULFILLED} from "../../common/actions/ActionUtils";
import {Actions} from "../../web/actions/Actions";
import CheckoutService, {BuildContactNodeRequest} from "../services/CheckoutService";
import {ContactNodeRequest} from "../../model";
import { DEFAULT_CONFIG_KEY, DefaultConfig } from '../../constants/Config';
import { StoreCartRequest } from '../../model/checkout/request/StoreCart';
import { UPDATE_AMOUNT } from '../actions/Actions';

export const EpicStoreCartState = (function () {
  return (action$, store: Store<IshState>) => action$
    .ofType(
      UPDATE_AMOUNT,
      REWRITE_CONTACT_NODE_TO_STATE
    )
    .map(action => ({
      timestamp: new Date().getTime(),
      ...action,
    }))
    .bufferTime(1000)
    .filter(actions => actions.length)
    .do(() => {
      const cartId = getCookie("cartId");
      if (cartId) {
        const {checkout, cart} = store.getState();

        const storedCartData: StoreCartRequest = {
          checkoutURL: location.origin + (window[DEFAULT_CONFIG_KEY]?.checkoutPath || DefaultConfig.CHECKOUT_PATH),
          total: String(checkout?.amount?.total || '0.00')
        };
        const contactNodes = checkout.summary.entities.contactNodes;

        if (CheckoutService.cartIsEmpty(cart)) {
          CartService._delete(cartId)
            .catch(() => {
              console.error("Failed to delete cart data")
            })
        } else if (contactNodes) {
          Object.keys(contactNodes).forEach(key => {
            const contacts = checkout?.contacts?.entities?.contact;
            if(contacts && contacts[key]) {
              storedCartData[key] = BuildContactNodeRequest.fromContact(checkout.contacts.entities.contact[key], checkout.summary, cart, checkout.payerId);
            }
          })

          storedCartData.payerId = checkout.payerId;
          CartService.create(cartId, JSON.stringify(storedCartData))
            .catch(() => {
              console.error("Failed to upload cart data")
            })
        }
      }
    })
    .filter(() => false);
})();
