import * as React from "react";

import {Store} from "react-redux";
import {Actions} from "../../js/web/actions/Actions";
import localForage from "localforage";
import {Injector} from "../../js/injector";
import {MockConfig} from "../mocks/mocks/MockConfig";

import Inspector from "react-inspector";

import "../../scss/index.scss";
import {IshState} from "../../js/services/IshState";
import {Values} from "redux-form-website-template";
import {NAME} from "../../js/enrol/containers/payment/CreditCartForm";


interface Props {
  store: Store<IshState>
  injector: Injector
}

export class MockControl extends React.Component<Props, any> {
  private config: MockConfig = MockConfig.CONFIG;

  private resetLocalForage = () => {
    localForage.clear();
  };

  private loadCourseClasses = () => {
    this.props.store.dispatch({
      type: Actions.REQUEST_COURSE_CLASS,
      payload: [this.config.db.classes.result[0]],
    });
  };

  private addCourseClass = () => {
    this.props.store.dispatch({
      type: Actions.ADD_CLASS_TO_CART,
      payload: {id: this.config.db.classes.result[0]}
    });
  };

  private removeCourseClass = () => {
    this.props.store.dispatch({
      type: Actions.REMOVE_CLASS_FROM_CART,
      payload: {id: this.config.db.classes.result[0]}
    });
  };

  private refresh() {
    this.setState(this.state ? {refresh: !this.state.refresh} : {refresh: false})
  }

  render() {
    const onPlainTextError = () => {
      this.config.plainTextError = !this.config.plainTextError;
      this.refresh();
    };
    const onCommonError = () => {
      this.config.commonError = !this.config.commonError;
      this.refresh();
    };
    const onValidationError = () => {
      this.config.validationError = !this.config.validationError;
      this.refresh();
    };
    return (<div>
      <fieldset>
        <div className="checkbox">
          <label>
            <input type="checkbox" checked={this.config.plainTextError} onClick={onPlainTextError}/>
            Throw Plain Text Error
          </label>
        </div>
        <div className="checkbox">
          <label>
            <input type="checkbox" checked={this.config.commonError} onClick={onCommonError}/>
            Throw Common Error
          </label>
        </div>
        <div className="checkbox">
          <label>
            <input type="checkbox" checked={this.config.validationError} onClick={onValidationError}/>
            Throw Validation Error
          </label>
        </div>
        <button className="btn" onClick={this.loadCourseClasses}>Load Classes</button>
        <button className="btn" onClick={this.addCourseClass}>Add Classes</button>
        <button className="btn" onClick={this.removeCourseClass}>Remove Classes</button>
        <button className="btn" onClick={this.resetLocalForage}>Reset LocalForage</button>
      </fieldset>
      <fieldset>
        <Inspector data={this.config.db.contacts.entities.contacts}/>
        <Inspector data={this.config.db.classes.entities.classes}/>
        <Inspector data={this.props.store.getState()}/>
      </fieldset>
      <Values form={NAME}/>
    </div>)
  }


}
