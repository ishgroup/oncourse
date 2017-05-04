import * as React from "react";
import {findDOMNode, render} from "react-dom";
import {Provider} from "react-redux";
import {Store} from "redux";
import {whenReady} from "../../services/jq";
import forEach from "lodash/forEach";

import {IshState} from "../../services/IshState";
import {Level, Logger, LogMessage} from "../../services/Logger";
import {ATTR_DATA_CID, HTMLMarker} from "../services/HTMLMarker";
import * as HtmlDataService from "./HtmlUtils";

import ComponentClass = React.ComponentClass;

export class Bootstrap {
  private components: { [key: string]: HTMLMarker } = {};
  private store: Store<IshState>;

  constructor(store: Store<IshState>) {
    this.store = store;
  }

  public register = (marker: HTMLMarker): Bootstrap => {
    this.components[marker.id] = marker;
    return this;
  };

  public start = (): Bootstrap => {
    whenReady(() => this.bootstrap());
    return this;
  };

  private render = (container: HTMLElement, marker: HTMLMarker) => {
    try {
      if (container.childElementCount != 0) {
        Logger.log(new LogMessage(Level.DEBUG, `Container ${marker.id} contains children, the application doesn't handle such containers.`));
        return;
      }
      const realProps = HtmlDataService.parse(container, marker);

      render(
        <Provider store={this.store}>
          <marker.component {...realProps}/>
        </Provider>,
        container
      );
    } catch (e) {
      Logger.log(new LogMessage(Level.ERROR, `Component with cid:${marker.id} cannot be instantiated.`, [e]));
    }
  };

  private bootstrap = (): void => {
    try {
      Object.keys(this.components).forEach((cid) => {
        const containers = document.querySelectorAll(`[${ATTR_DATA_CID}=${cid}]`);
        const marker: HTMLMarker = this.components[cid];

        forEach(containers, (container: HTMLElement) => {
          this.render(container, marker);
        });
      });
    } catch (e) {
      Logger.log(new LogMessage(Level.ERROR, "Unhandled error", e));
    }
  }
}
