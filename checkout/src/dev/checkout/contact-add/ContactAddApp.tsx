import * as React from "react";
import * as ReactDOM from "react-dom";

import {Provider} from "react-redux";
import ContactAddForm from "enrol/containers/contact-add/ContactAddForm";
import {CreateStore} from "CreateStore";
import {Progress, Tab} from "enrol/components/Progress";
import {Messages} from "enrol/containers/Functions";

const store = CreateStore();

const render = () => ReactDOM.render(
  <Provider store={store}>
    <div id="checkout" className="payments">
      <Progress onChange={(t) => {console.log(t)}}
        model = {{
          active: Tab.Details,
          disabled: [Tab.Summary, Tab.Payment],
        }}
      />
      <Messages/>
      <ContactAddForm/>
    </div>
  </Provider>,
  document.getElementById("root")
);

render();