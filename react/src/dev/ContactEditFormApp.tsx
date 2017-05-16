import * as React from "react";
import * as ReactDOM from "react-dom";

import {connect, Provider} from "react-redux";
import ContactEditForm from "../js/enrol/containers/contact/ContactEditForm";
import {CreateStore} from "../js/CreateStore";
import Messages from "../js/enrol/components/Messages";
import {Progress, Tab} from "../js/enrol/components/Progress";
import {ContactAdd, FieldsLoadRequest} from "../js/enrol/actions/Actions";
import {normalize} from "normalizr";
import {contactsSchema} from "../js/schema";
import {FieldSet} from "../js/model/web/FieldSet";
import { Values } from 'redux-form-website-template';

// const reducer = (state: any = {}): any => {
//   return state;
// };
//
// const store = createStore(combineReducers({state: reducer, form: formReducer}));

const store = CreateStore();

//add contact
store.dispatch({
  type: ContactAdd,
  payload: normalize({
    id: 1001,
    firstName: 'Andrey',
    lastName: 'Koyro',
    email: 'pervoliner@gmail.com',
    fieldSet: FieldSet.enrolment
  }, contactsSchema)
});

store.dispatch({
  type: FieldsLoadRequest
});


const MessagesRedux = connect((state) => {
  return {error: state.checkout.error}
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
      <ContactEditForm/>
      <Values form="EditContactForm" />
    </div>
  </Provider>,
  document.getElementById("root")
);

render();