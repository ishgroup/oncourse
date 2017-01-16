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