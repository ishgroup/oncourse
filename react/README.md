# Web modules application

Before make sure that you installed node and npm. If not you can get it from [https://nodejs.org](https://nodejs.org)

All bundles are located in `dist` folder. Run one of next commands for bundles creating:

## Production build
`npm run build:prod`

## Development build
`npm run build:dev`

## Development build
`npm run build:dev:watch`

## Development mode
1. Run dev build with file watcher `npm run build:dev:watch`
2. Run dev server `npm start`
3. Go to localhost:8080

## Run tests
1. Build test bundle
`npm run build:test`
2. Run tests
`npm test`