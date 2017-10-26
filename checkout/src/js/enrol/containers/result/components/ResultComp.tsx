import * as React from "react";

import {PaymentResponse, PaymentStatus} from "../../../../model";

import {Successful} from "./Successful";
import {Failed} from "./Failed";
import {Undefined} from "./Undefined";
import {InProgress} from "./InProgress";
import {SuccessfulByPass} from "./SuccessfulByPass";
import {SuccessfulWaitingCourses} from "./SuccessfulWaitingCourses";

export interface Props {
  response: PaymentResponse;
  onAnotherCard?: () => void;
  onCancel?: () => void;
  onInit?: () => void;
  successLink?: string;
  resetOnInit?: boolean;
  result?: any;
}

export class ResultComp extends React.Component<Props, any> {

  componentWillMount() {
    const {resetOnInit, onInit} = this.props;
    if (resetOnInit) {
      onInit();
    }
  }

  render() {
    const {response, onAnotherCard, onCancel, successLink, result} = this.props;
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
          {response.status === PaymentStatus.SUCCESSFUL_BY_PASS &&
          <SuccessfulByPass
            successLink={successLink}
          />
          }

          {response.status === PaymentStatus.SUCCESSFUL_WAITING_COURSES &&
          <SuccessfulWaitingCourses
            successLink={successLink}
            contacts={result}
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
