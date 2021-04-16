import React from 'react';
import {Route, Redirect} from 'react-router-dom';
import {routes} from '../../routes';

export const Content = props => {
  return (
    <>
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
    </>
  );
};

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
