import React from 'react';
import {Container, Row, Col, Button, FormGroup} from 'reactstrap';
import {Theme as ThemeModel} from "../../../model";
import Source from "../containers/Source";

interface Props {
  theme: ThemeModel;
  blocks: any;
}

export class ThemeLayout extends React.Component<Props, any> {

  render() {
    const {theme, blocks} = this.props;

    const getThemeBlocks = key => {
      if (!theme.schema) return [];

      return theme.schema[key].map(({id}) => blocks.find(block => block.id === id));
    };

    return (
      <div className="theme-schema">
        <div className="header">
          <Source list={getThemeBlocks('top')}/>
        </div>
        <div className="middle">
          <div>
            <Source list={getThemeBlocks('middle1')}/>
          </div>
          <div>
            <Source list={getThemeBlocks('middle2')}/>
          </div>
          <div>
            <Source list={getThemeBlocks('middle3')}/>
          </div>
        </div>
        <div className="footer">
          <Source list={getThemeBlocks('bottom')}/>
        </div>
      </div>
    );
  }
}
