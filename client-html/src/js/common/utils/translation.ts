/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import translationSource from '../../../../translate/translation.json';

namespace translation {
   export function translate(key: keyof typeof translationSource, variables?: string[] | number[]) {
     let translated = translationSource[key];
     
     if (variables?.length) {
       variables.forEach(v => {
         translated = translated.replace(/{{.+}}/, v?.toString());
       });
     }
     
    return translated;
  }
}

declare const $t: typeof translation.translate;

export default translation.translate;