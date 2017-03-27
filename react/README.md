# Web modules application

Before make sure that you installed node and npm. If not, you can get it from [https://nodejs.org](https://nodejs.org)

All bundles are located in `dist` folder. You will have two files:

* dynamic.js - our application with external dependencies
* dynamic-polyfill.js - es5 polyfills for browsers which doesn't support it
 
Run one of next commands for bundles creating:

## Production build (dist directory will be deleted before)
`npm run build:prod`
or
`npm run build:prod:watch`

## Development build
`npm run build:dev`
or
`npm run build:dev:watch`

## Development mode
1. Run dev server `npm start`
2. Go to localhost:1707/index.html|courses.html

## Run tests
`npm run test`
or
`npm run test:watch`

## Generate documentation
`npm run build:dev`

## Customization of React-component
When you create new react-component which will be able to customize you need create two files:
```
myComponent/
    MyComponent.js
    MyComponent.extend.js
```
You write all logic of component in `MyComponent.js` and all methods
which will be able to override by customer in `MyComponent.extend.js`.

After that you need to pull two modules `MyComponent.extend.js` and
`MyComponent.custom.js` (even if it doesn't exist) in `MyComponent.js`

You need to prepare some code inside overidable methods(e.g. render) in files

MyComponent.js
```
import extend from 'MyComponent.extend.js';
import customExtend from 'MyComponent.custom.js';

let extend = Object.assign({}, nativeExtend, customExtend);

class MyComponent extends React.Component {

    //some logic...
    
    render() {
        return extend.render.apply({
            welcome: 'Hi'
        });
    }
}
```

MyComponent.extend.js
```
export default {
    render() {
        return <div>{this.welcome + ', native ui'}
    }
};
```

MyComponent.custom.js
```
export default {
    render() {
        return <div>{this.welcome + ', custom ui'}
    }
};
```

You will get `Hi, native ui!` without `MyComponent.custom.js` and `Hi, custom ui!` with it in browser.

## Use public JavaScript API

All functions can be found in global object `window.Ish.api`.

For example:

```
window.Ish.api.getCart();
```

## Writing Components

Please follow project style, in project we use [Airbnb React/JSX Style Guide](https://github.com/airbnb/javascript/tree/master/react#table-of-contents) and [Airbnb JavaScript Style Guide](https://github.com/airbnb/javascript)
