import React from 'react';
import {Block as BlockModel} from "../../../model";

interface BlockProps {
  block: BlockModel;
  onSave: (blockId, html) => void;
}

export class Block extends React.Component<BlockProps, any> {

  componentDidMount() {

  }

  render() {
    const {block} = this.props;

    return (
      <div className="content-white">
      </div>
    );
  }
}