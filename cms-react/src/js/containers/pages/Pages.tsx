import React from 'react';
import {connect, Dispatch} from "react-redux";
import {Container, Row, Col, Button} from 'reactstrap';
import {Route as SubRoute, withRouter} from 'react-router-dom';
import {getPages} from "./actions";
import {Page as PageModel} from "../../model";
import {Route} from "../../routes";

interface Props {
  pages: PageModel[];
  onInit: () => any;
  routes?: Route[];
}

export class Pages extends React.Component<Props, any> {

  componentDidMount() {
    this.props.onInit();
  }

  render() {
    const {pages, routes} = this.props;
    console.log(this.props);
    return (
      <div>
        <SubRoute
          path={'/pages/:id'}
          exact={false}
          component={props => <Page {...props}>1123123123</Page>}
          sidebar={<div>Page Sidebar</div>}
        />
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

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(Pages));


const Page = props => {
  return (<div>
    page id - {props.match.params.id}
  </div>);
};
