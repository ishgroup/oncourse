import * as React from "react";

import {PaymentStatus,PaymentResponse} from "../../../../model";
import {Failed} from "./Failed";
import {Undefined} from "./Undefined";
import {InProgress} from "./InProgress";
import {SuccessfulWaitingCourses} from "./SuccessfulWaitingCourses";
import SummaryListComp from "./SummaryListComp";

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
          <SummaryListComp />
          }
          {response.status === PaymentStatus.SUCCESSFUL_BY_PASS &&
          <SummaryListComp />
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
            reason={response.responseText}
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
