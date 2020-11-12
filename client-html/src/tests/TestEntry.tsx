import * as React from "react";
import { Provider } from "react-redux";
import { configure } from "enzyme";
import Adapter from "enzyme-adapter-react-16";
import { StateObservable } from "redux-observable";
import { Subject } from "rxjs";
import TestStore from "../js/constants/Store";
import RootComponent from "../js/RootComponent";
import { initMockDB } from "../dev/mock/MockAdapter";

// Configuring virtual rendering library and mockedAPI class

export const mockedAPI: any = initMockDB();

export const store = new StateObservable(new Subject(), TestStore.getState());

configure({ adapter: new Adapter() });

export const TestEntry = ({ children }) => (
  <Provider store={TestStore as any}>
    <RootComponent>{children}</RootComponent>
  </Provider>
);
