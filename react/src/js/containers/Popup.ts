///<reference path="../actions/popup.ts"/>
import * as React from 'react';
import {bindActionCreators} from 'redux';
import {connect, ActionCreator} from 'react-redux';
import {updatePopup, hidePopup} from '../actions/popup';

class Popup extends React.Component<PopupProps, {}> {

  componentWillMount = () => {
    this.props.update(this.props.children);
  };

  componentWillReceiveProps = () => {
    this.props.update(this.props.children);
  };

  componentWillUnmount = () => {
    this.props.hide();
  };

  render() {
    return null;
  }
}

export default connect(null, (dispatch) => {
  return bindActionCreators({
    updatePopup,
    hidePopup
  }, dispatch);
})(Popup as any);

interface PopupProps {
  update: (content: any) => any;
  hide: () => any;
}
