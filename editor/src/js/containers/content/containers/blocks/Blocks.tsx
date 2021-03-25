import React, {useEffect} from 'react';
import {connect} from "react-redux";
import {Dispatch} from "redux";
import classnames from "classnames";
import {getBlocks, saveBlock, setBlockContentMode} from "./actions";
import {State} from "../../../../reducers/state";
import {BlockState} from "./reducers/State";
import Block from "./components/Block";
import {ContentMode} from "../../../../model";
import {setPageContentMode} from "../pages/actions";

interface Props {
  blocks: BlockState[];
  onInit: () => any;
  match?: any;
  fetching: boolean;
  onEditHtml: (id, html) => any;
  setContentMode?: (id: number, contentMode: ContentMode) => any;
}

const Blocks: React.FC<Props> = ({match, blocks, onEditHtml, fetching, onInit, setContentMode}) => {
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
            setContentMode={setContentMode}
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
    setContentMode: (id: number, contentMode: ContentMode) => dispatch(setBlockContentMode(id,contentMode))
  };
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Blocks as any);
