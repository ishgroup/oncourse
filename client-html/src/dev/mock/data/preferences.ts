/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import * as Model from "../../../js/model/preferences";

export function mockPreferences() {
  this.getPreferences = (keys: string[]) => keys.map(key => ({
    uniqueKey: key,
    valueString: this.preference[key]
  }));

  this.preferencesLockedDate = () => "2015-02-02";

  this.savePreferences = fields =>
    fields.forEach(item => {
      this.preference[item.uniqueKey] = item.valueString;
    });

  this.getPreferencesEnumByName = enumName => {
    if (enumName === "ExportJurisdiction") {
      return [
        {
          value: "1",
          label: "NCVER (Standard AVETMISS)"
        },
        {
          value: "2",
          label: "NSW Department of Education"
        },
        {
          value: "3",
          label: "CSO (Community Colleges)"
        },
        {
          value: "4",
          label: "STSOnline (NSW)"
        },
        {
          value: "5",
          label: "DETConnect (Queensland)"
        },
        {
          value: "6",
          label: "STELA (South Australia)"
        },
        {
          value: "7",
          label: "Skills Tasmania"
        },
        {
          value: "8",
          label: "Skills Victoria"
        },
        {
          value: "9",
          label: "STARS (WA)"
        },
        {
          value: "10",
          label: "AQTF Competency Completions"
        },
        {
          value: "11",
          label: "WA RAPT"
        },
        {
          value: "12",
          label: "Northern Territories VET Provider Portal"
        },
        {
          value: "13",
          label: "AVETARS (ACT)"
        }
      ];
    }

    if (enumName === "TwoFactorAuthStatus") {
      return [
        {
          "value": "disabled",
          "label": "Optional for all users"
        },
        {
          "value": "enabled.admin",
          "label": "Required for admin users"
        },
        {
          "value": "enabled.all",
          "label": "Required for all users"
        }
      ];
    }
    return [];
  };

  return {
    // College info preferences
    [Model.CollegeABN.uniqueKey]: "111",
    [Model.CollegeName.uniqueKey]: "My college",
    [Model.CollegeWebsite.uniqueKey]: "http://mysite.com",
    [Model.CollegeTimezone.uniqueKey]: "EU",
    [Model.CollegeSucureKey.uniqueKey]: "c87r5MTvLdowffWu",

    // Licences preferences
    [Model.LicenseAccessControl.uniqueKey]: true,
    [Model.LicenseScripting.uniqueKey]: false,
    [Model.LicenseSMS.uniqueKey]: false,

    // Messaging preferences
    [Model.EmailAdminAddress.uniqueKey]: "admin@admin.com",
    [Model.EmailBounceAddress.uniqueKey]: "admin@bounce.com",
    [Model.EmailBounceEnabled.uniqueKey]: true,
    [Model.EmailFromAddress.uniqueKey]: "admin@admin.com",
    [Model.EmailFromName.uniqueKey]: "admin@admin.com",
    [Model.EmailPop3Account.uniqueKey]: "test.com.au",
    [Model.EmailPop3Host.uniqueKey]: "test",
    [Model.EmailPop3Password.uniqueKey]: "test",
    [Model.SMSFromAddress.uniqueKey]: "+55555555",

    // Class defaults preferences
    [Model.ClassMinPlaces.uniqueKey]: 10,
    [Model.ClassMaxPlaces.uniqueKey]: 20,
    [Model.ClassDeliveryMode.uniqueKey]: 1,
    [Model.ClassFundingSourcePreference.uniqueKey]: 2,

    // LDAP preferences
    [Model.LdapBaseDN.uniqueKey]: "test",
    [Model.LdapBindUserDN.uniqueKey]: "test",
    [Model.LdapBindUserPass.uniqueKey]: "test",
    [Model.LdapDomain.uniqueKey]: "test",
    [Model.LdapGroupAttribute.uniqueKey]: "test",
    [Model.LdapGroupMemberAttribute.uniqueKey]: "test",
    [Model.LdapGroupPosixStyle.uniqueKey]: "test",
    [Model.LdapGroupSearchFilter.uniqueKey]: "test",
    [Model.LdapHost.uniqueKey]: "test",
    [Model.LdapSaslAuthentication.uniqueKey]: false,
    [Model.LdapSimpleAuthentication.uniqueKey]: false,
    [Model.LdapSecutiry.uniqueKey]: "test",
    [Model.LdapServerPort.uniqueKey]: "test",
    [Model.LdapSSL.uniqueKey]: true,
    [Model.LdapUsernameAttribute.uniqueKey]: "test",
    [Model.LdapUserSearchFilter.uniqueKey]: "test",

    // Maintenance preferences
    [Model.LogoutEnabled.uniqueKey]: "test",
    [Model.LogoutTimeout.uniqueKey]: 360,

    // AVETMISS preferences
    [Model.Address1.uniqueKey]: "test",
    [Model.Address2.uniqueKey]: "test",
    [Model.AvetmissCollegeName.uniqueKey]: "test",
    [Model.CertSignatoryName.uniqueKey]: "test",
    [Model.CollegeShortName.uniqueKey]: "test",
    [Model.ContactName.uniqueKey]: "test name",
    [Model.Email.uniqueKey]: "test@test.test",
    [Model.Fax.uniqueKey]: "test",
    [Model.FeeHelpProviderCode.uniqueKey]: "test",
    [Model.Id.uniqueKey]: "test",
    [Model.Jurisdiction.uniqueKey]: "3",
    [Model.Phone.uniqueKey]: "test",
    [Model.Postcode.uniqueKey]: "test",
    [Model.QldIdentifier.uniqueKey]: "test",
    [Model.ShowGUI.uniqueKey]: true,
    [Model.State.uniqueKey]: "1",
    [Model.StateName.uniqueKey]: "test",
    [Model.Suburb.uniqueKey]: "test",
    [Model.Type.uniqueKey]: "4",
    [Model.showOfferedQM.uniqueKey]: true,

    // Financial preferences
    [Model.AccountDebtors.uniqueKey]: "1",
    [Model.AccountBank.uniqueKey]: "2",
    [Model.AccountTax.uniqueKey]: "3",
    [Model.AccountStudentEnrolments.uniqueKey]: "5",
    [Model.AccountPrepaidFees.uniqueKey]: "4",
    [Model.AccountPrepaidFeesPostAt.uniqueKey]: "everySession",
    [Model.AccountVoucherLiability.uniqueKey]: "3",
    [Model.AccountVoucherUnderpayment.uniqueKey]: "9",
    [Model.AccountDefaultCurrency.uniqueKey]: "AUD",
    [Model.AccountInvoiceTerms.uniqueKey]: "7",
    [Model.PaymentInfo.uniqueKey]: "Some payment info",

    // Security
    [Model.SecurityAutoDisableInactiveAccount.uniqueKey]: true,
    [Model.SecurityPasswordComplexity.uniqueKey]: true,
    [Model.SecurityPasswordExpiryPeriod.uniqueKey]: 180,
    [Model.SecurityTFAExpiryPeriod.uniqueKey]: 180,
    [Model.SecurityNumberIncorrectLoginAttempts.uniqueKey]: 5,
    [Model.SecurityTFAStatus.uniqueKey]: "disabled",
  };
}
