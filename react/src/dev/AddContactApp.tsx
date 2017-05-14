import * as React from "react";
import * as ReactDOM from "react-dom";

import {connect, Provider} from "react-redux";
import ContactAddForm from "../js/enrol/containers/contact/ContactAddForm";
import {CreateStore} from "../js/CreateStore";
import Messages from "../js/enrol/components/Messages";
import {Progress, Tab} from "../js/enrol/components/Progress";

// const reducer = (state: any = {}): any => {
//   return state;
// };
//
// const store = createStore(combineReducers({state: reducer, form: formReducer}));

const store = CreateStore();


const MessagesRedux = connect((state) => {
  return {error: state.enrol.error}
}, (dispatch) => {
  return {}
})(Messages);

const render = () => ReactDOM.render(
  <Provider store={store}>
    <div id="checkout" className="payments">
      <Progress onChange={(t) => {console.log(t)}}
        model = {{
          active: Tab.Details,
          disabled: [Tab.Summary, Tab.Payment]
        }}
      />
      <MessagesRedux/>
      <ContactAddForm/>
    </div>
  </Provider>,
  document.getElementById("root")
);

render();