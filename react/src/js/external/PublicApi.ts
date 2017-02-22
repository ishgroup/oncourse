import {Store, Unsubscribe} from "redux";
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

  /**
   * Subscribe on store changes, so end users can be notified on important changes.
   *
   * @param listener
   * @returns {Unsubscribe} unsubscribe function
   */
  subscribe(listener: (state: IshState) => void): Unsubscribe {
    return this.store.subscribe(() => listener(this.store.getState()));
  }
}
