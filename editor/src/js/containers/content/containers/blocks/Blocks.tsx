import React, {useEffect} from 'react';
import {connect, Dispatch} from "react-redux";
import classnames from "classnames";
import {getBlocks, saveBlock} from "./actions";
import {State} from "../../../../reducers/state";
import {BlockState} from "./reducers/State";
import Block from "./components/Block";

interface Props {
  blocks: BlockState[];
  onInit: () => any;
  match?: any;
  fetching: boolean;
  onEditHtml: (id, html) => any;
}

const Blocks: React.FC<Props> = props => {

  const {match, blocks, onEditHtml, fetching, onInit} = props;

  useEffect(() => {
    onInit();
  },        []);

  const activeBlock = match.params.id && blocks.find(block => String(block.id) === match.params.id);

  return (
    <div>
      {activeBlock &&
        <div className={classnames({fetching})}>
          <Block
            block={activeBlock}
            onSave={onEditHtml}
          />
        </div>
      }
    </div>
  );
};

const mapStateToProps = (state: State) => ({
  blocks: state.block.items,
  fetching: state.fetching,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => dispatch(getBlocks()),
    onEditHtml: (id, content) => dispatch(saveBlock(id, {content}, true)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Blocks as any);
