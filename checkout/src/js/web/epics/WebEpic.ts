import {combineEpics} from "redux-observable";
import {Observable} from "rxjs";
import {Store} from "redux";
import {chunk} from "lodash";
import {FULFILLED} from "../../common/actions/ActionUtils";
import {Actions} from "../actions/Actions";
import * as ContactAddActions from "../../enrol/containers/contact-add/actions/Actions";
import {normalize} from "normalizr";
import uniq from "lodash/uniq";
import {
  ClassesListSchema,
  ClassesSchema, InactiveCoursesListSchema,
  ProductsListSchema,
  ProductsSchema,
  PromotionsSchema, WaitingCoursesListSchema, WaitingCoursesSchema,
} from "../../NormalizeSchema";
import {Injector} from "../../injector";
import {PromotionParams, ContactParams} from "../../model";
import {IshState} from "../../services/IshState";
import {mapError, mapPayload} from "../../common/epics/EpicUtils";
import {rewriteContactNodeToState} from "../../enrol/containers/summary/actions/Actions";

const {
  courseClassesApi,
  productsApi,
  promotionApi,
  contactApi,
  mergeService,
  legacySyncStorage,
} = Injector.of();

export const WebEpic = combineEpics(
  createCoursesEpic(),
  createWaitingCoursesEpic(),
  createInactiveCoursesEpic(),
  createUpdateCoursesEpic(),
  createProductsEpic(),
  createUpdateProductsEpic(),
  createPromotionsEpic(),
  createContactEpic(),
  createSyncCartRequestEpic(),
  createSyncCartEpic(),
  createLegacySyncEpic(),
  createAddClassToCartEpic(),
  createAddClassToSummaryEpic(),
  createRemoveClassFromCartEpic(),
  createAddProductToCartEpic(),
  createRemoveProductFromCartEpic(),
  createAddPromotionToCartEpic(),
  createRemovePromotionFromCartEpic(),
  createAddWaitingCourseToCartEpic(),
  createRemoveWaitingCourseFromCartEpic()
);

function createCoursesEpic() {
  return (action$, store: Store<IshState>) => action$
    .ofType(Actions.REQUEST_COURSE_CLASS)
    .bufferTime(100) // batch actions
    .filter(actions => actions.length)
    .mergeMap(actions => {
      const ids: string[] = uniq(actions.map(action => action.payload));
      const chunkIds = chunk(ids, 25);

      const courseStream = Observable.from(chunkIds).mergeMap(groupedIds =>
        Observable
          .defer(() => courseClassesApi.getCourseClasses({
            courseClassesIds: groupedIds,
            contact: createContactParams(store.getState()),
            promotions: createPromotionParams(store.getState()),
          }))
          .retry(2) // Retry two times if request has been rejected
          .map(payload => normalize(payload, ClassesListSchema))
          .map(mapPayload(Actions.REQUEST_COURSE_CLASS))
          .catch(mapError(Actions.REQUEST_COURSE_CLASS)));

      const urlParamContact = checkUrlParamContact();

      return urlParamContact ?
        Observable.concat(
          Observable
            .fromPromise(contactApi.getContact(urlParamContact))
            .map(mapPayload(Actions.REQUEST_CONTACT))
            .catch(mapError(Actions.REQUEST_CONTACT)),
          courseStream) : courseStream;
    });
}

function createWaitingCoursesEpic() {
  return (action$, store: Store<IshState>) => action$
    .ofType(Actions.REQUEST_WAITING_COURSE)
    .bufferTime(100) // batch actions
    .filter(actions => actions.length)
    .mergeMap(actions => {
      const ids: string[] = actions.map(action => action.payload);
      return Observable
        .defer(() => courseClassesApi.getCourses({
          coursesIds: uniq(ids),
        }))
        .retry(2) // Retry to times if request has been rejected
        .map(payload => normalize(payload, WaitingCoursesListSchema))
        .map(mapPayload(Actions.REQUEST_WAITING_COURSE))
        .catch(mapError(Actions.REQUEST_WAITING_COURSE));
    });
}

function createInactiveCoursesEpic() {
  return (action$, store: Store<IshState>) => action$
    .ofType(Actions.REQUEST_INACTIVE_COURSE)
    .bufferTime(100) // batch actions
    .filter(actions => actions.length)
    .mergeMap(actions => {
      const ids: string[] = actions.map(action => action.payload);
      return Observable
        .defer(() => courseClassesApi.getCourses({
          coursesIds: uniq(ids),
        }))
        .retry(2) // Retry to times if request has been rejected
        .map(payload => normalize(payload, InactiveCoursesListSchema))
        .map(mapPayload(Actions.REQUEST_INACTIVE_COURSE))
        .catch(mapError(Actions.REQUEST_INACTIVE_COURSE));
    });
}

function createProductsEpic() {
  return (action$, store: Store<IshState>) => action$
    .ofType(Actions.REQUEST_PRODUCT)
    .bufferTime(100) // batch actions
    .filter(actions => actions.length)
    .mergeMap(actions => {
      const ids: string[] = actions.map(action => action.payload);
      return Observable
        .fromPromise(productsApi.getProducts({
          productsIds: uniq(ids),
          contact: createContactParams(store.getState()),
          promotions: createPromotionParams(store.getState()),
        }))
        .map(payload => normalize(payload, ProductsListSchema))
        .map(mapPayload(Actions.REQUEST_PRODUCT))
        .catch(mapError(Actions.REQUEST_PRODUCT));
    });
}

function createPromotionsEpic() {
  return action$ => action$
    .ofType(Actions.REQUEST_PROMOTION)
    .mergeMap(action => Observable
      .fromPromise(promotionApi.getPromotion(action.code))
      .map(mapPayload(Actions.REQUEST_PROMOTION))
      .catch(mapError(Actions.REQUEST_PROMOTION)),
    );
}

function createContactEpic() {
  return action$ => action$
    .ofType(Actions.REQUEST_CONTACT)
    .mergeMap(action => Observable
      .fromPromise(contactApi.getContact(action.id))
      .map(mapPayload(Actions.REQUEST_CONTACT))
      .catch(mapError(Actions.REQUEST_CONTACT)),
    );
}

function createUpdateCoursesEpic() {
  return (action$, store: Store<IshState>) => action$
    .ofType(
      FULFILLED(Actions.REQUEST_CONTACT),
      FULFILLED(Actions.ADD_PROMOTION_TO_CART),
      FULFILLED(Actions.REMOVE_PROMOTION_FROM_CART),
    )
    .filter(action => store.getState().courses.result.length)
    .mergeMap(action => {
      const ids = store.getState().courses.result;

      return Observable
        .fromPromise(courseClassesApi.getCourseClasses({
          courseClassesIds: ids,
          contact: createContactParams(store.getState()),
          promotions: createPromotionParams(store.getState()),
        }))
        .map(payload => normalize(payload, ClassesListSchema))
        .map(mapPayload(Actions.REQUEST_COURSE_CLASS))
        .catch(mapError(Actions.REQUEST_COURSE_CLASS));
    });
}

function createUpdateProductsEpic() {
  return (action$, store: Store<IshState>) => action$
    .ofType(
      FULFILLED(Actions.REQUEST_CONTACT),
      FULFILLED(Actions.ADD_PROMOTION_TO_CART),
      FULFILLED(Actions.REMOVE_PROMOTION_FROM_CART),
    )
    .filter(action => store.getState().products.result.length)
    .mergeMap(action => {
      const ids = store.getState().products.result;

      return Observable
        .fromPromise(productsApi.getProducts({
          productsIds: ids,
          contact: createContactParams(store.getState()),
          promotions: createPromotionParams(store.getState()),
        }))
        .map(payload => normalize(payload, ProductsListSchema))
        .map(mapPayload(Actions.REQUEST_PRODUCT))
        .catch(mapError(Actions.REQUEST_PRODUCT));
    });
}

function createAddClassToCartEpic() {
  return (action$, store: Store<IshState>) => action$
    .ofType(Actions.ADD_CLASS_TO_CART)
    .map(action => ({
      type: FULFILLED(Actions.ADD_CLASS_TO_CART),
      payload: normalize(store.getState().courses.entities[action.payload.id] || {}, ClassesSchema),
    }));
}

function createAddClassToSummaryEpic() {
  return (action$, store: Store<IshState>) => action$
    .ofType(Actions.ADD_CLASS_TO_CART)
    .mergeMap(action => {
      const state = store.getState();
      const contacts = state.checkout.summary.result;
      const classItem = state.courses.entities[action.payload.id]
      return contacts.map(node => {
        return rewriteContactNodeToState({
         ...state.checkout.summary.entities.contactNodes[node],
         ...classItem.isAllowByApplication
          ? {applications: [
               ...state.checkout.summary.entities.contactNodes[node].applications,
               {
                 contactId: node,
                 classId: classItem.id,
                 allowRemove: null,
                 courseId: null,
                 errors: [],
                 fieldHeadings: [],
                 price: {...classItem.price},
                 relatedClassId: null,
                 relatedProductId: null,
                 selected: true,
                 warnings: []
               }
             ]}
          : {enrolments: [
               ...state.checkout.summary.entities.contactNodes[node].enrolments,
               {
                 contactId: node,
                 classId: classItem.id,
                 allowRemove: null,
                 courseId: null,
                 errors: [],
                 fieldHeadings: [],
                 price: {...classItem.price},
                 relatedClassId: null,
                 relatedProductId: null,
                 selected: true,
                 warnings: []
               }
             ]}
        } as any)
      })
    });
}

function createRemoveClassFromCartEpic() {
  return (action$, store: Store<IshState>) => action$
    .ofType(Actions.REMOVE_CLASS_FROM_CART)
    .map(action => ({
      type: FULFILLED(Actions.REMOVE_CLASS_FROM_CART),
      payload: normalize(action.payload, ClassesSchema),
    }));
}

function createAddWaitingCourseToCartEpic() {
  return (action$, store: Store<IshState>) => action$
    .ofType(Actions.ADD_WAITING_COURSE_TO_CART)
    .map(action => ({
      type: FULFILLED(Actions.ADD_WAITING_COURSE_TO_CART),
      payload: normalize(store.getState().waitingCourses.entities[action.payload.id], WaitingCoursesSchema),
    }));
}

function createAddProductToCartEpic() {
  return (action$, store: Store<IshState>) => action$
    .ofType(Actions.ADD_PRODUCT_TO_CART)
    .map(action => {
      return {
        type: FULFILLED(Actions.ADD_PRODUCT_TO_CART),
        payload: normalize(store.getState().products.entities[action.payload.id], ProductsSchema),
      };
    });
}

function createRemoveProductFromCartEpic() {
  return (action$, store: Store<IshState>) => action$
    .ofType(Actions.REMOVE_PRODUCT_FROM_CART)
    .map(action => ({
      type: FULFILLED(Actions.REMOVE_PRODUCT_FROM_CART),
      payload: normalize(action.payload, ProductsSchema),
    }));
}

function createRemoveWaitingCourseFromCartEpic() {
  return (action$, store: Store<IshState>) => action$
    .ofType(Actions.REMOVE_WAITING_COURSE_FROM_CART)
    .map(action => ({
      type: FULFILLED(Actions.REMOVE_WAITING_COURSE_FROM_CART),
      payload: normalize(action.payload, WaitingCoursesSchema),
    }));
}

function createAddPromotionToCartEpic() {
  return (action$, store: Store<IshState>) => action$
    .ofType(Actions.ADD_PROMOTION_TO_CART)
    .mergeMap(action => {
      return Observable
        .fromPromise(promotionApi.getPromotion(action.payload))
        .map(payload => normalize(payload, PromotionsSchema))
        .map(mapPayload(Actions.ADD_PROMOTION_TO_CART))
        .catch(mapError(Actions.ADD_PROMOTION_TO_CART));
    });
}

function createRemovePromotionFromCartEpic() {
  return (action$, store: Store<IshState>) => action$
    .ofType(Actions.REMOVE_PROMOTION_FROM_CART)
    .map(action => ({
      type: FULFILLED(Actions.REMOVE_PROMOTION_FROM_CART),
      payload: normalize(action.payload, PromotionsSchema),
    }));
}

function createSyncCartRequestEpic() {
  return (action$, store: Store<IshState>) => action$
    .ofType(
      FULFILLED(Actions.ADD_CLASS_TO_CART),
      FULFILLED(Actions.REMOVE_CLASS_FROM_CART),
      FULFILLED(Actions.ADD_PRODUCT_TO_CART),
      FULFILLED(Actions.REMOVE_PRODUCT_FROM_CART),
      FULFILLED(Actions.ADD_PROMOTION_TO_CART),
      FULFILLED(Actions.REMOVE_PROMOTION_FROM_CART),
      FULFILLED(Actions.REQUEST_PROMOTION),
      FULFILLED(Actions.REQUEST_CONTACT),
      Actions,
      ContactAddActions.ADD_CONTACT_TO_STATE,
    )
    .map(action => ({
      timestamp: new Date().getTime(),
      ...action,
    }))
    .bufferTime(100)
    .filter(actions => actions.length)
    .map(actions => ({
      type: Actions.SYNC_CART,
      payload: actions,
    }));
}

function createSyncCartEpic() {
  return action$ => action$
    .ofType(Actions.SYNC_CART)
    .mergeMap(action => Observable.from(mergeService.merge(action)));
}

function createLegacySyncEpic() {
  return action$ => action$
    .ofType(
      FULFILLED(Actions.ADD_CLASS_TO_CART),
      FULFILLED(Actions.REMOVE_CLASS_FROM_CART),
      FULFILLED(Actions.ADD_PRODUCT_TO_CART),
      FULFILLED(Actions.REMOVE_PRODUCT_FROM_CART),
    )
    .do(legacySyncStorage.sync)
    .filter(() => false);
}

function checkUrlParamContact() {
  const urlObj = new URL(window.location.href);
  return urlObj.searchParams.get("student");
}

function createContactParams(state: IshState): ContactParams {
  const contact = state.cart.contact;

  if (!contact) {
    return null;
  }

  return {
    id: contact.id,
  };
}

function createPromotionParams(state: IshState): PromotionParams[] {
  return state.cart.promotions.result.map(promotionId => ({
    id: promotionId,
  }));
}
