import React from 'react';
import {connect, Dispatch} from "react-redux";
import {Container, Row, Col} from 'reactstrap';
import {Route, Link, Redirect, withRouter} from 'react-router-dom';
import {routes} from '../routes';
import {Login} from "./Login/Login";
import {Layout} from './components/Layout/Layout';

export class Cms extends React.Component<any, any> {

  getContent() {
    const {isAuthenticated} = this.props;

    return (
      <div>
        {routes.map((item, index) => (
          <PrivateRoute
            key={index}
            path={item.path}
            exact={item.exact}
            component={item.main}
            isAuthenticated={isAuthenticated}
          />
        ))}

        <Route path="/login" component={Login}/>
      </div>
    );
  }

  render() {
    const {isAuthenticated} = this.props.auth;

    return (
      <Container>
        <Layout
          sidebar={isAuthenticated ? <Sidebar/> : undefined}
          content={this.getContent()}
          fullHeight={true}
        />
      </Container>
    );
  }
}

const Sidebar = () => (
  <div className="sidebar">
    <ul>
      {routes.map((route, index) => (
        <li key={index}><Link to={route.path}>{route.title}</Link></li>
      ))}
    </ul>
  </div>
)

const PrivateRoute = ({component: Component, ...rest}) => {
  return (
    <Route {...rest} render={props => (
      rest.isAuthenticated ? (
        <Component {...props}/>
      ) : (
        <Redirect to={{
          pathname: '/login',
          state: {from: props.location},
        }}/>
      )
    )}/>
  );
};

const mapStateToProps = state => ({
  auth: state.cms.auth,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {};
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(Cms));

