// indicate module
export {};

declare global {
  namespace NodeJS {
    export interface Global {
      __REDUX_DEVTOOLS_EXTENSION_COMPOSE__: any
    }
  }
}
