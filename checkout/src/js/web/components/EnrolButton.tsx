import * as React from "react";
import classnames from "classnames";
import {ConfirmOrderDialog} from "./addButton/ConfirmOrderDialog";
import {CourseClass} from "../../model";
import {stopPropagation} from "../../common/utils/HtmlUtils";


export class EnrolButton extends React.Component<Props, State> {
  constructor() {
    super();

    this.state = {
      showedPopup: false,
      isAlreadyAdded: false,
      pending: false,
    };
  }

  setButtonRef = node => {
    if (node) {
      const event = new CustomEvent('enrolButtonRendered', {bubbles: true});
      node.dispatchEvent(event);
    }
  }

  add = e => {
    if (this.state.pending) {
      return;
    }

    if (this.props.isAdded) {
      stopPropagation(e);
      this.setState({
        showedPopup: true,
        isAlreadyAdded: true,
      });
    } else {
      // ToDo how to handle error?
      this.setState({
        pending: true,
      });
      this.props.addToCart(this.props.courseClass);
      this.setState({
        showedPopup: true,
        isAlreadyAdded: false,
        pending: false,
      });
    }
  }

  closePopup = () => {
    this.setState({
      showedPopup: false,
    });
  }

  componentDidMount() {
    const {id, loadById} = this.props;
    loadById(id);
  }

  render() {
    const {
      id,
      course,
      isPaymentGatewayEnabled,
      hasAvailablePlaces,
      isCancelled,
      isFinished,
      isAllowByApplication,
      availableEnrolmentPlaces,
    } = this.props.courseClass;

    const {isAdded, checkoutPath} = this.props;
    const isActive = !isFinished && !isCancelled && hasAvailablePlaces && isPaymentGatewayEnabled;
    const showedPlaces = hasAvailablePlaces;
    const reverseElements = isFinished;
    const fetching = typeof isCancelled === 'undefined' && typeof isActive === 'undefined';

    let text = '';

    if (isCancelled) {
      text = 'Cancelled';
    } else if (isActive) {
      if (isAdded) {
        text = 'Added';
      } else if (isAllowByApplication) {
        text = 'Apply Now';
      } else {
        text = 'Enrol Now';
      }
    } else if (hasAvailablePlaces === false) {
      text = 'Class Full';
    } else {
      text = 'Finished';
    }

    const elements = [
      <button key="enrol_button" className={classnames('enrolAction', {
        'enrol-added-class': isAdded,
        disabled: !isActive && !fetching,
        fetching,
      })} title={text} onClick={isActive && !fetching ? this.add : null}>
        {fetching ? "Enrol Now" : text}
      </button>,
      showedPlaces &&
        <div
          key="free_places"
          className="classStatus"
        >
            There {availableEnrolmentPlaces === 1 ? "is " : "are "}
            <span
              data-places={availableEnrolmentPlaces}
              className={classnames("available-places",{
                ["available-places-high"]:  availableEnrolmentPlaces > 5,
              })}>
              {availableEnrolmentPlaces < 6 && availableEnrolmentPlaces}
            </span>
            {availableEnrolmentPlaces === 1 ? " place " : " places "}
            available
        </div>,
    ];

    return (
      <div className="classAction" ref={this.setButtonRef}>
        {reverseElements ? elements.reverse() : elements}
        {this.state.showedPopup && course &&
        <ConfirmOrderDialog
          id={id}
          name={course.name}
          isAlreadyAdded={this.state.isAlreadyAdded}
          close={this.closePopup}
          checkoutPath={checkoutPath}
        />
        }
      </div>
    );
  }
}

export interface Props {
  readonly id: string;
  readonly isAdded: boolean;
  readonly courseClass: CourseClass;
  readonly checkoutPath: string;
  readonly loadById: (id: string) => void;
  readonly addToCart?: (item: CourseClass) => void;
}

interface State {
  showedPopup: boolean;
  isAlreadyAdded: boolean;
  pending: boolean;
}

