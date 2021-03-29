import "rxjs";
import {Observable} from "rxjs/Observable";

let index: number = 0;

const getPromise = (): Promise<string> => {
  console.log(index);
  index = index + 1;
  if (index > 3) {
    return Promise.resolve("successful");
  } else {
    return Promise.reject("error");
  }
};


test('test rxjs retry ', () => {
//  Observable.fromPromise(getPromise()).flatMap().retry(3).subscribe((v) => console.log(v))

  Observable.defer(getPromise).retry(2).catch((error) => {
    console.log(error);
    return Observable.of(error)
  }).subscribe(
    (x) => console.log('Next: ' + x),
    (err) => console.log('Error: ' + err),
    () => console.log('Completed'));
});
