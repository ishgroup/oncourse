import scriptjs from "scriptjs";

import {Actions} from "./common/actions/Actions";
import {DefaultConfig} from "./constants/Config";

const DEFAULT_CONFIG_URL = `/s/js/config.js?${new Date().valueOf()}`;
const DEFAULT_CONFIG_KEY = 'willow_config';

export class WillowConfig {
  public checkoutPath?: string;

  constructor (props) {
    this.checkoutPath = props.checkoutPath || DefaultConfig.CHECKOUT_PATH;
  }
}

export const configLoader = store => {

  scriptjs(
    DEFAULT_CONFIG_URL,
    () => checkAndLoadConfig(),
  );

  const checkAndLoadConfig = () => {
    const willowConfig =  new WillowConfig(window[DEFAULT_CONFIG_KEY] || {});

    if (willowConfig) {
      loadConfigToState(willowConfig);
    }
  };

  const loadConfigToState = config => {
    store.dispatch({
      type: Actions.UPDATE_WILLOW_CONFIG,
      payload: config,
    });
  };
}

