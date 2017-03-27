import {PublicApi} from "../external/PublicApi";
import {Bootstrap} from "../lib/Bootstrap";
import {ModalService} from "./ModalService";

export class WindowService {
  static set<T extends IshWindow[K], K extends keyof IshWindow>(name: K, value: T) {
    const w = window as any;
    w.Ish = w.Ish || {}; // check that Ish global exists
    w.Ish[name] = value; // set value
  }
}

interface IshWindow {
  api: PublicApi;
  react: Bootstrap;
  modal: ModalService;
}
