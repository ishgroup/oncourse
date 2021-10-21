/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

export interface FormsState {
  contactForm: {
    userFirstName: string;
    userLastName: string;
    userPhone: string;
    userEmail: string;
  },
  organisationForm: {
    organisationName: string;
    abn: string;
    tradingName: string;
    address: string;
    suburb: string;
    state: string;
    postcode: string;
    country: string;
    timeZone: string;
  }
}
