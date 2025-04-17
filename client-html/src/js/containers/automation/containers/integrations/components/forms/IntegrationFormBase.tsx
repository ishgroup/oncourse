/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from 'react';
import { initialize } from 'redux-form';

class IntegrationFormBase extends React.Component<any, any> {
  constructor(props) {
    super(props);
    // Initializing form with values
    props.dispatch(initialize(props.form, props.item));
  }

  componentDidUpdate(prevProps) {
    if (prevProps.item.id !== this.props.item.id) {
      // Reinitializing form with values
      this.props.dispatch(initialize(this.props.form, this.props.item));
    }
  }
}

export default IntegrationFormBase;