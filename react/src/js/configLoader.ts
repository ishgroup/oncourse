import scriptjs from "scriptjs";

import {Actions} from "./common/actions/Actions";

const DEFAULT_CONFIG_URL = 'http://localhost:8000/config.js';
const DEFAULT_CONFIG_KEY = 'willow_config';

const DEFAULTS = {
  checkoutPath: '/checkout/',
}

class WillowConfig {
  private checkoutPath: string;

  constructor (props) {
    this.checkoutPath = props.checkoutPath || DEFAULTS.checkoutPath;
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

