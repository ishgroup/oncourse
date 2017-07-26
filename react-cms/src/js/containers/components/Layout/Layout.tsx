import React from "react";
import {Container, Row, Col} from 'reactstrap';
import classnames from 'classnames';

interface Props {
  sidebar?: any;
  content?: any;
  fullHeight?: boolean;
}

export const Layout = (props: Props) => {
  const {sidebar, fullHeight, content} = props;

  return (
    <Container>
      <Row className={classnames({'full-height': fullHeight})}>
        {sidebar &&
        <Col md="3" className="sidebar-wrapper">
          {sidebar}
        </Col>
        }

        <Col md={sidebar ? 9 : 12}>
          {content}
        </Col>
      </Row>
    </Container>
  );
}
