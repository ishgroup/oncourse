import * as React from "react";
import {Level, Logger, LogMessage} from "../services/Logger";

export class IndexPage extends React.Component<IndexProps, IndexState> {
  constructor() {
    super();
  }

  componentDidMount() {
    Logger.log(new LogMessage(Level.INFO, "Current path doesn't managed by React-Router yet."));
  }

  /**
   * We doesn't render anything here
   */
  render() {
    return null;
  }
}


interface IndexProps {
}

interface IndexState {
}
