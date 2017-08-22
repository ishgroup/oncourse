import * as React from "react";
import {ReactElement} from "react";
import {connect} from "react-redux";
import {IshState} from "../../services/IshState";

class PopupContainer extends React.Component<Props, any> {
  render():ReactElement<any> {
    return (
      <div>
        {this.props.content}
      </div>
    );
  }
}

export default connect((state:IshState) => {
  return {
    content: state.popup.content
  };
})(PopupContainer as any);

interface Props {
  content: string;
}
