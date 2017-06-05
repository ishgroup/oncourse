import * as React from "react";
import * as L from "lodash";
import {Actions} from "../../js/web/actions/Actions";
import localForage from "localforage";
import {MockConfig} from "../mocks/mocks/MockConfig";

import Inspector from "react-inspector";

import "../../scss/index.scss";
import {Values} from "redux-form-website-template";
import {NAME} from "../../js/enrol/containers/payment/CreditCartForm";
import {NAME as ContactEditFormName} from "../../js/enrol/containers/contact-edit/ContactEditForm";


interface Props {
  config: MockConfig
}

export class MockControl extends React.Component<Props, any> {

  private resetLocalForage = () => {
    localForage.clear();
  };

  private loadCourseClasses = () => {
    this.props.config.store.dispatch({
      type: Actions.REQUEST_COURSE_CLASS,
      payload: [this.props.config.db.classes.result[0]],
    });
  };

  private addCourseClass = () => {
    this.props.config.store.dispatch({
      type: Actions.ADD_CLASS_TO_CART,
      payload: {id: this.props.config.db.classes.result[0]}
    });
  };

  private removeCourseClass = () => {
    this.props.config.store.dispatch({
      type: Actions.REMOVE_CLASS_FROM_CART,
      payload: {id: this.props.config.db.classes.result[0]}
    });
  };

  private refresh() {
    this.setState(this.state ? {refresh: !this.state.refresh} : {refresh: false});
    this.props.config.save();
  }

  render() {
    const {config} = this.props;
    return (<div>
      <fieldset>
        {this.renderProperty("plainTextError")}
        {this.renderProperty("commonError")}
        {this.renderProperty("validationError")}
        {this.renderProperty("contactApi.contactFieldsIsEmpty")}
        {this.renderProperty("checkoutApi.makePayment.formError")}
        {this.renderProperty("checkoutApi.makePayment.modelError")}
        {this.renderProperty("checkoutApi.makePayment.result.failed")}
        {this.renderProperty("checkoutApi.makePayment.result.success")}
        {this.renderProperty("checkoutApi.makePayment.result.inProgress")}
        {this.renderProperty("checkoutApi.makePayment.result.undefined")}
        <button className="btn" onClick={this.loadCourseClasses}>Load Classes</button>
        <button className="btn" onClick={this.addCourseClass}>Add Classes</button>
        <button className="btn" onClick={this.removeCourseClass}>Remove Classes</button>
        <button className="btn" onClick={this.resetLocalForage}>Reset LocalForage</button>
      </fieldset>
      <fieldset>
        <Inspector data={config.db}/>
        <Inspector data={config.store.getState()}/>
        <Inspector data={config.props}/>
      </fieldset>
      <Values form={NAME}/>
      <Values form={ContactEditFormName}/>
    </div>)
  }

  renderProperty = (path: string) => {
    const {config} = this.props;

    const onProps = (path: string) => {
      L.set(config.props, path, !L.get(config.props, path));
      this.refresh();
    };
    const checked = !!L.get(config.props, path);
    return (
      <div className="checkbox">
        <label>
          <input type="checkbox" checked={checked}
                 onChange={() => onProps(path)}/>
        </label>
        {path}
      </div>
    )
  }
}

