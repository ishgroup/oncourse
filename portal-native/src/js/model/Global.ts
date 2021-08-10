// indicate module
export {};

declare global {
  namespace NodeJS {
    export interface Global {
      __REDUX_DEVTOOLS_EXTENSION_COMPOSE__: any
    }
  }
}


export type Normalized<K extends keyof any, V> = {
  [P in K]: { [id: string]: V }
};
