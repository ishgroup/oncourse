import {Bootstrap} from "../common/utils/Bootstrap";
import {ModalService} from "./ModalService";
import {Store} from "redux";
import {IshState, PromotionCart} from "./IshState";
import {Actions} from "../web/actions/Actions";

export class WindowService {

  /**
   * Get value from global Ish object.
   */
  static get<T extends IshWindow[K], K extends keyof IshWindow>(name: K): T {
    return WindowService.getIsh()[name];
  }

  /**
   * Put value at global Ish object.
   */
  static set<T extends IshWindow[K], K extends keyof IshWindow>(name: K, value: T) {
    WindowService.getIsh()[name] = value; // set value
  }

  private static getIsh() {
    const w = window as any;
    w.Ish = w.Ish || {}; // check that Ish global exists
    return w.Ish;
  }

  static initCheckoutApi(store: Store<IshState>) {
    const w = window as any;
    
    w.ShoppingCart = {
      
      addPromoCode: (code: string) => {
        store.dispatch({
          type: Actions.ADD_PROMOTION_TO_CART,
          payload: code,
        });
      },
      
      removePromoCode: (code: string) => {
        const promotion: PromotionCart  = Object.values(store.getState().cart.promotions.entities).find(item => item.code === code);
        store.dispatch({
          type: Actions.REMOVE_PROMOTION_FROM_CART,
          payload: promotion,
        });
      },
      
    }
  }
  
}

/**
 * Declare what we allow to put in Window.Ish object.
 */
interface IshWindow {
  react: Bootstrap;
  modal: ModalService;
}
