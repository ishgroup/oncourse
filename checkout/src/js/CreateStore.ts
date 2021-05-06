import {createEpicMiddleware} from "redux-observable";
import {createLogger} from "redux-logger";
import * as localforage from "localforage";
import {ConfigConstants} from "./config/ConfigConstants";
import {applyMiddleware, compose, createStore, StoreEnhancer, Store} from "redux";
import {IshState} from "./services/IshState";
import {EpicRoot} from "./EpicRoot";
import {combinedReducers} from "./reducers/reducers";
import {EnvironmentConstants} from "./config/EnvironmentConstants";
import {autoRehydrate, OnComplete, persistStore} from "redux-persist";
import {syncCartStore} from "./SyncCartStore";
import {createBlacklistFilter} from 'redux-persist-transform-filter';

localforage.ready().catch(e => {
  console.error(e);
});

const getMiddleware = (): StoreEnhancer<IshState> => {
  const logger = createLogger({
    collapsed: true,
  });

  /**
   * Split middlewares which we using in development and in production.
   */
  if (process.env.NODE_ENV === EnvironmentConstants.development) {
    return applyMiddleware(createEpicMiddleware(EpicRoot), logger) as any;
  } else {
    return applyMiddleware(createEpicMiddleware(EpicRoot)) as any;
  }
};

export const CreateStore = (): Store<IshState> => {
  const store: Store<IshState> = createStore(
    combinedReducers,
    <StoreEnhancer<IshState>>compose(getMiddleware(), autoRehydrate()),
  ) as Store<IshState>;

  syncCartStore(store);

  return store;
};

// Clear only Payment form values on store persist
const formBlacklistFilter = createBlacklistFilter(
  'form',
  ['PaymentForm'],
);

export const RestoreState = (store: Store<IshState>, onComplete: OnComplete<any>): void => {
  const persistStoreWrapper = () => {
    persistStore(store,
      {
        storage: localforage,
        blacklist: ["phase", "page", "contactAddProcess", "config"],
        transforms: [formBlacklistFilter],
      },
                 onComplete);
    localforage.setItem('appVersion', ConfigConstants.APP_VERSION);
  };


  localforage.getItem('appVersion').then(val => {
    if (val && val != ConfigConstants.APP_VERSION) {
      localforage.clear().then(() => {
        persistStoreWrapper();
      });
    } else {
      persistStoreWrapper();
    }
  });

};

