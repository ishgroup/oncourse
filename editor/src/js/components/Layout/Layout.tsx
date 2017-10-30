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
    <Container fluid>
      <Row className={classnames({'full-height': fullHeight})}>
        {sidebar &&
          sidebar
        }

        <Col md={sidebar ? 10 : 12} className="content-wrapper">
          {content}
        </Col>
      </Row>
    </Container>
  );
}
