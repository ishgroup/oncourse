import {Store} from "react-redux";
import {createEpicMiddleware} from "redux-observable";
import {createLogger} from "redux-logger";
import {applyMiddleware, createStore, GenericStoreEnhancer} from "redux";
import {IshState} from "./services/IshState";
import {rootEpic} from "./rootEpic";
import {combinedReducers} from "./reducers/reducers";
import {EnvironmentConstants} from "./config/EnvironmentConstants";
import {Actions} from "./web/actions/Actions";

export function configureStore(): Store<IshState> {
  const store = createStore(
    combinedReducers,
    getMiddlewares()
  );

  function getMiddlewares(): GenericStoreEnhancer {
    const logger = createLogger({
      collapsed: true
    });

    /**
     * Split middlewares which we using in development and in production.
     */
    if (process.env.NODE_ENV === EnvironmentConstants.development) {
      return applyMiddleware(createEpicMiddleware(rootEpic), logger);
    } else {
      return applyMiddleware(createEpicMiddleware(rootEpic));
    }
  }

  // Trigger syncing state with LocalStorage
  store.dispatch({
    type: Actions.SYNC_CART,
    payload: []
  });

  return store;
}
