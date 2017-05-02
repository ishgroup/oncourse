import {AutocompleteApi} from "../http/AutocompleteApi";
import {Suggestion} from "../selectors/autocomplete";

export class AutocompleteApiStub extends AutocompleteApi {
  autocomplete(key: string, text: string): Promise<Suggestion[]> {
    return new Promise(function (resolve) {
      setTimeout(() => {
        resolve([
          {key: `${text}1`, value: `value${text}1`},
          {key: `${text}2`, value: `value${text}2`},
          {key: `${text}3`, value: `value${text}3`},
          {key: `${text}4`, value: `value${text}4`},
          {key: `${text}5`, value: `value${text}5`}
        ])
      }, Math.floor((Math.random() * 1000) + 1));
    });
  }
}
