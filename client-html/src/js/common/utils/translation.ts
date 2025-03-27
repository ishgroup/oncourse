/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import translationSourceDefault from '../../../../translate/translation_AU.json';
import PreferencesService from '../../containers/preferences/services/PreferencesService';

class TranslationServiceBase {
  private translationSource = {};

  constructor() {
    PreferencesService.getLocation()
      .then(l => {
        this.translationSource = require(`../../../../translate/translation_${l.countryCode || 'AU'}.json`);
      })
      .catch(e => console.error(e));
  }

  public translate = (key: keyof typeof translationSourceDefault, variables?: string[] | number[]):typeof translationSourceDefault[keyof typeof translationSourceDefault] => {
   let translated = this.translationSource[key];

   if (translated && variables?.length) {
     variables.forEach(v => {
       translated = translated.replace(/{{.+}}/, v?.toString());
     });
   }

  return translated;
  };
}

const TranslationService = new TranslationServiceBase();

declare const $t: typeof TranslationService.translate;

export default TranslationService.translate;