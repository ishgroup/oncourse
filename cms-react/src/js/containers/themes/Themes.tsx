import React from 'react';
import {Container, Row, Col} from 'reactstrap';
import {connect, Dispatch} from "react-redux";
import {getThemes} from "./actions/index";

interface Props {

}

export class Themes extends React.Component<any, any> {

  componentDidMount() {
    console.log('sss');
    this.props.onInit();
  }

  render() {
    return (
      <div>Design page</div>
    );
  }
}

const mapStateToProps = state => ({
  themes: state.themes.items,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => dispatch(getThemes()),
  };
};


export default connect(mapStateToProps, mapDispatchToProps)(Themes);
