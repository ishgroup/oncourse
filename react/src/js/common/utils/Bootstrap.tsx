import * as React from "react";
import {forEach, isNil} from "lodash";
import {findDOMNode, render} from "react-dom";
import {Provider} from "react-redux";
import {Store} from "redux";
import {whenReady} from "../../services/jq";

import {IshState} from "../../services/IshState";
import {Level, Logger, LogMessage} from "../../services/Logger";
import {ATTR_DATA_CID, HTMLMarker, HTMLMarkers} from "../services/HTMLMarker";
import * as HtmlDataService from "./HtmlUtils";
import {Actions} from "../../web/actions/Actions";
import {Actions as CommonActions} from "../../common/actions/Actions";

import {htmlProps2CourseClass} from "../../web/services/CourseClassService";
import {CourseClass} from "../../model/web/CourseClass";
import {ClassesListSchema} from "../../NormalizeSchema";
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
  }

  /**
   * onEven parameter needs to have a possibility to start bootstrap from tests
   */
  public start = (onEvent:boolean = true): Bootstrap => {
    onEvent ? whenReady(() => this.bootstrap()) : this.bootstrap();
    return this;
  }

  private renderMarker = (container: HTMLElement, marker: HTMLMarker) => {
    try {
      if (container.childElementCount != 0) {
        Logger.log(new LogMessage(Level.DEBUG, `Container ${marker.id} contains children, the application doesn't handle such containers.`));
        return;
      }
      const realProps = HtmlDataService.parse(container, marker);

      if (!isNil(realProps.checkoutPath)) {
        this.store.dispatch({type: CommonActions.CheckoutPathUpdate, payload: realProps.checkoutPath});
      }

      render(
        <Provider store={this.store}>
          <marker.component {...realProps}/>
        </Provider>,
        container,
      );
    } catch (e) {
      Logger.log(new LogMessage(Level.ERROR, `Component with cid:${marker.id} cannot be instantiated.`, [e]));
    }
  }

  /**
   * This function convert loaded html properties to correspondent object and add this object to the react store
   */
  private addToStore = (marker: HTMLMarker, props: { [key: string]: any }, dispatch) => {
    switch (marker) {
      case HTMLMarkers.ENROL_BUTTON:
        const courseClass: CourseClass = htmlProps2CourseClass(props);
        if (courseClass.course) {
          dispatch({
            type: Actions.PutClassToStore,
            payload: normalize(courseClass, ClassesListSchema),
          });
        }
    }
  }


  private bootstrap = (): void => {
    try {
      Object.keys(this.components).forEach(cid => {
        const containers = document.querySelectorAll(`[${ATTR_DATA_CID}='${cid}']`);
        const marker: HTMLMarker = this.components[cid];
        forEach(containers, (container: HTMLElement) => {
          this.renderMarker(container, marker);
        });
      });
    } catch (e) {
      Logger.log(new LogMessage(Level.ERROR, "Unhandled error", e));
    }
  }
}
