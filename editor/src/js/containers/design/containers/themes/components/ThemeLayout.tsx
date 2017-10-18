import React from 'react';
import {Container, Row, Col, Button, FormGroup} from 'reactstrap';
import {Theme as ThemeModel} from "../../../../../model";
import Source from "../containers/Source";

interface Props {
  theme: ThemeModel;
  blocks: any;
  onUpdate: (blockId, items) => any;
}

export class ThemeLayout extends React.Component<Props, any> {

  render() {
    const {theme, blocks, onUpdate} = this.props;

    const getThemeBlocks = key => {
      if (!theme.schema) return [];

      return theme.schema[key].map(({id}) => blocks.find(block => block.id === id));
    };

    return (
      <div className="theme__layout">
        <Row>
          <Col md="12">
            <Source
              placeholder="Header"
              id="top"
              onUpdate={onUpdate}
              list={getThemeBlocks('top')}
            />
          </Col>
        </Row>

        <Row>
          <Col md="4">
            <Source
              placeholder="Left"
              id="middle1"
              onUpdate={onUpdate}
              list={getThemeBlocks('middle1')}
            />
          </Col>

          <Col md="4">
            <Source
              placeholder="Middle"
              id="middle2"
              onUpdate={onUpdate}
              list={getThemeBlocks('middle2')}
            />
          </Col>

          <Col md="4">
            <Source
              placeholder="Right"
              id="middle3"
              onUpdate={onUpdate}
              list={getThemeBlocks('middle3')}
            />
          </Col>
        </Row>

        <Row>
          <Col md="12">
            <Source
              placeholder="Footer"
              id="footer"
              onUpdate={onUpdate}
              list={getThemeBlocks('footer')}
            />
          </Col>
        </Row>

      </div>
    );
  }
}
