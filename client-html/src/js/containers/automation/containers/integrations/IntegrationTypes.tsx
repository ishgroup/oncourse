/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import $t from '@t';
import React from 'react';
import alchemer from '../../../../../images/alchemer.png';
import amazons3 from '../../../../../images/amazons3.png';
import azure from '../../../../../images/azure.png';
import canvas from '../../../../../images/canvas.png';
import cloudassess from '../../../../../images/cloudassess.png';
import coassemble from '../../../../../images/coassemble.png';
import googleclassroom from '../../../../../images/google_classroom.png';
import kronos from '../../../../../images/kronos.png';
import learndash from '../../../../../images/learndash.png';
import mailchimp from '../../../../../images/mailchimp.png';
import micropower from '../../../../../images/micropower.png';
import moodle from '../../../../../images/moodle.png';
import myob from '../../../../../images/myob.png';
import okta from '../../../../../images/okta.svg';
import servicensw from '../../../../../images/servicensw.svg';
import surveymonkey from '../../../../../images/surveymonkey.png';
import talentlms from '../../../../../images/talentlms.png';
import usi from '../../../../../images/usi.png';
import tsci from '../../../../../images/vet_student_loans.png';
import xero from '../../../../../images/xero.png';
import {
  IntegrationTypesEnum,
  IntegrationTypesModel
} from '../../../../model/automation/integrations/IntegrationSchema';
import { AlchemerForm } from './components/forms/AlchemerForm';
import { AmazonS3Form } from './components/forms/AmazonS3Form';
import { AzureForm } from './components/forms/AzureForm';
import { CanvasForm } from './components/forms/CanvasForm';
import { CloudAssessForm } from './components/forms/CloudAssessForm';
import { CoassembleForm } from './components/forms/CoassembleForm';
import { GoogleClassroomForm } from './components/forms/GoogleClassroomForm';
import { KronosForm } from './components/forms/KronosForm';
import { LearnDashForm } from './components/forms/LearnDashForm';
import { MailchimpForm } from './components/forms/MailchimpForm';
import { MicropowerForm } from './components/forms/MicropowerForm';
import { MoodleForm } from './components/forms/MoodleForm';
import { MYOBForm } from './components/forms/MYOBForm';
import { NSWForm } from './components/forms/NSWForm';
import { OktaForm } from './components/forms/OktaForm';
import { SurveyMonkeyForm } from './components/forms/SurveyMonkeyForm';
import { TalentLMSForm } from './components/forms/TalentLMSForm';
import { UsiForm } from './components/forms/UsiForm';
import { VetStudentLoansForm } from './components/forms/VetStudentLoansForm';
import { XeroForm } from './components/forms/XeroForm';

const types: IntegrationTypesModel = {
  [IntegrationTypesEnum.Moodle]: {
    name: IntegrationTypesEnum[IntegrationTypesEnum.Moodle],
    image: moodle,
    form: MoodleForm,
    formName: "MoodleForm",
    description:
      "Moodle is an open source Learning Managment System (LMS) for delivering training to students. onCourse can create enrolments in Moodle whenever a succesful enrolment is created in corresponding class in onCourse."
  },
  [IntegrationTypesEnum.Mailchimp]: {
    name: IntegrationTypesEnum[IntegrationTypesEnum.Mailchimp],
    form: MailchimpForm,
    formName: "MailchimpForm",
    image: mailchimp,
    description:
      "MailChimp helps you email the right people at the right time. Send automated emails based on customer behavior and preferences."
  },
  [IntegrationTypesEnum.SurveyMonkey]: {
    name: IntegrationTypesEnum[IntegrationTypesEnum.SurveyMonkey],
    form: SurveyMonkeyForm,
    formName: "SurveyMonkeyForm",
    image: surveymonkey,
    description:
      "Create and publish online surveys in minutes, and view results graphically in real time SurveyMonkey provides free online questionnaire and survey software."
  },
  [IntegrationTypesEnum.Alchemer]: {
    name: IntegrationTypesEnum[IntegrationTypesEnum.Alchemer],
    form: AlchemerForm,
    formName: "SurveyGizmoForm",
    image: alchemer,
    description:
      "Alchemer is an exeptionally powerful survey tool designed to make even the most advanced survey projects fun, easy & affordable."
  },
  [IntegrationTypesEnum.Xero]: {
    name: IntegrationTypesEnum[IntegrationTypesEnum.Xero],
    form: XeroForm,
    formName: "XeroForm",
    image: xero,
    description:
      "Xero is online accouting software for small business. Use Xero to manage invoicing bank reconciliation, bookkeeping & more."
  },
  [IntegrationTypesEnum.MYOB]: {
    name: IntegrationTypesEnum[IntegrationTypesEnum.MYOB],
    form: MYOBForm,
    formName: "MYOBForm",
    image: myob,
    description:
      "MYOB, Mind Your Own Business, is an Australian multinational corporation that provides tax, accounting and other services to small and medium businesses."
  },
  [IntegrationTypesEnum["Cloud Assess"]]: {
    name: IntegrationTypesEnum[IntegrationTypesEnum["Cloud Assess"]],
    form: CloudAssessForm,
    formName: "CloudAssessForm",
    image: cloudassess,
    description:
      "Cloud Assess is a flexible assessment platform designed specifically for vocational education assessment."
  },
  [IntegrationTypesEnum.Canvas]: {
    name: IntegrationTypesEnum[IntegrationTypesEnum.Canvas],
    form: CanvasForm,
    formName: "CanvasForm",
    image: canvas,
    description:
      "Canvas is an open source Learning Management System (LMS) for delivering training to students. This integration allows you to automatically create classes, students and enrolments in Canvas as needed."
  },
  [IntegrationTypesEnum.Micropower]: {
    name: IntegrationTypesEnum[IntegrationTypesEnum.Micropower],
    form: MicropowerForm,
    formName: "MicropowerForm",
    image: micropower,
    description: "Micropower is a powerful users and memberships management platform."
  },
  [IntegrationTypesEnum["USI Agency"]]: {
    name: IntegrationTypesEnum[IntegrationTypesEnum["USI Agency"]],
    form: UsiForm,
    formName: "UsiForm",
    image: usi,
    description:
      "Unique Student Identifier is a reference number for every student of accredited training in Australia. Automatically add and verify USIs for your students."
  },
  [IntegrationTypesEnum["VET Student Loans"]]: {
    name: IntegrationTypesEnum[IntegrationTypesEnum["VET Student Loans"]],
    formName: "VetStudentLoansForm",
    form: VetStudentLoansForm,
    image: tsci,
    description:
      "This integration feeds data from onCourse directly into the HEIMS data collection system (sometimes called TCSI)."
  },
  [IntegrationTypesEnum["Google Classroom"]]: {
    name: IntegrationTypesEnum[IntegrationTypesEnum["Google Classroom"]],
    form: GoogleClassroomForm,
    formName: "GoogleClassroomForm",
    image: googleclassroom,
    description:
      "Google Classroom is mission control for your classes. As a free service for teachers and students, you can create classes, distribute assignments, send feedback, and see everything in one place. Instant. Paperless. Easy."
  },
  [IntegrationTypesEnum.Coassemble]: {
    name: IntegrationTypesEnum[IntegrationTypesEnum.Coassemble],
    form: CoassembleForm,
    formName: "CoassembleForm",
    image: coassemble,
    description:
      "Coassemble is a powerful and intuitive platform for organizations to create and deliver quality online training. The first of its kind, Coassemble combines a user-friendly Learning Management System with outstanding rapid authoring capabilities. With Coassemble, organizations can create and deliver online training from a single integrated platform."
  },
  [IntegrationTypesEnum.TalentLMS]: {
    name: IntegrationTypesEnum[IntegrationTypesEnum.TalentLMS],
    form: TalentLMSForm,
    formName: "TalentLMSForm",
    image: talentlms,
    description:
      "TalentLMS is a cloud-based learning management system that provides an online tool to deliver your course materials."
  },
  [IntegrationTypesEnum.LearnDash]: {
    name: IntegrationTypesEnum[IntegrationTypesEnum.LearnDash],
    form: LearnDashForm,
    formName: "LearnDashForm",
    image: learndash,
    description:
      "LearnDash is a learning management system plugin for Wordpress websites only. It can provide an online space for you to deliver your course materials to students."
  },
  [IntegrationTypesEnum["Amazon S3"]]: {
    name: IntegrationTypesEnum[IntegrationTypesEnum["Amazon S3"]],
    form: AmazonS3Form,
    formName: "AmazonS3Form",
    image: amazons3,
    description:
      "Experience Reliability & Scalability With AWS Online Storage Solutions. Sign Up For Free"
  },
  [IntegrationTypesEnum["Microsoft Azure"]]: {
    name: IntegrationTypesEnum[IntegrationTypesEnum["Microsoft Azure"]],
    form: AzureForm,
    formName: "AzureForm",
    image: azure,
    description:
      "Massively-Scalable Object Storage for Unstructured Data. Try Blob Storage Free. Learn by Doing. 25+ Free Services. Build Your Idea. Get Free Credit. Try Free Products."
  },
  [IntegrationTypesEnum["Service NSW voucher"]]: {
    name: IntegrationTypesEnum[IntegrationTypesEnum["Service NSW voucher"]],
    form: NSWForm,
    formName: "NSWForm",
    image: servicensw,
    description: (
      <span>
        {$t('automatically_verify_and_redeem_the_nsw_government')}
        <a className="d-block mt-1" href="https://www.service.nsw.gov.au/campaign/creative-kids">
          https://www.service.nsw.gov.au/campaign/creative-kids
        </a>
      </span>
    )
  },
  [IntegrationTypesEnum.Kronos]: {
    name: IntegrationTypesEnum[IntegrationTypesEnum.Kronos],
    form: KronosForm,
    formName: "KronosForm",
    image: kronos,
    description:
      "With this integration you can send your tutor session rosters directly to your Kronos Workforce Ready system."
  },
  [IntegrationTypesEnum.Okta]: {
    name: IntegrationTypesEnum[IntegrationTypesEnum.Okta],
    form: OktaForm,
    formName: "OktaForm",
    image: okta,
    description:
      "Built to tackle both Consumer and SaaS Apps across every industry. Authenticate, authorize, and secure access for applications, devices, and users."
  },
};

export default types;