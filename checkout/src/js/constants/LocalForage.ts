import localforage from "localforage";
import { IS_JEST } from './Config';

if (!IS_JEST) {
  localforage.ready(() => {
    console.log("localforage ready");
  }).catch(e => {
    console.error(e);
  });
}

export const localForage = IS_JEST ? {
  getItem: jest.fn(),
  setItem: jest.fn(),
  clear: jest.fn()
} : localforage;
