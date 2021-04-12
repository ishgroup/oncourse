import React from 'react';
import ReactDOM from 'react-dom';
import {MemoryRouter as Router} from 'react-router-dom';
import {StylesProvider, jssPreset, createGenerateClassName} from '@material-ui/styles';
import { create } from 'jss';
import Cms from "./containers/Cms";
import StylesProviderCustom from "./styles/StylesProviderCustom";

class CMSCustom extends HTMLElement {
  connectedCallback() {
    // console.log(1, "first render");
    const shadowRoot = this.attachShadow({ mode: 'open' });
    const mountPoint = document.createElement('span');
    const reactRoot = shadowRoot.appendChild(mountPoint);

    const jss = create({
      ...jssPreset(),
      insertionPoint: reactRoot
    });

    const generateClassName = createGenerateClassName({
      disableGlobal: true
    });

    return ReactDOM.render(
      <StylesProvider jss={jss} generateClassName={generateClassName}>
        <Router>
          <Cms/>
        </Router>
      </StylesProvider>,
      mountPoint);
  }
}
// console.log(0, customElements.get('cms-custom'))
declare global {
  namespace JSX {
    interface IntrinsicElements {
      'cms-custom': React.DetailedHTMLProps<React.HTMLAttributes<HTMLElement>, HTMLElement>;
    }
  }
}
// console.log(1, customElements.get('cms-custom'))
customElements.define('cms-custom', CMSCustom);
// console.log(2, customElements.get('cms-custom'))

export default new CMSCustom;