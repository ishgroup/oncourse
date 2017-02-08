import * as React from 'react';
import {render, findDOMNode} from 'react-dom';
import {Provider} from 'react-redux';
import {camelCase} from './utils';
import {Store} from "redux";
import {IshState} from "../services/IshState";
import {whenReady} from "../services/jq.js";
import forEach from "lodash/forEach";
import Component = React.Component;
import JSXElement = JSX.JSXElement;
import ComponentClass = React.ComponentClass;
import {Logger, Level, LogMessage} from "../services/Logger";

interface BootstrapComponent {
  Component: ComponentClass<any>;
  props: {[key: string]: string};
}

export class Bootstrap {
  private components: {[key: string]: BootstrapComponent} = {};

  constructor(private store: Store<IshState>) {
  }

  register(id: string, Component: ComponentClass<any>, props?: {[key: string]: string}) {
    this.components[id] = {
      Component,
      props
    };
    return this;
  }

  start(): Bootstrap {
    whenReady(() => this.bootstrap());
    return this
  }

  private static prepareProp(value, type) {
    if (!(type in Bootstrap.prepareType)) {
      throw new Error('unexpected type');
    }

    return Bootstrap.prepareType[type](value);
  }

  private bootstrap() {
    for (let cid in this.components) {
      const containers = document.querySelectorAll(`[data-cid=${cid}]`),
        {Component, props} = this.components[cid];

      forEach(containers, (container: HTMLElement) => {
        if (container.childElementCount != 0) {
          Logger.log(new LogMessage(Level.DEBUG, "Container already has a children, that mean that React will not" +
            " render this tag."));
          return
        }

        let realProps = {};

        for (let prop in props) {
          let value = container.dataset[camelCase('prop-' + prop)];

          realProps[prop] = Bootstrap.prepareProp(value, props[prop]);
        }

        render(
          <Provider store={this.store}>
            <Component {...realProps}/>
          </Provider>,
          container
        );
      });
    }
  }

  private static prepareType = {
    boolean(value) {
      if (['true', 'false'].indexOf(value) === -1) {
        throw new Error('expected boolean type');
      }

      return value === 'true';
    },

    number(value) {
      let v = Number(value);

      if (isNaN(v)) {
        throw new Error('expected number type');
      }

      return v;
    },

    string(value) {
      if (typeof value !== 'string') {
        throw new Error('expected string type');
      }

      return value;
    },

    array(value) {
      let v = JSON.parse(value);

      if (!(v instanceof Array)) {
        throw new Error('expected array type');
      }

      return v;
    },

    object(value) {
      let v = JSON.parse(value);

      if (typeof v !== 'object' || v instanceof Array) {
        throw new Error('expected object type');
      }

      return v;
    }
  };
}
