import * as React from 'react';
import {plural, stopPropagation} from '../../lib/utils';
import classnames from 'classnames';
import {ConfirmOrderDialog} from "../addButton/ConfirmOrderDialog";
import {CourseClassCart, CourseClassCartState} from "../../services/IshState";
import {CourseClass} from "../../model/CourseClass";

export class EnrolButton extends React.Component<EnrolButtonProps, EnrolButtonState> {

  constructor() {
    super();

    this.state = {
      showedPopup: false,
      isAlreadyAdded: false,
      pending: false
    };
  }

  add = (e) => {
    if (this.state.pending) {
      return;
    }

    if (this.props.isAdded) {
      stopPropagation(e);
      this.setState({
        showedPopup: true,
        isAlreadyAdded: true
      });
    } else {
      //ToDo how to handle error?
      this.setState({
        pending: true
      });
      this.props.addCourseClass(this.props.courseClass);
      this.setState({
        showedPopup: true,
        isAlreadyAdded: false,
        pending: false
      });
    }
  };

  closePopup = () => {
    this.setState({
      showedPopup: false
    });
  };

  render() {
    const {
      id,
      name,
      isPaymentGatewayEnabled,
      hasAvailablePlaces,
      isCancelled,
      isFinished,
      isAllowByApplication,
      freePlaces
    } = this.props.courseClass;

    const {isAdded} = this.props;
    const isActive = !isFinished && !isCancelled && hasAvailablePlaces && isPaymentGatewayEnabled;
    const showedPlaces = hasAvailablePlaces;
    const reverseElements = isFinished;

    let text = '';

    if (isCancelled) {
      text = 'Cancelled'
    } else if (isActive) {
      if (isAdded) {
        text = 'Added';
      } else if (isAllowByApplication) {
        text = 'Apply Now';
      } else {
        text = 'Enrol Now';
      }
    } else if (!hasAvailablePlaces) {
      text = 'Class Full';
    } else {
      text = 'Finished';
    }

    let elements = [
      isPaymentGatewayEnabled && <button key="enrol_button" className={classnames('enrolAction', {
        'enrol-added-class': isAdded,
        'disabled': !isActive
      })} title={text} onClick={isActive ? this.add : null}>
        {text}
      </button>,
      showedPlaces && <div key="free_places" className="classStatus">
        {freePlaces > 5 ? 'There are places available' : `There ${plural(freePlaces, ['is one place', `${freePlaces} are places`])} available`}
      </div>
    ];

    return (
      <div className="classAction">
        {reverseElements ? elements.reverse() : elements}
        {this.state.showedPopup && <ConfirmOrderDialog id={id} name={name}
                                            isAlreadyAdded={this.state.isAlreadyAdded} close={this.closePopup}/>}
      </div>
    );
  }
}

export interface EnrolButtonProps {
  readonly id: string;
  readonly isAdded: boolean,
  readonly courseClass: CourseClassCart; // TODO: actually just CourseClass, waiting for Artem fixes.
  readonly addCourseClass?: (item: CourseClass) => void,
}

interface EnrolButtonState {
  showedPopup: boolean,
  isAlreadyAdded: boolean,
  pending: boolean
}

