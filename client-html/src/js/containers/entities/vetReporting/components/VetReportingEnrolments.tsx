/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import { Contact } from "@api/model";
import { EditViewProps } from "../../../../model/common/ListView";
import FullScreenStickyHeader from "../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader";
import Divider from "@mui/material/Divider";
import { LinkAdornment } from "../../../../common/components/form/FieldAdornments";
import FormField from "../../../../common/components/form/formFields/FormField";
import {
  EnrolmentSelectItemRenderer,
  EnrolmentSelectValueRenderer,
  openEnrolmentLink
} from "../../enrolments/utils";

const getCustomSearch = search => `courseClass.course.name starts with "${search}" or courseClass.code starts with "${search}"`;

const VetReportingStudent = (props: EditViewProps<Contact>) => {
  const {
    twoColumn,
    values
  } = props;

  return (
    <div className="pt-1 pl-3 pr-3">
      <FullScreenStickyHeader
        isFixed={false}
        twoColumn={twoColumn}
        title="Enrolments"
        disableInteraction
      />
      <Divider className="mt-3 mb-3" />
      <FormField
        type="remoteDataSelect"
        name="selectedEnrolment"
        entity="Enrolment"
        label="Select en enrolment"
        returnType="object"
        selectValueMark="id"
        selectLabelMark="courseClass.course.name"
        aqlColumns="courseClass.course.name,courseClass.course.code,status"
        itemRenderer={EnrolmentSelectItemRenderer}
        valueRenderer={EnrolmentSelectValueRenderer}
        getCustomSearch={getCustomSearch}
        rowHeight={55}
        labelAdornment={<LinkAdornment linkHandler={openEnrolmentLink} link={(values as any).selectedEnrolment} disabled={!(values as any).selectedEnrolment} />}
      />
      <div style={{ height: 2000 }}/>
    </div>
  );
};

export default props => props.values
  ? <VetReportingStudent {...props}/>
  : null;
