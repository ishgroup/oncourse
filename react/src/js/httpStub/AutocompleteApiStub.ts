import {HttpService} from "../common/services/HttpService";
import {ModelError} from "../model/ModelError";
import {AutocompleteApi} from "../http/AutocompleteApi";

export class AutocompleteApiStub extends AutocompleteApi {
  autocomplete(key: string, text: string): Promise<string[]> {
    return new Promise(function (resolve, reject) {
      setTimeout(() => {
        resolve([
          `${text}1`,
          `${text}2`,
          `${text}3`,
          `${text}4`,
          `${text}5`
        ])
      }, Math.floor((Math.random() * 1000) + 1));
    });
  }
}
