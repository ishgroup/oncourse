import {createEpicMiddleware} from "redux-observable";
import {createLogger} from "redux-logger";
import {ConfigConstants} from "./config/ConfigConstants";
import {applyMiddleware, compose, createStore, StoreEnhancer, Store} from "redux";
import {IshState} from "./services/IshState";
import {EpicRoot} from "./EpicRoot";
import {combinedReducers} from "./reducers/reducers";
import {EnvironmentConstants} from "./config/EnvironmentConstants";
import {autoRehydrate, OnComplete, persistStore} from "redux-persist";
import {syncCartStore} from "./SyncCartStore";
import {createBlacklistFilter} from 'redux-persist-transform-filter';
import { localForage } from "./constants/LocalForage";

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
        storage: localForage,
        blacklist: ["phase", "page", "contactAddProcess", "config"],
        transforms: [formBlacklistFilter],
      },
                 onComplete);
    localForage.setItem('appVersion', ConfigConstants.APP_VERSION).catch(e => {
      console.error(e);
    });
  };


  localForage.getItem('appVersion').then(val => {
    if (val && val != ConfigConstants.APP_VERSION) {
      localForage.clear().then(() => {
        persistStoreWrapper();
      }).catch(e => {
        console.error(e);
      });
    } else {
      persistStoreWrapper();
    }
  }).catch(e => {
    console.error(e);
  });

};

