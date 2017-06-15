import * as React from "react";
import {bindActionCreators} from "redux";
import {connect} from "react-redux";
import {hidePopup, updatePopup} from "../actions/Actions";

class Popup extends React.Component<PopupProps, {}> {

  componentWillMount() {
    this.props.update(this.props.children);
  }

  componentWillReceiveProps() {
    this.props.update(this.props.children);
  }

  componentWillUnmount() {
    this.props.hide();
  }

  render() {
    return null;
  }
}

export default connect(null, (dispatch) => {
  return bindActionCreators({
    updatePopup,
    hidePopup,
  }, dispatch);
})(Popup as any);

interface PopupProps {
  update: (content: any) => any;
  hide: () => any;
}
