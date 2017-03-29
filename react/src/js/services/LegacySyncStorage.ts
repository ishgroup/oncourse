import {CookieService} from "./CookieService";
import {IshAction} from "../actions/IshAction";
import {CourseClassCartState, ProductCartState} from "./IshState";
import {FULFILLED, IshActions} from "../constants/IshActions";
import {Level, Logger, LogMessage} from "./Logger";

export class LegacySyncStorage {
  addCourse(id: string) {
    add(id, "shorlist");
  }

  removeCourse(id: string) {
    remove(id, "shorlist");
  }

  addProduct(id: string) {
    add(id, "productShortList");
  }

  removeProduct(id: string) {
    remove(id, "productShortList");
  }

  sync = (action: IshAction<CourseClassCartState | ProductCartState>) => {
    switch (action.type) {
      case FULFILLED(IshActions.ADD_CLASS_TO_CART):
        return this.addCourse(getId(action));
      case FULFILLED(IshActions.REMOVE_CLASS_FROM_CART):
        return this.removeCourse(getId(action));
      case FULFILLED(IshActions.ADD_PRODUCT_TO_CART):
        return this.addProduct(getId(action));
      case FULFILLED(IshActions.REMOVE_PRODUCT_FROM_CART):
        return this.removeProduct(getId(action));
      default:
        Logger.log(new LogMessage(Level.ERROR, "Unexpected action", [action]));
        return;
    }
  }
}

function getId(action: IshAction<CourseClassCartState | ProductCartState>) {
  return action.payload.result;
}

function add(id: string, name: string) {
  const list = CookieService.get(name);

  if (list) {
    const ids = list.split("%");

    if (ids.includes(id)) {
      return;
    }

    const newIds = [...ids, id].join("%");

    CookieService.set(name, newIds)
  } else {
    CookieService.set(name, id)
  }
}

function remove(id: string, name: string) {
  const list = CookieService.get(name);

  if (!list) {
    return;
  }

  if (list) {
    const ids = list.split("%");

    if (!ids.includes(id)) {
      return;
    }

    const newIds = ids
      .filter(it => it !== id)
      .join("%");

    CookieService.set(name, newIds)
  }
}
