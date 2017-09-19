import React from 'react';
import {connect, Dispatch} from "react-redux";
import {Container, Row, Col, Button} from 'reactstrap';
import {withRouter} from 'react-router-dom';
import {getPages} from "./actions";
import {Page as PageModel} from "../../model";

interface Props {
  pages: PageModel[];
  onInit: () => any;
  match?: any;
}

export class Pages extends React.Component<Props, any> {

  componentDidMount() {
    this.props.onInit();
  }

  render() {
    const {match} = this.props;

    return (
      <div>
        {match.params.id &&
          <Page id={match.params.id}/>
        }
      </div>
    );
  }
}

const mapStateToProps = state => ({
  pages: state.page.pages,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => dispatch(getPages()),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Pages);


const Page = props => {
  return (<div>
    page id - {props.id}
  </div>);
};
