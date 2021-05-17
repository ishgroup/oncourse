import localforage from "localforage";
import * as memoryDriver from 'localforage-driver-memory';

export const configLocalStorage = () => {
  localforage.defineDriver(memoryDriver);
  localforage.config({
    driver: [localforage.LOCALSTORAGE, localforage.INDEXEDDB, localforage.WEBSQL, memoryDriver._driver]
  });

  localforage.ready().catch(e => {
    console.error(e);
  });
}

export const localForage = localforage;

