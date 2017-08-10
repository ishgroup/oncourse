import * as React from "react";

import {PaymentResponse, PaymentStatus} from "../../../../model";

import {Successful} from "./Successful";
import {Failed} from "./Failed";
import {Undefined} from "./Undefined";
import {InProgress} from "./InProgress";

export interface Props {
  response: PaymentResponse;
  onAnotherCard?: () => void;
  onCancel?: () => void;
  onDestroy?: () => void;
  successLink?: string;
  resetOnDestroy?: boolean;
}

export class ResultComp extends React.Component<Props, any> {
  constructor(props, context) {
    super(props, context);
    this.componentCleanup = this.componentCleanup.bind(this);
  }

  componentCleanup() {
    const {resetOnDestroy, onDestroy} = this.props;
    if (resetOnDestroy) {
      onDestroy();
    }
  }

  componentDidMount() {
    window.addEventListener('beforeunload', this.componentCleanup);
  }

  componentWillUnmount() {
    this.componentCleanup();
    window.removeEventListener('beforeunload', this.componentCleanup);
  }

  render() {
    const {response, onAnotherCard, onCancel, successLink} = this.props;
    return (
      <div>
        {response &&
        <div>
          {response.status === PaymentStatus.SUCCESSFUL &&
          <Successful
            refId={response.reference}
            successLink={successLink}
          />
          }
          {response.status === PaymentStatus.FAILED &&
          <Failed
            onAnotherCard={onAnotherCard}
            onCancel={onCancel}
            successLink={successLink}
          />
          }
          {response.status === PaymentStatus.UNDEFINED && <Undefined/>}
          {response.status === PaymentStatus.IN_PROGRESS && <InProgress/>}
        </div>
        }
      </div>
    );
  }
}
