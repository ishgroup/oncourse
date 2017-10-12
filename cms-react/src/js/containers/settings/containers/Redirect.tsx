import React from 'react';
import {connect, Dispatch} from "react-redux";

interface Props {
  onInit: () => any;
}

export class Redirect extends React.Component<Props, any> {

  componentDidMount() {
    this.props.onInit();
  }

  render() {


    return (
      <div>
        Redirect settings
      </div>
    );
  }
}

const mapStateToProps = state => ({

});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => undefined,
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Redirect);
