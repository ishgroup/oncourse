import * as React from "react";
import classnames from "classNames";

/**
 * Progress bar for the enrol application
 */
export class Progress extends React.Component<Props, State> {

  render() {
    const {onChange} = this.props;
    const detailsClass = this.isActive(Tab.details)? "active": "disable";
    const summaryClass = this.isActive(Tab.summary)? "active": "disable";
    const paymentClass = classnames("last", this.isActive(Tab.payment)? "active": "disable");
    const onDetails = (e) => {onChange(Tab.details)};
    const onSummary = (e) => {onChange(Tab.summary)};
    const onPayment = (e) => {onChange(Tab.payment)};

    return (
      <div className="progress-steps">
        <ul>
          <li className={detailsClass} ><a onClick={onDetails} href="#">Your details</a></li>
          <li className={summaryClass}><a onClick={onSummary} href="#">Summary</a></li>
          <li className={paymentClass}><a onClick={onPayment} href="#">Payment</a></li>
        </ul>
      </div>
    )
  }

  private isActive = (tab:Tab):boolean => {
    const {selected} = this.props;
    return selected === tab;
  }
}

export enum Tab {
  details,
  summary,
  payment
}


interface Props {
  selected: Tab,
  onChange: (Tab) => void
}

interface State {
  tab: Tab;
}
