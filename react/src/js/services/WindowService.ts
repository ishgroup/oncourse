import {PublicApi} from "../external/PublicApi";
import {Bootstrap} from "../common/utils/Bootstrap";
import {ModalService} from "./ModalService";

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
}

/**
 * Declare what we allow to put in Window.Ish object.
 */
interface IshWindow {
  api: PublicApi;
  react: Bootstrap;
  modal: ModalService;
}
