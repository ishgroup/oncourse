import {Actions} from "./common/actions/Actions";
import { DEFAULT_CONFIG_KEY, DefaultConfig } from "./constants/Config";

export class WillowConfig {
  public checkoutPath?: string;
  public paymentSuccessURL?: string;
  public termsAndConditions?: string;
  public featureEnrolmentDisclosure?: string;

  constructor(props) {
    this.checkoutPath = props.checkoutPath || DefaultConfig.CHECKOUT_PATH;
    this.paymentSuccessURL = props.paymentSuccessURL || DefaultConfig.PAYMENT_SUCCESS_URL;
    this.termsAndConditions = props.termsAndConditions || DefaultConfig.TERMS_AND_CONDITIONS;
    this.featureEnrolmentDisclosure = props.featureEnrolmentDisclosure || DefaultConfig.FEATURE_ENROLMENT_DISCLOSURE;
  }
}

export const configLoader = store => {

  const loadConfigToState = config => {
    store.dispatch({
      type: Actions.UPDATE_WILLOW_CONFIG,
      payload: config,
    });
  };

  const willowConfig = new WillowConfig(window[DEFAULT_CONFIG_KEY] || {});

  if (willowConfig) {
    loadConfigToState(willowConfig);
  }
};

