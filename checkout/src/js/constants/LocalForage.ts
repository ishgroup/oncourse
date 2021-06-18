import * as localforage from "localforage";

localforage.ready(() => {
  console.log("localforage ready");
}).catch(e => {
  console.error(e);
});

export const localForage = localforage;
