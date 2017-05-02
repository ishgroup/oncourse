import {combineEpics} from "redux-observable";
import {Observable} from "rxjs";
import {Store} from "redux";
import {FULFILLED, IshActions} from "../../constants/IshActions";
import {normalize} from "normalizr";
import uniq from "lodash/uniq";
import {
  classesListSchema,
  classesSchema,
  productsListSchema,
  productsSchema,
  promotionsSchema
} from "../../schema";
import {Injector} from "../../injector";
import {PromotionParams} from "../../model/web/PromotionParams";
import {IshState} from "../../services/IshState";
import {ContactParams} from "../../model/web/ContactParams";
import {mapError, mapPayload} from "../../epics/epicsUtils";

const {
  courseClassesApi,
  productsApi,
  promotionApi,
  contactApi,
  mergeService,
  legacySyncStorage
} = Injector.of();

export const webEpics = combineEpics(
  createCoursesEpic(IshActions.REQUEST_COURSE_CLASS),
  createUpdateCoursesEpic(IshActions.REQUEST_COURSE_CLASS),
  createProductsEpic(IshActions.REQUEST_PRODUCT),
  createUpdateProductsEpic(IshActions.REQUEST_PRODUCT),
  createPromotionsEpic(IshActions.REQUEST_PROMOTION),
  createContactEpic(IshActions.REQUEST_CONTACT),
  createSyncCartRequestEpic(),
  createSyncCartEpic(),
  createLegacySyncEpic(),
  createAddClassToCartEpic(IshActions.ADD_CLASS_TO_CART),
  createRemoveClassFromCartEpic(IshActions.REMOVE_CLASS_FROM_CART),
  createAddProductToCartEpic(IshActions.ADD_PRODUCT_TO_CART),
  createRemoveProductFromCartEpic(IshActions.REMOVE_PRODUCT_FROM_CART),
  createAddPromotionToCartEpic(IshActions.ADD_PROMOTION_TO_CART),
  createRemovePromotionFromCartEpic(IshActions.REMOVE_PROMOTION_FROM_CART)
);

function createCoursesEpic(actionType) {
  return (action$, store: Store<IshState>) => action$
    .ofType(actionType)
    .bufferTime(100) // batch actions
    .filter(actions => actions.length)
    .mergeMap(actions => {
      const ids: string[] = actions.map(action => action.payload);
      return Observable
        .fromPromise(courseClassesApi.getCourseClasses({
          courseClassesIds: uniq(ids),
          contact: createContactParams(store.getState()),
          promotions: createPromotionParams(store.getState())
        }))
        .map(payload => normalize(payload, classesListSchema))
        .map(mapPayload(actionType))
        .catch(mapError(actionType));
    });
}

function createProductsEpic(actionType) {
  return (action$, store: Store<IshState>) => action$
    .ofType(actionType)
    .bufferTime(100) // batch actions
    .filter(actions => actions.length)
    .mergeMap(actions => {
      const ids: string[] = actions.map(action => action.payload);
      return Observable
        .fromPromise(productsApi.getProducts({
          productsIds: uniq(ids),
          contact: createContactParams(store.getState()),
          promotions: createPromotionParams(store.getState())
        }))
        .map(payload => normalize(payload, productsListSchema))
        .map(mapPayload(actionType))
        .catch(mapError(actionType));
    });
}

function createPromotionsEpic(actionType) {
  return (action$) => action$
    .ofType(actionType)
    .mergeMap(action => Observable
      .fromPromise(promotionApi.getPromotion(action.code))
      .map(mapPayload(actionType))
      .catch(mapError(actionType))
    );
}

function createContactEpic(actionType) {
  return (action$) => action$
    .ofType(actionType)
    .mergeMap(action => Observable
      .fromPromise(contactApi.getContact(action.id))
      .map(mapPayload(actionType))
      .catch(mapError(actionType))
    );
}

function createUpdateCoursesEpic(actionType) {
  return (action$, store: Store<IshState>) => action$
    .ofType(
      FULFILLED(IshActions.REQUEST_CONTACT),
      FULFILLED(IshActions.ADD_PROMOTION_TO_CART),
      FULFILLED(IshActions.REMOVE_PROMOTION_FROM_CART)
    )
    .filter(action => store.getState().courses.result.length)
    .mergeMap(action => {
      const ids = store.getState().courses.result;

      return Observable
        .fromPromise(courseClassesApi.getCourseClasses({
          courseClassesIds: ids,
          contact: createContactParams(store.getState()),
          promotions: createPromotionParams(store.getState())
        }))
        .map(payload => normalize(payload, classesListSchema))
        .map(mapPayload(actionType))
        .catch(mapError(actionType));
    });
}

function createUpdateProductsEpic(actionType) {
  return (action$, store: Store<IshState>) => action$
    .ofType(
      FULFILLED(IshActions.REQUEST_CONTACT),
      FULFILLED(IshActions.ADD_PROMOTION_TO_CART),
      FULFILLED(IshActions.REMOVE_PROMOTION_FROM_CART)
    )
    .filter(action => store.getState().products.result.length)
    .mergeMap(action => {
      const ids = store.getState().products.result;

      return Observable
        .fromPromise(productsApi.getProducts({
          productsIds: ids,
          contact: createContactParams(store.getState()),
          promotions: createPromotionParams(store.getState())
        }))
        .map(payload => normalize(payload, productsListSchema))
        .map(mapPayload(actionType))
        .catch(mapError(actionType));
    });
}

function createAddClassToCartEpic(actionType) {
  return (action$, store: Store<IshState>) => action$
    .ofType(actionType)
    .map(action => ({
      type: FULFILLED(actionType),
      payload: normalize(store.getState().courses.entities[action.payload.id], classesSchema)
    }));
}

function createRemoveClassFromCartEpic(actionType) {
  return (action$, store: Store<IshState>) => action$
    .ofType(actionType)
    .map(action => ({
      type: FULFILLED(actionType),
      payload: normalize(action.payload, classesSchema)
    }));
}

function createAddProductToCartEpic(actionType) {
  return (action$, store: Store<IshState>) => action$
    .ofType(actionType)
    .map(action => ({
      type: FULFILLED(actionType),
      payload: normalize(store.getState().products.entities[action.payload.id], productsSchema)
    }));
}

function createRemoveProductFromCartEpic(actionType) {
  return (action$, store: Store<IshState>) => action$
    .ofType(actionType)
    .map(action => ({
      type: FULFILLED(actionType),
      payload: normalize(action.payload, productsSchema)
    }));
}

function createAddPromotionToCartEpic(actionType) {
  return (action$, store: Store<IshState>) => action$
    .ofType(actionType)
    .mergeMap(action => {
      return Observable
        .fromPromise(promotionApi.getPromotion(action.payload))
        .map(payload => normalize(payload, promotionsSchema))
        .map(mapPayload(actionType))
        .catch(mapError(actionType));
    });
}

function createRemovePromotionFromCartEpic(actionType) {
  return (action$, store: Store<IshState>) => action$
    .ofType(actionType)
    .map(action => ({
      type: FULFILLED(actionType),
      payload: normalize(action.payload, promotionsSchema)
    }));
}

function createSyncCartRequestEpic() {
  return (action$, store: Store<IshState>) => action$
    .ofType(
      FULFILLED(IshActions.ADD_CLASS_TO_CART),
      FULFILLED(IshActions.REMOVE_CLASS_FROM_CART),
      FULFILLED(IshActions.ADD_PRODUCT_TO_CART),
      FULFILLED(IshActions.REMOVE_PRODUCT_FROM_CART),
      FULFILLED(IshActions.ADD_PROMOTION_TO_CART),
      FULFILLED(IshActions.REMOVE_PROMOTION_FROM_CART),
      FULFILLED(IshActions.REQUEST_PROMOTION),
      FULFILLED(IshActions.REQUEST_CONTACT)
    )
    .map(action => ({
      timestamp: new Date().getTime(),
      ...action
    }))
    .bufferTime(100)
    .filter(actions => actions.length)
    .map(actions => ({
      type: IshActions.SYNC_CART,
      payload: actions
    }));
}

function createSyncCartEpic() {
  return (action$) => action$
    .ofType(IshActions.SYNC_CART)
    .mergeMap(action => Observable.from(mergeService.merge(action)));
}

function createLegacySyncEpic() {
  return (action$) => action$
    .ofType(
      FULFILLED(IshActions.ADD_CLASS_TO_CART),
      FULFILLED(IshActions.REMOVE_CLASS_FROM_CART),
      FULFILLED(IshActions.ADD_PRODUCT_TO_CART),
      FULFILLED(IshActions.REMOVE_PRODUCT_FROM_CART),
    )
    .do(legacySyncStorage.sync)
    .filter(() => false);
}

function createContactParams(state: IshState): ContactParams {
  const contact = state.cart.contact;

  if (!contact) {
    return null;
  }

  return {
    id: contact.id
  };
}

function createPromotionParams(state: IshState): PromotionParams[] {
  return state.cart.promotions.result.map(promotionId => ({
    id: promotionId
  }));
}
