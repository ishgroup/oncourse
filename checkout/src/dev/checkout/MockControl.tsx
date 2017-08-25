import * as React from "react";
import * as L from "lodash";
import {Actions} from "../../js/web/actions/Actions";
import localforage from "localforage";
import {MockConfig} from "../mocks/mocks/MockConfig";

import * as ReactInspector from "react-inspector";

import {Values} from "redux-form-website-template";
import {NAME} from "../../js/enrol/containers/payment/components/PaymentForm";
import {NAME as ContactEditFormName} from "../../js/enrol/containers/contact-edit/ContactEditForm";
import {NAME as ConcessionAddForm} from "../../js/enrol/containers/concession/Concession";
import {GABuilder} from "../../js/services/GoogleAnalyticsService";


interface Props {
  config: MockConfig;
}

export class MockControl extends React.Component<Props, any> {

  private resetLocalForage = () => {
    localforage.clear();
  }

  private loadCourseClasses = (i) => {
    this.props.config.store.dispatch({
      type: Actions.REQUEST_COURSE_CLASS,
      payload: [this.props.config.db.classes.result[i]],
    });
  }

  private addCourseClass = i => {
    this.props.config.store.dispatch({
      type: Actions.ADD_CLASS_TO_CART,
      payload: {id: this.props.config.db.classes.result[i]},
      meta: {
        analytics: GABuilder.addItemToCart('Course Class', this.props.config.db.classes.result[i]),
      },
    });
  }

  private loadVoucher = () => {
    this.props.config.store.dispatch({
      type: Actions.REQUEST_PRODUCT,
      payload: [this.props.config.db.products.result[0]],

    });
  }

  private addVoucher = () => {
    this.props.config.store.dispatch({
      type: Actions.ADD_PRODUCT_TO_CART,
      payload: {id: this.props.config.db.products.result[0]},
      meta: {
        analytics: GABuilder.addItemToCart('Voucher', this.props.config.db.products.result[0])
      }
    });
  }

  private removeCourseClass = () => {
    this.props.config.store.dispatch({
      type: Actions.REMOVE_CLASS_FROM_CART,
      payload: {id: this.props.config.db.classes.result[0]},
      meta: {
        analytics: GABuilder.removeItemFromCart('CourseClass', this.props.config.db.classes.result[0])
      },
    });
  }

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
        <button className="btn" onClick={() => this.loadCourseClasses(0)}>Load Classes 1</button>
        <button className="btn" onClick={() => this.addCourseClass(0)}>Add Classes 1</button>
        <br/>
        <button className="btn" onClick={() => this.loadCourseClasses(1)}>Load Classes 2</button>
        <button className="btn" onClick={() => this.addCourseClass(1)}>Add Classes 2</button>
        <br/>
        <button className="btn" onClick={this.removeCourseClass}>Remove Classes</button>
        <button className="btn" onClick={this.loadVoucher}>Load Voucher</button>
        <button className="btn" onClick={this.addVoucher}>Add Voucher</button>
        <button className="btn" onClick={this.resetLocalForage}>Reset LocalForage</button>
      </fieldset>
      <fieldset>
        <ReactInspector.Inspector data={config.db}/>
        <ReactInspector.Inspector data={config.store.getState()}/>
        <ReactInspector.Inspector data={config.props}/>
      </fieldset>
      <Values form={NAME}/>
      <Values form={ContactEditFormName}/>
      <Values form={ContactEditFormName}/>
      <Values form={ConcessionAddForm}/>
    </div>);
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
    );
  }
}

