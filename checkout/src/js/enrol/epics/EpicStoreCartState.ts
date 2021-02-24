import {IshState} from "../../services/IshState";
import {REMOVE_ITEM_FROM_SUMMARY, REWRITE_CONTACT_NODE_TO_STATE,} from "../containers/summary/actions/Actions";
import { Store} from "redux";
import CartService from "../../services/CartService";
import {getCookie} from "../../common/utils/Cookie";
import {StoredCartData} from "../../model/common/StoredCartData";
import {FULFILLED} from "../../common/actions/ActionUtils";
import {Actions} from "../../web/actions/Actions";

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
