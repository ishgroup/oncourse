import React from 'react';
import {connect, Dispatch} from "react-redux";
import {Container, Row, Col} from 'reactstrap';
import {Route, NavLink, Redirect, withRouter} from 'react-router-dom';
import {routes} from '../routes';
import {Layout} from './components/Layout/Layout';

export class Cms extends React.Component<any, any> {

  render() {
    const {isAuthenticated} = this.props.auth;
    console.log(this.props);

    return (
      <Container>
        <Layout
          sidebar={isAuthenticated ? <Sidebar/> : undefined}
          content={<Content isAuthenticated={isAuthenticated}/>}
          fullHeight={true}
        />
      </Container>
    );
  }
}

const Content = props => {
  return (
    <div>
      {routes.map((route, index) => (
        <RouteWrapper
          key={index}
          path={route.path}
          exact={route.exact}
          isPublic={route.isPublic}
          component={route.main}
          isAuthenticated={props.isAuthenticated}
        />
      ))}
    </div>
  );
}

const Sidebar = () => (
  <div className="sidebar">
    <ul>
      {routes.filter(route => !route.isPublic).map((route, index) => (
        <li key={index}>
          <NavLink exact={route.exact} to={route.path} activeClassName="active">{route.title}</NavLink>
        </li>
      ))}
    </ul>
  </div>
)

const RouteWrapper = ({component: Component, ...rest}) => {
  return (
    <Route {...rest} render={props => (
      rest.isAuthenticated || rest.isPublic ? (
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

