import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import Billing from './Billing';
import reportWebVitals from './reportWebVitals';

ReactDOM.render(
  <React.StrictMode>
    <Billing />
  </React.StrictMode>,
  document.getElementById('root')
);

reportWebVitals(console.log);
