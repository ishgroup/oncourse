import * as React from "react";
import {findDOMNode, render} from "react-dom";
import {Provider} from "react-redux";
import {Store} from "redux";
import {whenReady} from "../../services/jq";
import forEach from "lodash/forEach";

import {IshState} from "../../services/IshState";
import {Level, Logger, LogMessage} from "../../services/Logger";
import {ATTR_DATA_CID, HTMLMarker, HTMLMarkers} from "../services/HTMLMarker";
import * as HtmlDataService from "./HtmlUtils";
import {Actions} from "../../web/actions/Actions";
import {htmlProps2CourseClass} from "../../web/services/CourseClassService";
import {CourseClass} from "../../model/web/CourseClass";
import {classesListSchema} from "../../schema";
import {normalize} from "normalizr";

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
      this.addToStore(marker, realProps, this.store.dispatch);
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

  private addToStore = (marker: HTMLMarker, props:{ [key: string]: any }, dispatch) => {
    switch (marker) {
      case HTMLMarkers.ENROL_BUTTON:
        const courseClass:CourseClass = htmlProps2CourseClass(props);
        if (courseClass.course) {
          dispatch({
            type: Actions.PutClassToStore,
            payload: normalize(courseClass, classesListSchema)
          })
        }
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
