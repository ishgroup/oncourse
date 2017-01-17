import { render, findDOMNode } from 'react-dom';
import { Provider } from 'react-redux';
import { camelCase } from './utils';

export default class Bootstrap {

    constructor() {
        this.components = {};
    }

    setStore(store) {
        this.store = store;
        return this;
    }

    register(id, Component, props) {
        this.components[id] = {
            Component,
            props
        };
        return this;
    }

    start() {
        for(let cid in this.components) {
            let containers = document.querySelectorAll(`[data-cid=${cid}]`),
                { Component, props } = this.components[cid];

            containers.forEach((container) => {
                let realProps = {};

                for(let prop in props) {
                    let value = container.dataset[camelCase('prop-' + prop)];

                    realProps[prop] = this.prepareProp(value, props[prop]);
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

    prepareProp(value, type) {
        if(!(type in this.constructor.prepareType)) {
            throw new Error('unexpected type');
        }

        return this.constructor.prepareType[type](value);
    }
}

Bootstrap.prepareType = {
    boolean(value) {
        if(['true', 'false'].indexOf(value) === -1) {
            throw new Error('expected boolean type');
        }

        return value === 'true';
    },

    number(value) {
        let v = Number(value);

        if(isNaN(v)) {
            throw new Error('expected number type');
        }

        return v;
    },

    string(value) {
        if(typeof value !== 'string') {
            throw new Error('expected string type');
        }

        return value;
    },

    array(value) {
        let v = JSON.parse(value);

        if(!(v instanceof Array)) {
            throw new Error('expected array type');
        }

        return v;
    },

    object(value) {
        let v = JSON.parse(value);

        if(typeof v !== 'object' || v instanceof Array) {
            throw new Error('expected object type');
        }

        return v;
    }
};