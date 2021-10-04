import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import Billing from './components/Billing';
import { GlobalClasses } from './styles/global';
import '../scss/billing.scss';
import { store } from './redux';

export const initApp = () => {
  ReactDOM.render(
    <Provider store={store}>
      <React.StrictMode>
        <GlobalClasses />
        <Billing />
      </React.StrictMode>
    </Provider>,
    document.getElementById('provisioning')
  );
};

initApp();
