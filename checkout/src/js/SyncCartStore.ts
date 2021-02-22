import 'rxjs';
import localforage from "localforage";
import {extendPrototype} from "localforage-observable";
import {Observable} from "rxjs/Observable";
import {persistStore} from "redux-persist";

localforage.config({
  driver: [localforage.INDEXEDDB, localforage.WEBSQL, localforage.LOCALSTORAGE],
});

const localForage = extendPrototype(localforage);

export function syncCartStore(store) {
  typeof localForage.ready === 'function' && localForage.ready().then(() => {

    localForage.configObservables({
      crossTabNotification: true,
      changeDetection: false,
    });

    // TypeScript will find `newObservable()` after the casting that `extendPrototype()` does
    localForage.newObservable.factory = function (subscribeFn) {
      return Observable.create(subscribeFn);
    };

    const observable = localForage.newObservable({
      key: "reduxPersist:cart",
      crossTabNotification: true,
    });

    observable.subscribe({
      next(args) {
        // Sync cart state between all tabs
        persistStore(store, {
          storage: localForage,
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
  });
}
