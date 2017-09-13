import * as Actions from "./common/actions";
import {DefaultConfig} from "./constants/Config";

const DEFAULT_CONFIG_KEY = 'cms_config';

export class CmsConfig {
  public cssPath?: string;

  constructor(props) {
    this.cssPath = props.cssPath || DefaultConfig.CSS_PATH;
  }
}

export const configLoader = store => {

  const loadConfigToState = config => {
    store.dispatch({
      type: Actions.UPDATE_CMS_CONFIG,
      payload: config,
    });
  };

  const cmsConfig = new CmsConfig(window[DEFAULT_CONFIG_KEY] || {});

  if (cmsConfig) {
    loadConfigToState(cmsConfig);
  }
};

