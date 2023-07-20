/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */


import { DocumentAttachmentRelation, DocumentVersion } from "@api/model";

export const isSingleContactAttachment = (attachmentRelations: DocumentAttachmentRelation[]) => attachmentRelations.length === 1
  && [
    'Contact',
    'Application',
    'Certificate',
    'Enrolment',
    'Invoice',
    'PriorLearning'].includes(attachmentRelations[0].entity);

export const isSingleCourseAttachment = (attachmentRelations: DocumentAttachmentRelation[]) => attachmentRelations.length === 1
  && attachmentRelations[0].entity === 'Course';

export const getDocumentShareSummary = (
  access: DocumentVisibility,
  attachmentRelations: DocumentAttachmentRelation[]
) => {
  let label = 'onCourse admin users';

  switch (access) {
    case 'Public': {
      label += isSingleCourseAttachment(attachmentRelations) ? ', all website visitors' : ', anyone with the link';
      break;
    }
    case 'Tutors only': {
      label += attachmentRelations.length
        ? ', some tutors in skillsOnCourse portal'
        : ', all tutors in skillsOnCourse portal';
      break;
    }
    case 'Tutors and enrolled students': {
      if (isSingleContactAttachment(attachmentRelations)) {
        label += ', ' + attachmentRelations.map(r =>
          r.relatedContacts.map(c => c.name));
        break;
      }
      if (attachmentRelations.length) {
        label += ', some students and tutors in skillsOnCourse portal';
      }
      break;
    }
    case 'Link': {
      label += ', anyone with the link';
    }
  }
  return label;
};

export const getInitialDocument = (file: File): Promise<DocumentExtended> => {
  const today = new Date().toISOString();

  const modified = new Date(file.lastModified).toISOString();

  return getDocumentThumbnail(file).then(thumbnail => ({
    id: null,
    name: null,
    created: today,
    modified,
    added: null,
    tags: [],
    thumbnail,
    description: '',
    access: 'Private',
    url: '',
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

export const getAvailableOptions = (relations: StringKeyObject<DocumentAttachmentRelation[]>): { [O in DocumentShareOption]?: boolean } => (
  {
    'PortalSharing':
      !Object.keys(relations).length
      || Boolean(relations['Application']
        || relations['Assessment']
        || relations['Certificate']
        || relations['Contact']
        || relations['Class']
        || relations['Course']
        || relations['Enrolment']
        || relations['Invoice']
        || relations['PriorLearning']),
    'Tutor&Student':
      !Object.keys(relations).length
      || Boolean(relations['Assessment']
        || relations['Class']
        || relations['Course'])
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