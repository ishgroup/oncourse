# Web modules application

Before make sure that you installed node and npm. If not, you can get it from [https://nodejs.org](https://nodejs.org)

All bundles are located in `dist` folder. You will have two files:

* dynamic.js - our application with external dependencies
* polyfill.js - es5 polyfills for browsers which doesn't support it
 
Run one of next commands for bundles creating:

## Production build (dist directory will be deleted before)
`npm run build:prod`

## Development build
`npm run build:dev`

## Development build with watcher
`npm run build:dev:watch`

## Development mode
1. Run dev build with file watcher `npm run build:dev:watch`
2. Run dev server `npm start`
3. Go to localhost:8080

## Run tests
1. Build test bundle (bundle will be put in tmp folder)
`npm run build:test`
2. Run tests
`npm test`

You can remove test bundle file by: `npm test:clean`

## Generate documentation
`npm run doc`

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