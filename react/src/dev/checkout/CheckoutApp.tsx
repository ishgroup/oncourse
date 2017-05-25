import * as React from "react";
import * as ReactDOM from "react-dom";

import {Provider} from "react-redux";
import {CreateStore, RestoreState} from "../../js/CreateStore";
import Checkout from "../../js/enrol/containers/Checkout";
import {Actions} from "../../js/web/actions/Actions";
import localForage from "localforage";
import {Injector} from "../../js/injector";
import {ContactApiStub} from "../../js/httpStub/ContactApiStub";
import {MockConfig} from "../../js/httpStub/mocks/MockConfig";
import {Contact} from "../../js/model/web/Contact";
import ContactInfo from "../../js/enrol/components/ContactInfo";

const store = CreateStore();
RestoreState(store, () => render());

const render = () => ReactDOM.render(
  <Provider store={store}>
    <div>
      <div id="checkout" className="payments">
        <Checkout/>
      </div>
      <Controls/>
    </div>
  </Provider>,
  document.getElementById("root")
);


const {
  contactApi
} = Injector.of();

const contactApiMock = contactApi as ContactApiStub;
const config:MockConfig = contactApiMock.config;

class Controls extends React.Component<any, any> {

  private resetLocalForage = () => {
    localForage.clear();
  };

  private loadCourseClasses = () => {
    store.dispatch({
      type: Actions.REQUEST_COURSE_CLASS,
      payload: [5037296],
    });
  };

  private addCourseClass = () => {
    store.dispatch({
      type: Actions.ADD_CLASS_TO_CART,
      payload: {id: "5037296"}
    });
  };

  private removeCourseClass = () => {
    store.dispatch({
      type: Actions.REMOVE_CLASS_FROM_CART,
      payload: {id: "5037296"}
    });
  };

  private refresh() {
    this.setState(this.state ? {refresh: !this.state.refresh}: {refresh: false})
  }
  
  render() {
    const onPlainTextError = () => {config.plainTextError = !config.plainTextError;this.refresh();};
    const onCommonError = () => {config.commonError = !config.commonError;this.refresh();};
    const onValidationError = () => {config.validationError = !config.validationError;this.refresh();};
    return (<div>
      <fieldset>
        <div className="checkbox">
          <label>
            <input type="checkbox" checked={config.plainTextError} onClick={onPlainTextError}/>
              Throw Plain Text Error
          </label>
        </div>
        <div className="checkbox">
          <label>
            <input type="checkbox" checked={config.commonError} onClick={onCommonError}/>
            Throw Common Error
          </label>
        </div>
        <div className="checkbox">
          <label>
            <input type="checkbox" checked={config.validationError} onClick={onValidationError}/>
            Throw Validation Error
          </label>
        </div>
        <button className="btn" onClick={this.loadCourseClasses}>Load Classes</button>
        <button className="btn" onClick={this.addCourseClass}>Add Classes</button>
        <button className="btn" onClick={this.removeCourseClass}>Remove Classes</button>
        <button className="btn" onClick={this.resetLocalForage}>Reset LocalForage</button>
      </fieldset>
       <fieldset>
         {config.db.contacts.result.map((id)=><ContactInfo contact={config.db.getContactById(id)}/>) }
       </fieldset>
    </div>)
  }


}
