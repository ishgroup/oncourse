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
import { compareAsc, parseISO } from "date-fns";
import { DocumentAttachmentRelation, DocumentVisibility } from "@api/model";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { StringKeyObject } from "../../../../../../model/common/CommomObjects";
import { DocumentExtended } from "../../../../../../model/common/Documents";
import { DocumentShareOption } from "../../../../../../model/entities/Document";

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

export const getDocumentThumbnail = (file: File): Promise<string> => {
  if (file.type.match(/image/)) {
    const reader = new FileReader();

    return new Promise(resolve => {
      reader.onload = () => resolve(reader.result);
      reader.readAsDataURL(file);
    }).then((result: any) => result.replace(`data:${file.type};base64,`, ""));
  }

  return new Promise(resolve => resolve(""));
};

export const iconSwitcher = iconType => {
  switch (iconType) {
    case "image/jpeg": {
      return props => FontAwesomeIcon({ fixedWidth: true, icon: "file-image", className: props.classes.wh100 });
    }

    case "image/gif": {
      return props => FontAwesomeIcon({ fixedWidth: true, icon: "file-image", className: props.classes.wh100 });
    }

    case "image/png": {
      return props => FontAwesomeIcon({ fixedWidth: true, icon: "file-image", className: props.classes.wh100 });
    }

    case "image/bmp": {
      return props => FontAwesomeIcon({ fixedWidth: true, icon: "file-image", className: props.classes.wh100 });
    }

    case "image/tiff": {
      return props => FontAwesomeIcon({ fixedWidth: true, icon: "file-image", className: props.classes.wh100 });
    }

    case "application/pdf": {
      return props => FontAwesomeIcon({ fixedWidth: true, icon: "file-pdf", className: props.classes.wh100 });
    }

    case "application/msword": {
      return props => FontAwesomeIcon({ fixedWidth: true, icon: "file-word", className: props.classes.wh100 });
    }

    case "application/vnd.ms-excel": {
      return props => FontAwesomeIcon({ fixedWidth: true, icon: "file-excel", className: props.classes.wh100 });
    }

    case "application/vnd.ms-powerpoint": {
      return props => FontAwesomeIcon({ fixedWidth: true, icon: "file-powerpoint", className: props.classes.wh100 });
    }

    case "application/zip": {
      return props => FontAwesomeIcon({ fixedWidth: true, icon: "file-archive", className: props.classes.wh100 });
    }

    case "application/x-gzip": {
      return props => FontAwesomeIcon({ fixedWidth: true, icon: "file-archive", className: props.classes.wh100 });
    }

    case "application/txt": {
      return props => FontAwesomeIcon({ fixedWidth: true, icon: "file-alt", className: props.classes.wh100 });
    }

    default: {
      return props => FontAwesomeIcon({ fixedWidth: true, icon: "file", className: props.classes.wh100 });
    }
  }
};

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
    access: "Public",
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
      label += attachmentRelations.length
        ? ", some students and tutors in skillsOnCourse portal"
        : ", all students and tutors in skillsOnCourse portal";
      break;
    }
    case "Link": {
      label += ", anyone with the link";
    }
  }
  return label;
};

export const getAvailableOptions = (relations: StringKeyObject<DocumentAttachmentRelation[]>): {[O in DocumentShareOption]?: boolean} => (
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

export const getLatestDocumentItem = (data: any[]) => {
  if (data && data.length === 1) return data[0];

  let latestDate = parseISO("1970-01-01T00:00:00.000Z");
  let resultElem = {};

  data.forEach(elem => {
    const result = compareAsc(parseISO(elem.added), latestDate);
    if (result === 1) {
      latestDate = parseISO(elem.added);
      resultElem = elem;
    }
  });

  return resultElem as any;
};