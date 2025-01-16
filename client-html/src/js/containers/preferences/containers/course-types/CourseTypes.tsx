/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import { reduxForm } from "redux-form";
import { onSubmitFail } from "../../../../common/utils/highlightFormErrors";
import { COURSE_TYPES_FORM_NAME } from "../../../../constants/Forms";
import SpecialTagTypeForm from "../../components/SpecialTagTypeForm";

const Decorated = reduxForm({
  onSubmitFail,
  form: COURSE_TYPES_FORM_NAME
})(props => <SpecialTagTypeForm {...props} title="Course types" entity='Course' />);

export default Decorated;