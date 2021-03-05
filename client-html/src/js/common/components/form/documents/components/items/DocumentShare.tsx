/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

import { Document, DocumentAttachmentRelation, DocumentVisibility } from "@api/model";
import {
  FormControlLabel
} from "@material-ui/core";
import Collapse from "@material-ui/core/Collapse";
import IconButton from "@material-ui/core/IconButton";
import clsx from "clsx";
import React, {
  useEffect, useMemo, useRef, useState
} from "react";
import Avatar from "@material-ui/core/Avatar";
import Button from "@material-ui/core/Button";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import CardHeader from "@material-ui/core/CardHeader";
import makeStyles from "@material-ui/core/styles/makeStyles";
import Typography from "@material-ui/core/Typography";
import {
 Attachment, Language, OpenInNew, Link, LockOutlined, SupervisorAccount, Directions
} from "@material-ui/icons";
import { AlertTitle } from "@material-ui/lab";
import Alert from "@material-ui/lab/Alert";
import { Dispatch } from "redux";
import { change } from "redux-form";
import { showMessage } from "../../../../../actions";
import { StyledCheckbox } from "../../../form-fields/CheckboxField";
import { Switch } from "../../../form-fields/Switch";
import { openInternalLink } from "../../../../../utils/links";
import { AppTheme } from "../../../../../../model/common/Theme";
import { DocumentShareOption } from "../../../../../../model/entities/Document";
import {
 getAvailableOptions, getDocumentShareSummary, groupAttachmentsByEntity, isSingleContactAttachment
} from "../utils";

const useStyles = makeStyles((theme: AppTheme) => ({
  linkButton: {
    fontSize: "1.2em",
    padding: theme.spacing(0.5)
  },
  action: {
    marginTop: 0,
    marginRight: theme.spacing(-4)
  },
  attachments: {
    display: "grid",
    gridTemplateColumns: "1fr 1fr",
    gridGap: theme.spacing(1)
  },
  attachmentsTwoColumn: {
    gridTemplateColumns: "1fr 1fr"
  },
  lockCheckbox: {
    marginLeft: theme.spacing(1),
    marginTop: theme.spacing(-0.5)
  }
}));

interface Props {
  validUrl?: string;
  form?: string;
  dispatch?: Dispatch;
  documentSource?: Document;
  readOnly?: boolean;
  itemPath?: string;
  noPaper?: boolean;
}

const onAttachmentCategoryClick = (entity, entityIds) =>
  openInternalLink(`/${entity.toLowerCase()}?search=id in (${entityIds})`);

const onAttachmentPeopleClick = (entity: string, access: DocumentVisibility, relations: DocumentAttachmentRelation[]) => {
  switch (entity) {
    case "Class": {
      if (access === "Tutors and enrolled students") {
        let link = `/contact?search=studentCourseClass.id in (${relations.map(r => r.entityId).join(", ")})`;
        link = link.concat(' or ');
        link = link.concat(`tutorCourseClass.id in (${relations.map(r => r.entityId).join(", ")})`);
        openInternalLink(link);
      }
      if (access === "Tutors only") {
        openInternalLink(`/contact?search=${relations.map(r => `tutorCourseClass.id=${r.entityId}`).join(" or ")}`);
      }
      break;
    }
    case "Course": {
      if (access === "Tutors and enrolled students") {
        const link = `/contact?search=allRelatedContactsToCourse.id in (${relations.map(r => r.entityId).join(", ")})`;
        openInternalLink(link);
      }
      if (access === "Tutors only") {
        openInternalLink(`/contact?search=${relations.map(r => `tutorCourseClass.course.id=${r.entityId}`).join(" or ")}`);
      }
      break;
    }
    case "Assessment": {
      if (access === "Tutors and enrolled students") {
        const link = `/contact?search=allRelatedContactsToAssessment.id in (${relations.map(r => r.entityId).join(", ")})`;
        openInternalLink(link);
      }
      if (access === "Tutors only") {
        openInternalLink(`/contact?search=${relations.map(r =>
          `tutorCourseClass.assessmentClasses.assessment.id=${r.entityId}`).join(" or ")}`);
      }
      break;
    }
    default: {
      openInternalLink(`/contact?search=id in (${relations.map(r => r.relatedContacts.map(c => c.id))})`);
    }
  }
};

const DocumentShare:React.FC<Props> = ({
   validUrl,
   dispatch,
   documentSource,
   form,
   readOnly,
   itemPath,
   noPaper
 }) => {
  const attachmentRef = useRef(null);
  const summaryRef = useRef(null);

  const groupedAttachmentRelations = groupAttachmentsByEntity(documentSource.attachmentRelations);

  const [availableOptions, setAvailableOptions] = useState<{[O in DocumentShareOption]?: boolean}>(getAvailableOptions(groupedAttachmentRelations));

  const onAttachmentsOver = () => {
    const nodes = attachmentRef.current.querySelectorAll(".highlight");
    nodes.forEach(node => {
      node.classList.add("primaryColor");
    });
  };

  const onAttachmentsOut = () => {
    const nodes = attachmentRef.current.querySelectorAll(".highlight");
    nodes.forEach(node => {
      node.classList.remove("primaryColor");
    });
  };

  useEffect(() => {
    setAvailableOptions(getAvailableOptions(groupedAttachmentRelations));
  }, [documentSource.id]);

  useEffect(() => {
    summaryRef?.current?.addEventListener("mouseenter", onAttachmentsOver);
    summaryRef?.current?.addEventListener("mouseleave", onAttachmentsOut);
    return () => {
      summaryRef?.current?.removeEventListener("mouseenter", onAttachmentsOver);
      summaryRef?.current?.removeEventListener("mouseleave", onAttachmentsOut);
    };
  }, []);

  const linkInput = useRef<any>();

  const classes = useStyles();

  const onCopyLink = () => {
    linkInput.current.select();
    document.execCommand("copy");
    linkInput.current.setSelectionRange(0, 0);
    dispatch(showMessage({
      message: "Link copied",
      success: true
    }));
  };

  const accesPath = itemPath ? `${itemPath}.access` : "access";
  const sharedPath = itemPath ? `${itemPath}.shared` : "shared";

  const onPortalSharingChange = (e, val) => {
    dispatch(change(form, accesPath, val ? "Tutors and enrolled students" : "Private"));
  };

  const onTutorsAndStudentsChange = (e, val) => {
    dispatch(change(form, accesPath, val ? "Tutors and enrolled students" : "Tutors only"));
  };

  const onTutorsChange = (e, val) => {
    dispatch(change(form, accesPath, val ? "Tutors only" : "Private"));
  };

  const onLinkChange = (e, val) => {
    dispatch(change(form, accesPath, val ? "Link" : "Private"));
  };

  const onWebsiteChange = (e, val) => {
    dispatch(change(form, accesPath, val ? "Public" : "Private"));
  };

  const onSharedChange = (e, val) => {
    dispatch(change(form, sharedPath, !val));
  };

  const AttachmentRelations = useMemo(() => {
    if (!documentSource.attachmentRelations || !documentSource.attachmentRelations.length) {
      return (
        <Typography>
          No attachments
        </Typography>
      );
    }

    const portalEnabled = documentSource.access === "Tutors and enrolled students" || documentSource.access === "Tutors only";

    return (
      <div className={documentSource.attachmentRelations.length > 1 ? classes.attachments : "d-flex"}>
        {Object.keys(groupedAttachmentRelations).map((entity: any) => {
        const relations = groupedAttachmentRelations[entity];
        const relationsMap = relations.map(r => r.entityId);
        const displayedRelations = relations.slice(0, 4);
        const displayedRelationsCount = displayedRelations.length;
        const relationsCount = relations.length;
        const moreCount = relationsCount - displayedRelationsCount;

        return (
          <div key={entity}>
            <Typography
              component="div"
            >
              {`${relationsCount} ${entity.capitalize()}${relationsCount > 1 ? entity[entity.length - 1] === "s" ? "es" : "s" : ""} `}
              <IconButton
                color="secondary"
                className={classes.linkButton}
                onClick={() => onAttachmentCategoryClick(entity, relationsMap)}
              >
                <OpenInNew fontSize="inherit" />
              </IconButton>
              {portalEnabled
                && !["Site", "Room"].includes(entity) && (
                <IconButton
                  color="secondary"
                  className={classes.linkButton}
                  onClick={() => onAttachmentPeopleClick(entity, documentSource.access, relations)}
                >
                  <SupervisorAccount fontSize="inherit" className="highlight" />
                </IconButton>
              )}
            </Typography>
            <Typography variant="body2" color="textSecondary" component="div">
              {
                displayedRelations.map((r, index) => (
                  <span
                    key={r.entityId}
                  >
                    {r.label}
                    {index === displayedRelations.length - 1
                      ? ""
                      : ", "}
                  </span>
                ))
              }
              {moreCount ? (
                <span>{`... and ${moreCount} more`}</span>
                )
                : null}
            </Typography>
          </div>
        );
      })}
        {documentSource.attachmentRelations.length === 1
        && (
          <FormControlLabel
            classes={{
              root: clsx("checkbox", classes.lockCheckbox),
              label: "ml-0"
            }}
            control={(
              <StyledCheckbox
                disabled={readOnly}
                checked={!documentSource.shared}
                onChange={onSharedChange}
              />
            )}
            label="Attach only to this record"
          />
        )}
      </div>
    );
  }, [documentSource.attachmentRelations, documentSource.shared, documentSource.access]);

  const websiteAvailable = !documentSource.attachmentRelations.length
    || (documentSource.attachmentRelations.length === 1 && documentSource.attachmentRelations[0].entity === "Course");

  const contactRelated = isSingleContactAttachment(documentSource.attachmentRelations);

  const SummaryLabel = useMemo(() => getDocumentShareSummary(documentSource.access, documentSource.attachmentRelations),
     [documentSource.access, documentSource.attachmentRelations]);

  const linkOrPublic = ["Link", "Public"].includes(documentSource.access);

  const tutorsAndStudents = ["Tutors and enrolled students", "Tutors only"].includes(documentSource.access);

  const cardsElevation = noPaper ? 0 : 1;

  const cardHeaderClasses = noPaper
    ? {
        root: "p-0",
        action: "mt-0",
        title: "heading"
      }
    : {
        action: classes.action,
        title: "heading"
      };

  return (
    <div>
      <Alert severity="info" className="mb-2" ref={summaryRef}>
        <AlertTitle>Who can view this document</AlertTitle>
        {SummaryLabel}
      </Alert>
      <Card className="mb-2" elevation={cardsElevation}>
        <CardHeader
          classes={cardHeaderClasses}
          avatar={(
            <Avatar className="activeAvatar">
              <Attachment />
            </Avatar>
          )}
          title="Attached to"
        />
        <CardContent ref={attachmentRef}>
          {AttachmentRelations}
        </CardContent>
      </Card>

      {availableOptions["PortalSharing"]
        && (
        <Card className="mb-2" elevation={cardsElevation}>
          <CardHeader
            classes={cardHeaderClasses}
            action={(
              <FormControlLabel
                classes={{
                root: "switchWrapper",
                label: "switchLabel"
              }}
                control={(
                  <Switch
                    disabled={readOnly}
                    checked={tutorsAndStudents}
                    onChange={onPortalSharingChange}
                  />
              )}
                label="Shared in portal"
              />
          )}
            avatar={(
              <Avatar className="activeAvatar">
                <Directions />
              </Avatar>
          )}
            title="Skills onCourse"
          />
          {contactRelated
          ? (
            <Collapse in={tutorsAndStudents}>
              <CardContent>
                <Typography>
                  Shared with
                  {" "}
                  {documentSource.attachmentRelations[0].relatedContacts.map(c => c.name)}
                  {" "}
                </Typography>
              </CardContent>
            </Collapse>
          )
          : (
            <>
              {availableOptions["Tutor&Student"]
              && (
              <Collapse in={tutorsAndStudents}>
                <CardContent>
                  <FormControlLabel
                    classes={{
                    root: "checkbox",
                    label: "ml-0"
                  }}
                    control={(
                      <StyledCheckbox
                        disabled={readOnly}
                        checked={tutorsAndStudents}
                        onChange={onTutorsChange}
                      />
                  )}
                    label="Show to tutors"
                  />
                  <FormControlLabel
                    classes={{
                    root: "checkbox"
                  }}
                    control={(
                      <StyledCheckbox
                        disabled={readOnly}
                        checked={documentSource.access === "Tutors and enrolled students"}
                        onChange={onTutorsAndStudentsChange}
                      />
                  )}
                    label="Show to students"
                  />
                </CardContent>
              </Collapse>
              )}
            </>
          )}
        </Card>
      )}

      <Card className="mb-2" elevation={cardsElevation}>
        <CardHeader
          classes={cardHeaderClasses}
          action={(
            <FormControlLabel
              classes={{
                root: "switchWrapper",
                label: "switchLabel"
              }}
              control={(
                <Switch
                  disabled={readOnly}
                  checked={linkOrPublic}
                  onChange={onLinkChange}
                />
              )}
              label="Shared by link"
            />
          )}
          avatar={(
            <Avatar className="activeAvatar">
              <Link />
            </Avatar>
          )}
          title="Shareable link"
        />
        {validUrl
          && linkOrPublic && (
            <CardContent>
              <div className="centeredFlex">
                <Typography color="textSecondary" className="flex-fill">
                  <input ref={linkInput} readOnly className="codeArea" type="text" value={validUrl} />
                </Typography>
                <Button color="primary" className="text-nowrap" onClick={onCopyLink}>
                  Copy Link
                </Button>
              </div>
            </CardContent>
          )}
        {!linkOrPublic
          && (
            <CardContent>
              <Alert severity="warning" icon={<LockOutlined />}>
                Document can not be accessed by direct link
              </Alert>
            </CardContent>
          )}
      </Card>

      {websiteAvailable
      && (
        <Card className="mb-2" elevation={cardsElevation}>
          <CardHeader
            classes={cardHeaderClasses}
            action={(
              <FormControlLabel
                classes={{
                  root: "switchWrapper",
                  label: "switchLabel"
                }}
                control={(
                  <Switch
                    disabled={readOnly}
                    checked={
                      documentSource.access === "Public"
                    }
                    onChange={onWebsiteChange}
                  />
                )}
                label="Shared with website visitors"
              />
            )}
            avatar={(
              <Avatar className="activeAvatar">
                <Language />
              </Avatar>
            )}
            title="Website"
          />
        </Card>
      )}
    </div>
);
};

export default DocumentShare;
