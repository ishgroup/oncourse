import {IshState} from "../../services/IshState";
import {REMOVE_ITEM_FROM_SUMMARY, REWRITE_CONTACT_NODE_TO_STATE,} from "../containers/summary/actions/Actions";
import { Store} from "redux";
import CartService from "../../services/CartService";
import {getCookie} from "../../common/utils/Cookie";
import {FULFILLED} from "../../common/actions/ActionUtils";
import {Actions} from "../../web/actions/Actions";
import CheckoutService, {BuildContactNodeRequest} from "../services/CheckoutService";
import {ContactNodeRequest} from "../../model";

export const EpicStoreCartState = (function () {
  return (action$, store: Store<IshState>) => action$
    .ofType(
      REMOVE_ITEM_FROM_SUMMARY,
      REWRITE_CONTACT_NODE_TO_STATE,
      FULFILLED(Actions.ADD_PRODUCT_TO_CART),
      FULFILLED(Actions.ADD_CLASS_TO_CART),
      FULFILLED(Actions.ADD_WAITING_COURSE_TO_CART)
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
        const storedCartData: { [key: string]: ContactNodeRequest } & { payerId?: string } = {};
        const contactNodes = checkout.summary.entities.contactNodes;

        if (CheckoutService.cartIsEmpty(cart)) {
          CartService._delete(cartId)
            .catch(() => {
              console.error("Failed to delete cart data")
            })
        } else if (contactNodes) {
          Object.keys(contactNodes).forEach(key => {
            storedCartData[key] = BuildContactNodeRequest.fromContact(checkout.contacts.entities.contact[key], checkout.summary, cart);
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
