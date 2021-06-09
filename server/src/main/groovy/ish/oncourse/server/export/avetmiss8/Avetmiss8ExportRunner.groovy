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

package ish.oncourse.server.export.avetmiss8

import groovy.transform.CompileStatic
import ish.common.types.OutcomeStatus
import ish.oncourse.common.ExportJurisdiction
import ish.oncourse.entity.services.CertificateService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.FundingSource
import ish.oncourse.server.cayenne.Outcome
import ish.oncourse.server.cayenne.Room
import ish.oncourse.server.cayenne.Site
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.export.avetmiss.AvetmissExport
import org.apache.cayenne.query.SelectById

import static ish.oncourse.server.export.avetmiss.AvetmissExport.*
import ish.oncourse.server.export.avetmiss.AvetmissExportResult
import ish.persistence.GetInExpression
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.ResultBatchIterator
import org.apache.cayenne.ResultIterator
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.time.LocalDate

@CompileStatic
class Avetmiss8ExportRunner {

    private static final Logger logger = LogManager.getLogger()

    private static final AvetmissExport[] states = [
            AVETMISS_QLD,
            AVETMISS_NSW,
            AVETMISS_VIC,
            AVETMISS_TAS,
            AVETMISS_ACT,
            AVETMISS_WA,
            AVETMISS_SA,
            AVETMISS_NT
    ]

    static Set<Outcome> getOutcomes(List<Long> classIds,
                                    List<Long> enrolmentIds,
                                    List<Long> fundingContracts,
                                    Boolean includeLinkedOutcomes,
                                    LocalDate outcomesEnd,
                                    LocalDate outcomesStart,
                                    List<AvetmissExport> fee,
                                    ExportJurisdiction jurisdiction,
                                    ObjectContext context) {
        Set<Outcome> resultOutcomes = []

        // Open AVTEMISS export from class list view
        if (!classIds.empty) {
            logger.info("Started AVETMISS export with {} classes.", classIds.size())
            resultOutcomes += ObjectSelect.query(Outcome)
                    .where(Outcome.ENROLMENT.dot(Enrolment.COURSE_CLASS).dot(CourseClass.ID).in(classIds))
                    .select(context)
            return resultOutcomes
        }

        // Open AVTEMISS export from enrolment list view
        if (!enrolmentIds.empty) {
            logger.info("Started AVETMISS export with {} enrolments.", enrolmentIds.size())
            resultOutcomes += ObjectSelect.query(Outcome)
                    .where(Outcome.ENROLMENT.dot(Enrolment.ID).in(enrolmentIds))
                    .select(context)
            return resultOutcomes
        }

        outcomesStart = outcomesStart ?: LocalDate.now()
        outcomesEnd = outcomesEnd ?: LocalDate.now()

        Expression baseFilter = Outcome.ENROLMENT.dot(Enrolment.SUPPRESS_AVETMISS_EXPORT).isFalse()
                .andExp(Outcome.ENROLMENT.dot(Enrolment.COURSE_CLASS).dot(CourseClass.SUPPRESS_AVETMISS_EXPORT).isFalse())
                .andExp(Outcome.START_DATE.lte(outcomesEnd))
                .andExp(Outcome.END_DATE.gte(outcomesStart))

        List<Long> fundingContractIds = fundingContracts

        if (!fundingContractIds.empty) {
            resultOutcomes += (ObjectSelect.query(Outcome.class)
                    .where(baseFilter)
                    .and(Outcome.ENROLMENT.dot(Enrolment.RELATED_FUNDING_SOURCE).dot(FundingSource.ID).in(fundingContractIds)))
                    .select(context)
        }

        List<String> stateNames = fee.findAll { it in states }.collect { it.stateName }

        if (!stateNames.empty) {
            resultOutcomes += ObjectSelect.query(Outcome.class)
                    .where(baseFilter)
                    .and(Outcome.ENROLMENT.dot(Enrolment.COURSE_CLASS).outer().dot(CourseClass.ROOM).outer().dot(Room.SITE).outer().dot(Site.STATE).in(stateNames))
                    .and(Outcome.ENROLMENT.dot(Enrolment.COURSE_CLASS).dot(CourseClass.COURSE).dot(Course.IS_VET).isTrue())
                    .and(Outcome.ENROLMENT.dot(Enrolment.RELATED_FUNDING_SOURCE).isNull())
                    .select(context)
        }

        if (AVETMISS_NO_STATE in fee) {
            resultOutcomes += ObjectSelect.query(Outcome.class)
                    .where(baseFilter)
                    .and(Outcome.ENROLMENT.dot(Enrolment.COURSE_CLASS).outer().dot(CourseClass.ROOM).outer().dot(Room.SITE).outer().dot(Site.STATE).isNull())
                    .and(Outcome.ENROLMENT.dot(Enrolment.COURSE_CLASS).dot(CourseClass.COURSE).dot(Course.IS_VET).isTrue())
                    .and(Outcome.ENROLMENT.dot(Enrolment.RELATED_FUNDING_SOURCE).isNull())
                    .select(context)
        }

        if (includeLinkedOutcomes) {
            // get all outcomes from enrolments joined to the outcomes we already found
            resultOutcomes = resultOutcomes*.enrolment.toSet()*.outcomes.flatten().toSet() as Set<Outcome>

            // get all outcomes with the same purchasing contract ID as the outcomes we already have
            Set<String> contractIds = resultOutcomes.findResults { o -> StringUtils.trimToNull(o.outcomeVetPurchasingContractID) != null ? o.outcomeVetPurchasingContractID : null }.unique().toSet()
            if (!contractIds.empty) {
                resultOutcomes += ObjectSelect.query(Outcome)
                        .where(Outcome.VET_PURCHASING_CONTRACT_ID.in(contractIds))
                        .select(context)
            }

        }

        if (!(jurisdiction in [ExportJurisdiction.SA, ExportJurisdiction.SMART])) {
            resultOutcomes = resultOutcomes.findAll { it.status != OutcomeStatus.STATUS_ASSESSABLE_DET_DID_NOT_START }
        }

        if (!stateNames.empty || AVETMISS_NO_STATE in fee) {
            // Add prior learning outcomes
            resultOutcomes += ObjectSelect.query(Outcome.class)
                    .where(Outcome.PRIOR_LEARNING.isNotNull())
                    .and(Outcome.START_DATE.lte(outcomesEnd))
                    .and(Outcome.END_DATE.gte(outcomesStart))
                    .select(context)
        }
        return resultOutcomes
    }

    /**
     * Run the actual export which will create files in a zip
     * @param context
     * @param certificateService
     * @param preferenceController
     * @param jurisdiction
     * @param defaultOutcome
     * @param overriddenEndDate
     * @param exportEndDate
     * @param outcomeIds
     * @return
     */
    static AvetmissExportResult export(ObjectContext context,
                                       CertificateService certificateService,
                                       PreferenceController preferenceController,
                                       ExportJurisdiction jurisdiction,
                                       OutcomeStatus defaultOutcome,
                                       LocalDate overriddenEndDate,
                                       LocalDate exportEndDate,
                                       Boolean noAssessments,
                                       List<Long> outcomeIds) {

        logger.info("Getting {} outcomes from the database.", outcomeIds.size())



        def result = new AvetmissExportResult()
        result.exportEndDate = exportEndDate
        result.defaultOutcome = defaultOutcome
        result.overriddenEndDate = overriddenEndDate
        result.ignoreAssessments = noAssessments
        // get default administrative site
        result.adminSite = ObjectSelect.query(Site.class).where(Site.IS_ADMINISTRATION_CENTRE.isTrue()).selectFirst(context)

        // One record for the college
        new Avetmiss010Factory(result, jurisdiction, preferenceController).createLine()

        logger.info("AVETMISS export for {} outcomes. Now constructing outcome objects...", outcomeIds.size())
        
        outcomeIds.parallelStream().forEach() { id ->
            Outcome outcome = SelectById.query(Outcome.class, id)
                    .prefetch(Outcome.ENROLMENT.joint())
                    .prefetch(Outcome.ENROLMENT.dot(Enrolment.STUDENT).joint())
                    .prefetch(Outcome.ENROLMENT.dot(Enrolment.STUDENT).dot(Student.CONTACT).joint())
                    .prefetch(Outcome.ENROLMENT.dot(Enrolment.COURSE_CLASS).joint())
                    .prefetch(Outcome.ENROLMENT.dot(Enrolment.COURSE_CLASS).dot(CourseClass.COURSE).joint())
                    .prefetch(Outcome.ENROLMENT.dot(Enrolment.COURSE_CLASS).dot(CourseClass.COURSE).dot(Course.QUALIFICATION).joint())
                    .selectOne(context)
            

            new Avetmiss120Factory(result, jurisdiction, preferenceController).createLine(outcome)
            Avetmiss130Factory a130 = new Avetmiss130Factory(result, jurisdiction, preferenceController)
            a130.certificateService = certificateService
            a130.createLine(outcome)
            if (ExportJurisdiction.VIC == jurisdiction) {
                a130.createForVIC(outcome)
            }

        }
        return result
    }
}
