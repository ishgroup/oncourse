import {Actions} from "./common/actions/Actions";
import {DefaultConfig} from "./constants/Config";

const DEFAULT_CONFIG_KEY = 'checkout_config';

export class WillowConfig {
  public checkoutPath?: string;

  constructor(props) {
    this.checkoutPath = props.checkoutPath || DefaultConfig.CHECKOUT_PATH;
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

