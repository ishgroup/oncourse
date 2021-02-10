import { applyMiddleware, compose, createStore, StoreEnhancer, combineReducers } from 'redux';
import { Store } from "react-redux";
import { createLogger } from "redux-logger";
import { createEpicMiddleware } from "redux-observable";
import { createCollegeReducer } from "./reducers";
import { EpicRoot } from "./epics";

const combinedReducers = combineReducers({
  creatingCollege: createCollegeReducer
});

export const epicMiddleware = createEpicMiddleware();

const getMiddleware = (): StoreEnhancer<any> => {
  const logger = createLogger({
    collapsed: true,
  });

  if (process.env.NODE_ENV ==="development") {
    return applyMiddleware(epicMiddleware, logger);
  } else {
    return applyMiddleware(epicMiddleware);
  }
};

export const CreateStore = (): Store<any> => {
  const store: Store<any> = createStore(
    combinedReducers,
    compose(getMiddleware()),
  ) as Store<any>;

  epicMiddleware.run(EpicRoot);

  return store;
};
