import 'rxjs';
import {Observable} from "rxjs/Observable";
import {persistStore} from "redux-persist";
import { localForage } from './constants/LocalForage';
import { extendPrototype } from 'localforage-observable';

const localForageEx = extendPrototype(localForage);

export function syncCartStore(store) {
  typeof localForageEx.ready === 'function' && localForageEx.ready().then(() => {

    localForageEx.configObservables({
      crossTabNotification: true,
      changeDetection: false,
    });

    // TypeScript will find `newObservable()` after the casting that `extendPrototype()` does
    localForageEx.newObservable.factory = function (subscribeFn) {
      return Observable.create(subscribeFn);
    };

    const observable = localForageEx.newObservable({
      key: "reduxPersist:cart",
      crossTabNotification: true,
    });

    observable.subscribe({
      next(args) {
        // Sync cart state between all tabs
        persistStore(store, {
          storage: localForageEx,
          blacklist: ["form", "popup", "checkout", "inactiveCourses", "products", "phase", "page", "contactAddProcess", "config"],
        });

      },
      error(err) {
        console.log('Found an error!', err);
      },
      complete() {
        console.log('Observable destroyed!');
      },
    });
  })
  .catch(e => {
    console.error("!!!!!",e);
  });
}
