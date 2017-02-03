import {plural} from '../../lib/utils.ts';
import AddButton from '../addButton/AddButton';
import nativeExtend from './EnrolButton.extend';
import customExtend from './EnrolButton.custom';

let extend = Object.assign({}, nativeExtend, customExtend);

class EnrolButton extends AddButton {

  render() {
    let context = this.getContext();

    Object.assign(context.props, {
      isCanceled: this.props.isCanceled,
      isFinished: this.props.isFinished,
      hasAvailableEnrolmentPlaces: this.props.hasAvailableEnrolmentPlaces,
      allowByApplication: this.props.allowByApplication,
      freePlaces: this.props.freePlaces
    });

    Object.assign(context.utils, {plural});

    return extend.render.apply(context);
  }
}

EnrolButton.propTypes = {
  isCanceled: React.PropTypes.bool,
  isFinished: React.PropTypes.bool,
  hasAvailableEnrolmentPlaces: React.PropTypes.bool,
  allowByApplication: React.PropTypes.bool,
  freePlaces: React.PropTypes.number
};

export default EnrolButton;
