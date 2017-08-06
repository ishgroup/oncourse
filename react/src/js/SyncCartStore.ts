import 'rxjs';
import * as localforage from "localforage";
import {extendPrototype} from "localforage-observable";
import {Observable} from "rxjs/Observable";
import {persistStore} from "redux-persist";

const localForage = extendPrototype(localforage);

export function syncCartStore(store) {
  localForage.ready().then(() => {

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
          blacklist: ["form", "popup", "checkout", "products", "phase", "page", "contactAddProcess"],
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
