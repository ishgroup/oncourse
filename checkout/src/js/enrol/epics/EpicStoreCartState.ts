import {IshState} from "../../services/IshState";
import {REMOVE_ITEM_FROM_SUMMARY, REWRITE_CONTACT_NODE_TO_STATE,} from "../containers/summary/actions/Actions";
import { Store} from "redux";
import CartService from "../../services/CartService";
import {getCookie} from "../../common/utils/Cookie";
import {CHANGE_PHASE} from "../actions/Actions";
import {StoredCartData} from "../../model/common/StoredCartData";

export const EpicStoreCartState = (function () {
  return (action$, store: Store<IshState>) => action$
    .ofType(
      CHANGE_PHASE,
      REMOVE_ITEM_FROM_SUMMARY,
      REWRITE_CONTACT_NODE_TO_STATE,
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
        const { checkout } = store.getState();
        const storedCartData: StoredCartData = {};
        const contactNodes = checkout.summary.entities.contactNodes;

        let isEmptyCart = true;

        if (contactNodes) {
          Object.keys(contactNodes).forEach(key => {
            storedCartData[key] = {};
            Object.keys(contactNodes[key]).forEach(eKey => {
              if(contactNodes[key][eKey].length) {
                storedCartData[key][eKey] = contactNodes[key][eKey];
                if (eKey !== "contactId" && isEmptyCart) {
                  isEmptyCart = false;
                }
              }
            })
          })
          storedCartData.payerId = checkout.payerId;

          if (isEmptyCart) {
            CartService._delete(cartId)
              .catch(() => {
                console.error("Failed to delete cart data")
              })
          } else {
            CartService.create(cartId, JSON.stringify(storedCartData))
              .catch(() => {
                console.error("Failed to upload cart data")
              })
          }
        }
      }
    })
    .filter(() => false);
})();
