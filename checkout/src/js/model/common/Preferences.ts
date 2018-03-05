
export class Preferences {

  /**
   * is corporate pass enabled preference
   */
  corporatePassEnabled?: boolean;

  /**
   * is credit card enabled preference
   */
  creditCardEnabled?: boolean;

  /**
   * After payment successful redirect user to URL
   */
  successLink?: string;

  /**
   * Terms and conditions page
   */
  refundPolicyUrl?: string;

  /**
   * Student Information
   */
  featureEnrolmentDisclosure?: string;

  /**
   * Google tag manager account
   */
  googleTagmanagerAccount?: string;

  /**
   * Minimal age which not requiring parent
   */
  minAge?: number;
}

