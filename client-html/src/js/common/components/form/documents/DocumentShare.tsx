/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Document, DocumentAttachmentRelation, DocumentVisibility } from '@api/model';
import {
  Attachment,
  Directions,
  Language,
  Link,
  LockOutlined,
  OpenInNew,
  SupervisorAccount
} from '@mui/icons-material';
import { Alert, AlertTitle, Card, CardContent, Collapse, FormControlLabel, Typography } from '@mui/material';
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import CardHeader from '@mui/material/CardHeader';
import IconButton from '@mui/material/IconButton';
import $t from '@t';
import clsx from 'clsx';
import { makeAppStyles, openInternalLink, StyledCheckbox, Switch } from 'ish-ui';
import React, { useEffect, useMemo, useRef, useState } from 'react';
import { Dispatch } from 'redux';
import { change } from 'redux-form';
import { mapEntityDisplayName } from '../../../../containers/entities/common/utils';
import { DocumentShareOption } from '../../../../model/entities/Document';
import { showMessage } from '../../../actions';
import { IAction } from '../../../actions/IshAction';
import {
  getAvailableOptions,
  getDocumentShareSummary,
  groupAttachmentsByEntity,
  isSingleContactAttachment
} from '../../../utils/documents';

const typesAllowedForWebsite = ["Course", "Contact"];

const useStyles = makeAppStyles()(theme => ({
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
  dispatch?: Dispatch<IAction>;
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

const mapEntityName = (entity: string) => {
  switch (entity) {
    case "VoucherProduct":
      return "voucher";
    case "MembershipProduct":
      return "membership";
    case "ArticleProduct":
      return "product";
    case "Voucher":
    case "Membership":
    case "Article":
      return "sale";
    default:
      return entity.toLowerCase();
  }
};

const DocumentShare: React.FC<Props> = ({
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

  const [availableOptions, setAvailableOptions] = useState<{ [O in DocumentShareOption]?: boolean }>(getAvailableOptions(groupedAttachmentRelations));

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

  const linkInput = useRef<any>(undefined);

  const { classes } = useStyles();

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
          {$t('no_attachments')}
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
          const entityName = mapEntityName(entity);

          return (
            <div key={entity}>
              <Typography
                component="div"
              >
                {`${relationsCount} ${mapEntityDisplayName(entity)}${relationsCount > 1 ? entity[entity.length - 1] === "s" ? "es" : "s" : ""}`}
                <IconButton
                  size="small"
                  color="secondary"
                  className={classes.linkButton}
                  onClick={() => onAttachmentCategoryClick(entityName, relationsMap)}
                >
                  <OpenInNew fontSize="inherit" color="primary"/>
                </IconButton>
                {portalEnabled
                  && !["Site", "Room"].includes(entity) && (
                    <IconButton
                      size="small"
                      color="secondary"
                      className={classes.linkButton}
                      onClick={() => onAttachmentPeopleClick(entity, documentSource.access, relations)}
                    >
                      <SupervisorAccount fontSize="inherit" className="highlight" color="primary"/>
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
              label={$t('attach_only_to_this_record')}
            />
          )}
      </div>
    );
  }, [documentSource.attachmentRelations, documentSource.shared, documentSource.access]);

  const websiteAvailable = !documentSource.attachmentRelations.length
    || (documentSource.attachmentRelations.length === 1
      && typesAllowedForWebsite.includes(documentSource.attachmentRelations[0].entity));

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
        <AlertTitle>{$t('who_can_view_this_document')}</AlertTitle>
        {SummaryLabel}
      </Alert>
      <Card className="mb-2" elevation={cardsElevation}>
        <CardHeader
          classes={cardHeaderClasses}
          avatar={(
            <Avatar className="activeAvatar">
              <Attachment/>
            </Avatar>
          )}
          title={$t('attached_to')}
        />
        <CardContent className={noPaper && "pl-0"} ref={attachmentRef}>
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
                  label={$t('shared_in_portal')}
                />
              )}
              avatar={(
                <Avatar className="activeAvatar">
                  <Directions/>
                </Avatar>
              )}
              title={$t('skills_oncourse')}
            />
            {contactRelated
              ? (
                <Collapse in={tutorsAndStudents}>
                  <CardContent className={noPaper && "pl-0"}>
                    <Typography>
                      {$t('shared_with')}
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
                        <CardContent className={noPaper && "pl-0"}>
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
                            label={$t('show_to_tutors')}
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
                            label={$t('show_to_students')}
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
              label={$t('shared_by_link')}
            />
          )}
          avatar={(
            <Avatar className="activeAvatar">
              <Link/>
            </Avatar>
          )}
          title={$t('shareable_link')}
        />
        <Collapse in={validUrl && linkOrPublic}>
          <CardContent className={noPaper && "pl-0"}>
            <div className="centeredFlex">
              <Typography color="textSecondary" className="flex-fill">
                <input ref={linkInput} readOnly className="codeArea" type="text" value={validUrl}/>
              </Typography>
              <Button color="primary" className="text-nowrap" onClick={onCopyLink}>
                {$t('copy_link')}
              </Button>
            </div>
          </CardContent>
        </Collapse>
        <Collapse in={!linkOrPublic}>
          <CardContent className={noPaper && "pl-0"}>
            <Alert severity="warning" icon={<LockOutlined/>}>
              {$t('document_can_not_be_accessed_by_direct_link')}
            </Alert>
          </CardContent>
        </Collapse>
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
                  label={$t('shared_with_website_visitors')}
                />
              )}
              avatar={(
                <Avatar className="activeAvatar">
                  <Language/>
                </Avatar>
              )}
              title={$t('website')}
            />
          </Card>
        )}
    </div>
  );
};

export default DocumentShare;
