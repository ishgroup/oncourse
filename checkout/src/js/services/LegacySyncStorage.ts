import {IshAction} from "../actions/IshAction";
import {CourseClassCartState, ProductCartState} from "./IshState";
import {FULFILLED} from "../common/actions/ActionUtils";
import {Actions} from "../web/actions/Actions";
import {Level, Logger, LogMessage} from "./Logger";
import {getCookie, setCookie} from "../common/utils/Cookie";

class KEYS {
  static shortlist: string = "shortlist";
  static productShortList: string = "productShortList";
  static promotions: string = "promotions";

}

export class LegacySyncStorage {
  addCourse(id: string) {
    add(id, KEYS.shortlist);
  }

  replaceCourse(ids: string[]) {
    replace(ids[0], ids[1], KEYS.shortlist);
  }

  removeCourse(id: string) {
    remove(id, KEYS.shortlist);
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

function getReplaceIdS(action: any) {
  return [action.payload.replace.result,action.payload.replacement.result];
}

function replace(id1: string, id2: string, name: string) {
  const list = getCookie(name);

  if (list) {
    const ids = list.split("%");

    if (ids.includes(id2)) {
      return;
    }

    const newIds = [...ids, id2].filter(it => it !== id1).join("%");

    setCookie(name, newIds);
  }
}

function add(id: string, name: string) {
  const list = getCookie(name);

  if (list) {
    const ids = list.split("%");

    if (ids.includes(id)) {
      return;
    }

    const newIds = [...ids, id].join("%");

    setCookie(name, newIds);
  } else {
    setCookie(name, id);
  }
}

function remove(id: string, name: string) {
  const list = getCookie(name);

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

    setCookie(name, newIds);
  }
}
