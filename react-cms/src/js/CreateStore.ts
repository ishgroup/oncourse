import {Store} from "react-redux";
import {createEpicMiddleware} from "redux-observable";
import {createLogger} from "redux-logger";
import localforage from "localforage";
import {applyMiddleware, compose, createStore, GenericStoreEnhancer, StoreEnhancer, combineReducers} from "redux";
import {EpicRoot} from "./EpicRoot";
import {autoRehydrate, getStoredState, OnComplete, persistStore} from "redux-persist";

const getMiddleware = (): StoreEnhancer<any> => {
  const logger = createLogger({
    collapsed: true,
  });

  /**
   * Split middlewares which we using in development and in production.
   */
  return applyMiddleware(createEpicMiddleware(EpicRoot), logger);
};

export const CreateStore = (): Store<any> => {
  const store: Store<any> = createStore(
    combineReducers({

    }),
    <any>compose(getMiddleware(), autoRehydrate()),
  ) as Store<any>;
  return store;
};

