import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import Main from './components/Main';
import { GlobalClasses } from './styles/global';
import '../scss/billing.scss';
import { store } from './redux';

export const initApp = () => {
  ReactDOM.render(
    <Provider store={store}>
      <React.StrictMode>
        <GlobalClasses />
        <Main />
      </React.StrictMode>
    </Provider>,
    document.getElementById('provisioning')
  );
};

initApp();
