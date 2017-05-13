import * as React from "react";
import * as Lodash from "lodash";
import classnames from "classnames";

/**
 * Progress bar for the enrol application
 */

const Classes = {
  active: "active",
  disable: "disable",
  last: "last"
};

export class Progress extends React.Component<Props, any> {

  render() {
    const {onChange} = this.props;
    const detailsClass = this.className(Tab.details);
    const summaryClass = this.className(Tab.summary);
    const paymentClass = classnames(Classes.last, this.className(Tab.payment));

    const onDetails = () => {
      onChange(Tab.details)
    };
    const onSummary = () => {
      onChange(Tab.summary)
    };
    const onPayment = () => {
      onChange(Tab.payment)
    };

    return (
      <div className="progress-steps">
        <ul>
          <li className={detailsClass}><a onClick={onDetails} href="#">Your details</a></li>
          <li className={summaryClass}><a onClick={onSummary} href="#">Summary</a></li>
          <li className={paymentClass}><a onClick={onPayment} href="#">Payment</a></li>
        </ul>
      </div>
    )
  }

  private className = (tab: Tab): string => {
    return this.isActive(tab) ? Classes.active : this.isDisabled(tab) ? Classes.disable : ""
  };

  private isActive = (tab: Tab): boolean => {
    const {model} = this.props;
    return model.active === tab;
  };

  private isDisabled = (tab: Tab): boolean => {
    const {model} = this.props;
    return !Lodash.isNil(model.disabled.find((t) => t === tab));
  }
}

export enum Tab {
  details,
  summary,
  payment
}

export interface Model {
  active: Tab,
  disabled: Tab[]
}

interface Props {
  model: Model
  onChange: (Tab) => void
}