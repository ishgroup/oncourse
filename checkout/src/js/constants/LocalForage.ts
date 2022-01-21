import localforage from 'localforage';
import { IS_JEST } from './Config';

if (!IS_JEST) {
  localforage.ready()
    .catch((e) => {
      console.error(e);
    })
    .then(() => {
      console.log('localforage ready');
    });
}

export const localForage: LocalForage = IS_JEST ? {
  getItem: jest.fn(),
  setItem: jest.fn(),
  clear: jest.fn()
} as any : localforage;
