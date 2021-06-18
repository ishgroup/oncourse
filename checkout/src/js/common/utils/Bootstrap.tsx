import * as React from "react";
import {render} from "react-dom";
import {Provider} from "react-redux";
import {Store} from "redux";
import {whenReady} from "../../services/jq";
import {IshState} from "../../services/IshState";
import {Level, Logger, LogMessage} from "../../services/Logger";
import {ATTR_DATA_CID, HTMLMarker, HTMLMarkers} from "../services/HTMLMarker";
import * as HtmlDataService from "./HtmlUtils";
import {Actions} from "../../web/actions/Actions";
import {htmlProps2CourseClass} from "../../web/services/CourseClassService";
import {CourseClass} from "../../model";
import {ClassesListSchema} from "../../NormalizeSchema";
import {normalize} from "normalizr";
import {ConfigConstants} from "../../config/ConfigConstants";
import { ErrorBoundary} from "../../constants/Bugsnag";
import bugsnagClient from "@bugsnag/js";
import {initGAEvent} from "../../services/GoogleAnalyticsService";
import { localForage } from "../../constants/LocalForage";

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
      if (container.childElementCount > 1 || (container.firstElementChild && container.firstElementChild.hasAttribute('data-reactroot'))) {
        Logger.log(new LogMessage(Level.DEBUG, `Container ${marker.id} contains already mounted react module or more than one children element`));
        return;
      }
      const realProps = HtmlDataService.parse(container, marker);

      render(
        <ErrorBoundary>
          <Provider store={this.store}>
            <marker.component {...realProps}/>
          </Provider>
        </ErrorBoundary>,
        container,
      );
    } catch (e) {
      bugsnagClient.notify(e);
      Logger.log(new LogMessage(Level.ERROR, `Component with cid:${marker.id} cannot be instantiated.`, [e]));
      Logger.log(new LogMessage(Level.INFO, `State`, [this.store.getState()]));
      Logger.log(new LogMessage(Level.INFO, `App version`, [ConfigConstants.APP_VERSION]));

      localForage.clear().then(() => {
        Logger.log(new LogMessage(Level.INFO, `Local storage cleared`));
      }).catch(e => {
        console.error(e);
      });
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

  private onLinkClick = (type,code) => {
    initGAEvent({
      ecAction: "linkClick",
      type,
      code,
    },
    this.store.getState());
  }


  private bootstrap = (): void => {
    try {
      const classLinks = document.querySelectorAll(`a[href*="${window.location.origin}/class/"]`) as any;
      const productLinks = document.querySelectorAll(`a[href*="${window.location.origin}/product/"]`) as any;

      classLinks.forEach(l => {
        const codeMatch = l.getAttribute("href").match(/[^/]+$/);
        const code = codeMatch ? codeMatch[0] : "";
        l.onclick = () => this.onLinkClick("class",code);
      });

      productLinks.forEach(l => {
        const codeMatch = l.getAttribute("href").match(/[^/]+$/);
        const code = codeMatch ? codeMatch[0] : "";
        l.onclick = () => this.onLinkClick("product",code);
      });

      Object.keys(this.components).forEach(cid => {
        const containers = document.querySelectorAll(`[${ATTR_DATA_CID}='${cid}']`) as any;
        const marker: HTMLMarker = this.components[cid];
        containers.forEach(container => {
          this.renderMarker(container, marker);
        });
      });
    } catch (e) {
      bugsnagClient.notify(e);
      Logger.log(new LogMessage(Level.ERROR, "Unhandled error", e));
    }
  }
}
