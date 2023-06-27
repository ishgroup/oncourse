/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import { compareAsc, parseISO } from "date-fns";
import { DocumentAttachmentRelation, DocumentVersion, DocumentVisibility } from "@api/model";
import { StringKeyObject } from "../../../../../../model/common/CommomObjects";
import { DocumentExtended } from "../../../../../../model/common/Documents";
import { DocumentShareOption } from "../../../../../../model/entities/Document";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";

export const formatDocumentSize = (size: number): string => {
  if (size < Math.pow(1024, 2)) {
    return `${Math.round(size / 1024)} kb`;
  }
  if (size < Math.pow(1024, 3)) {
    return `${Math.round(size / 1024 / 1024)} Mb`;
  }
  if (size < Math.pow(1024, 4)) {
    return `${Math.round(size / 1024 / 1024 / 1024)} Gb`;
  }

  return size.toString();
};

export const getDocumentContent = (file: File): Promise<string> => {
  const reader = new FileReader();

  return new Promise(resolve => {
    reader.onload = () => resolve(reader.result);
    reader.readAsDataURL(file);
  }).then((result: any) => result.replace(`data:${file.type};base64,`, ""));

};

export const getDocumentThumbnail = (file: File): Promise<string> => {
  if (file.type.match(/image/)) {
    return getDocumentContent(file);
  }

  return new Promise(resolve => resolve(""));
};

const getIcon = type => {
  switch (type) {
    case "image/tiff":
    case "image/bmp":
    case "image/png":
    case "image/gif":
    case "image/jpeg": 
      return "file-image";
    case "application/pdf": 
      return "file-pdf";
    case "application/msword": 
      return "file-word";
    case "application/vnd.ms-excel": 
      return "file-excel";
    case "application/vnd.ms-powerpoint":
      return "file-powerpoint";
    case "application/zip":
      return "file-archive";
    case "application/x-gzip":
      return "file-archive";
    case "application/txt":
      return "file-alt";
    default:
      return "file";
  }
};

export const FileTypeIcon = ({ mimeType }) => <FontAwesomeIcon icon={getIcon(mimeType)} fixedWidth className="w-100 h-100" />;

export const getInitialDocument = (file: File): Promise<DocumentExtended> => {
  const today = new Date().toISOString();

  const modified = new Date(file.lastModified).toISOString();

  return getDocumentThumbnail(file).then(thumbnail => ({
    id: null,
    name: null,
    versionId: null,
    created: today,
    modified,
    added: null,
    tags: [],
    thumbnail,
    description: "",
    access: "Private",
    url: "",
    shared: true,
    content: file,
    versions: [
      {
        id: null,
        added: today,
        createdBy: null,
        fileName: file.name,
        mimeType: file.type,
        size: formatDocumentSize(file.size)
      }
    ],
    removed: false,
    attachedRecordsCount: 0,
    attachmentRelations: []
  }));
};

export const isSingleContactAttachment = (attachmentRelations: DocumentAttachmentRelation[]) => attachmentRelations.length === 1
  && [
    "Contact",
    "Application",
    "Certificate",
    "Enrolment",
    "Invoice",
    "PriorLearning"].includes(attachmentRelations[0].entity);

export const isSingleCourseAttachment = (attachmentRelations: DocumentAttachmentRelation[]) => attachmentRelations.length === 1
  && attachmentRelations[0].entity === "Course";

export const getDocumentShareSummary = (
  access: DocumentVisibility,
  attachmentRelations: DocumentAttachmentRelation[]
) => {
  let label = "onCourse admin users";

  switch (access) {
    case "Public": {
      label += isSingleCourseAttachment(attachmentRelations) ? ", all website visitors" : ", anyone with the link";
      break;
    }
    case "Tutors only": {
      label += attachmentRelations.length
        ? ", some tutors in skillsOnCourse portal"
        : ", all tutors in skillsOnCourse portal";
      break;
    }
    case "Tutors and enrolled students": {
      if (isSingleContactAttachment(attachmentRelations)) {
        label += ", " + attachmentRelations.map(r =>
          r.relatedContacts.map(c => c.name));
        break;
      }
      if (attachmentRelations.length) {
        label += ", some students and tutors in skillsOnCourse portal";
      }
      break;
    }
    case "Link": {
      label += ", anyone with the link";
    }
  }
  return label;
};

export const getAvailableOptions = (relations: StringKeyObject<DocumentAttachmentRelation[]>): { [O in DocumentShareOption]?: boolean } => (
  {
    "PortalSharing":
      !Object.keys(relations).length
      || Boolean(relations["Application"]
      || relations["Assessment"]
      || relations["Certificate"]
      || relations["Contact"]
      || relations["Class"]
      || relations["Course"]
      || relations["Enrolment"]
      || relations["Invoice"]
      || relations["PriorLearning"]),
    "Tutor&Student":
      !Object.keys(relations).length
      || Boolean(relations["Assessment"]
      || relations["Class"]
      || relations["Course"])
  }
);

export const groupAttachmentsByEntity = (attachmentRelations: DocumentAttachmentRelation[]) =>
  attachmentRelations.reduce<StringKeyObject<DocumentAttachmentRelation[]>>((acc, cur) => {
  if (Array.isArray(acc[cur.entity])) {
    acc[cur.entity].push(cur);
  } else {
    acc[cur.entity] = [cur];
  }
  return acc;
}, {});

export const getLatestDocumentItem = (data: DocumentVersion[]) => {
  if (data && data.length === 1) return data[0];
  return data.find( elem => elem.current);
};