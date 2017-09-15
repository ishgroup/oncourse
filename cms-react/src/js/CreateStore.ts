import {Store} from "react-redux";
import {createEpicMiddleware} from "redux-observable";
import {createLogger} from "redux-logger";
import localforage from "localforage";
import {applyMiddleware, compose, createStore, StoreEnhancer} from "redux";

import {EpicRoot} from "./EpicRoot";
import {autoRehydrate, getStoredState, OnComplete, persistStore} from "redux-persist";
import {combinedReducers} from "./reducers";
import {EnvironmentConstants} from "./config/EnvironmentConstants";


const getMiddleware = (): StoreEnhancer<any> => {
  const logger = createLogger({
    collapsed: true,
  });

  /**
   * Split middlewares which we using in development and in production.
   */
  if (process.env.NODE_ENV === EnvironmentConstants.development) {
    return applyMiddleware(createEpicMiddleware(EpicRoot), logger);
  } else {
    return applyMiddleware(createEpicMiddleware(EpicRoot));
  }
};

export const CreateStore = (): Store<any> => {
  const store: Store<any> = createStore(
    combinedReducers,
    <any>compose(getMiddleware(), autoRehydrate()),
  ) as Store<any>;
  return store;
};

export const RestoreState = (store: Store<any>, onComplete: OnComplete<any>): void => {
  persistStore(store, {storage: localforage, blacklist: []}, onComplete);
};

