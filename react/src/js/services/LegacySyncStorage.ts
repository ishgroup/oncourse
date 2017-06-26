import {CookieService} from "./CookieService";
import {IshAction} from "../actions/IshAction";
import {CourseClassCartState, ProductCartState} from "./IshState";
import {FULFILLED} from "../common/actions/ActionUtils";
import {Actions} from "../web/actions/Actions";
import {Level, Logger, LogMessage} from "./Logger";

class KEYS {
  static shorlist: string = "shorlist";
  static productShortList: string = "productShortList";
  static promotions: string = "promotions";

}

export class LegacySyncStorage {
  addCourse(id: string) {
    add(id, KEYS.shorlist);
  }

  removeCourse(id: string) {
    remove(id, KEYS.shorlist);
  }

  addProduct(id: string) {
    add(id, KEYS.productShortList);
  }

  removeProduct(id: string) {
    remove(id, KEYS.productShortList);
  }

  addPromotion(id: string) {
    add(id, KEYS.promotions);
  }

  removePromotion(id: string) {
    remove(id, KEYS.promotions);
  }

  sync = (action: IshAction<CourseClassCartState | ProductCartState>) => {
    switch (action.type) {
      case FULFILLED(Actions.ADD_CLASS_TO_CART):
        return this.addCourse(getId(action));
      case FULFILLED(Actions.REMOVE_CLASS_FROM_CART):
        return this.removeCourse(getId(action));
      case FULFILLED(Actions.ADD_PRODUCT_TO_CART):
        return this.addProduct(getId(action));
      case FULFILLED(Actions.REMOVE_PRODUCT_FROM_CART):
        return this.removeProduct(getId(action));
      case FULFILLED(Actions.ADD_PROMOTION_TO_CART):
        return this.addPromotion(getId(action));
      case FULFILLED(Actions.REMOVE_PROMOTION_FROM_CART):
        return this.removePromotion(getId(action));
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

    CookieService.set(name, newIds);
  } else {
    CookieService.set(name, id);
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

    CookieService.set(name, newIds);
  }
}
