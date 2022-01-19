import { Store } from 'redux';
import { IshState } from '../../services/IshState';
import CartService from '../../services/CartService';
import { getCookie } from '../../common/utils/Cookie';
import CheckoutService, { BuildContactNodeRequest } from '../services/CheckoutService';
import { DEFAULT_CONFIG_KEY, DefaultConfig } from '../../constants/Config';
import { StoreCartRequest } from '../../model/checkout/request/StoreCart';
import { FULFILLED } from '../../common/actions/ActionUtils';
import { Actions } from '../../web/actions/Actions';
import { REWRITE_CONTACT_NODES_TO_STATE } from '../containers/summary/actions/Actions';

export const EpicStoreCartState = (function () {
  return (action$, store: Store<IshState>) => action$
    .ofType(
      FULFILLED(Actions.ADD_CLASS_TO_CART),
      FULFILLED(Actions.REMOVE_CLASS_FROM_CART),
      FULFILLED(Actions.ADD_PRODUCT_TO_CART),
      FULFILLED(Actions.REMOVE_PRODUCT_FROM_CART),
      FULFILLED(Actions.ADD_PROMOTION_TO_CART),
      FULFILLED(Actions.REMOVE_PROMOTION_FROM_CART),
      REWRITE_CONTACT_NODES_TO_STATE
    )
    .map((action) => ({
      timestamp: new Date().getTime(),
      ...action,
    }))
    .bufferTime(1000)
    .filter((actions) => actions.length)
    .do(() => {
      const cartId = getCookie('cartId');

      if (cartId) {
        const { checkout, cart } = store.getState();

        const storedCartData: StoreCartRequest = {
          checkoutURL: location.origin + (window[DEFAULT_CONFIG_KEY]?.checkoutPath || DefaultConfig.CHECKOUT_PATH),
        };
        const { contactNodes } = checkout.summary.entities;

        if (CheckoutService.cartIsEmpty(cart)) {
          CartService._delete(cartId)
            .catch(() => {
              console.error('Failed to delete cart data');
            });
        } else if (contactNodes) {
          Object.keys(contactNodes).forEach((key) => {
            const contacts = checkout?.contacts?.entities?.contact;
            if (contacts && contacts[key]) {
              storedCartData[key] = BuildContactNodeRequest.fromContact(checkout.contacts.entities.contact[key], checkout.summary, cart, checkout.payerId);
            }
          });

          storedCartData.payerId = checkout.payerId;
          CartService.create(cartId, JSON.stringify(storedCartData))
            .catch(() => {
              console.error('Failed to upload cart data');
            });
        }
      }
    })
    .filter(() => false);
}());
