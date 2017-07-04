import * as React from "react";
import * as Lodash from "lodash";
import classnames from "classnames";

/**
 * Progress bar for the checkout application
 */

const Classes = {
  active: "active",
  disable: "disable",
  last: "last",
};

interface Props {
  model: Model;
  onChange: (Tab) => void;
}

export class Progress extends React.Component<Props, any> {

  render() {
    const {onChange} = this.props;
    const detailsClass = this.className(Tab.Details);
    const summaryClass = this.className(Tab.Summary);
    const paymentClass = classnames(Classes.last, this.className(Tab.Payment));

    const onClick = (tab: Tab) => {
      !this.isDisabled(tab) && onChange(tab);
    };

    return (
      <div className="progress-steps">
        <ul>
          <li className={detailsClass}><a onClick={() => onClick(Tab.Details)} href="#">Your details</a></li>
          <li className={summaryClass}><a onClick={() => onClick(Tab.Summary)} href="#">Summary</a></li>
          <li className={paymentClass}><a onClick={() => onClick(Tab.Payment)} href="#">Payment</a></li>
        </ul>
      </div>
    );
  }

  private className = (tab: Tab): string => {
    return this.isActive(tab) ? Classes.active : this.isDisabled(tab) ? Classes.disable : "";
  }

  private isActive = (tab: Tab): boolean => {
    const {model} = this.props;
    return model.active === tab;
  }

  private isDisabled = (tab: Tab): boolean => {
    const {model} = this.props;
    return !Lodash.isNil(model.disabled.find(t => t === tab));
  }
}

export enum Tab {
  Details,
  Summary,
  Payment,
}

export interface Model {
  active: Tab;
  disabled: Tab[];
}

