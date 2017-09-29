import React from 'react';
import {Container, Row, Col, Button, FormGroup} from 'reactstrap';
import {Theme as ThemeModel} from "../../../model";

interface Props {
  theme: ThemeModel;
  onSave: (themeId, schema) => void;
}


export class Theme extends React.Component<Props, any> {

  render() {

    return (
      <div>
        Theme Layout
      </div>
    );
  }
}
