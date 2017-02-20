import {Store} from "redux";
import {IshState, CartState} from "../services/IshState";

export class PublicApi {

  constructor(private store: Store<IshState>) {}

  /**
   * Return current state of Cart in redux store.
   *
   * @returns {CartState} current cart state
   */
  getCart(): CartState {
    return this.store.getState().cart;
  }
}
