import {IshState} from "../../services/IshState";
import {REWRITE_CONTACT_NODE_TO_STATE, } from "../containers/summary/actions/Actions";
import { Store} from "redux";
import {FULFILLED} from "../../common/actions/ActionUtils";
import {Actions} from "../../web/actions/Actions";
import {CartService} from "../../services/CartService";
import {DefaultHttpService} from "../../common/services/HttpService";
import {getCookie} from "../../common/utils/Cookie";

export const EpicStoreCartState = (function () {
  return (action$, store: Store<IshState>) => action$
    .ofType(
      FULFILLED(Actions.ADD_CLASS_TO_CART),
      FULFILLED(Actions.REPLACE_CLASS_IN_CART),
      FULFILLED(Actions.REMOVE_CLASS_FROM_CART),
      FULFILLED(Actions.ADD_PRODUCT_TO_CART),
      FULFILLED(Actions.REMOVE_PRODUCT_FROM_CART),
      FULFILLED(Actions.ADD_PROMOTION_TO_CART),
      FULFILLED(Actions.REMOVE_PROMOTION_FROM_CART),
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
        const cartApi = new CartService(new DefaultHttpService());
        const { cart, checkout } = store.getState();
        cartApi.create(cartId, JSON.stringify({ cart, checkout }))
          .catch(() => {
            console.error("Failed to upload cart data")
          })
      }
    })
    .filter(() => false);
})();
