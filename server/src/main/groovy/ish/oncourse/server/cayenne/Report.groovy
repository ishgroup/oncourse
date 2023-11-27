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

package ish.oncourse.server.cayenne

import ish.common.types.AutomationStatus
import ish.common.types.NodeType
import ish.oncourse.API
import ish.oncourse.server.cayenne.glue._Report
import ish.oncourse.server.print.AbstractReportDataSource
import ish.oncourse.server.report.ReportService
import ish.oncourse.server.upgrades.DataPopulation
import ish.print.PageBreakType
import org.apache.cxf.common.util.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import javax.annotation.Nullable
/**
 * Report template which can be printed or saved to PDF.
 *
 * Reports are usually printed from onCourse user interface, but can also be generated from within a script. For example:
 *
 * ```
 * 	report {
 * 	    keycode "ish.onCourse.studentDetailsReport"
 * 	    records ObjectSelect.query(Contact).select(args.context)
 * 	}
 * ```
 * Optionaly you can use extended list of  arguments:
 *
 * ```
 * 	report {
 * 	    keycode "ish.onCourse.studentDetailsReport"
 * 	    entity "Contact"
 * 	    records ObjectSelect.query(Contact).select(args.context)
 * 	    background  "backgroundName"
 * 	    param "dateRange_from" :  Date.parse("yyyy-MM-dd", "2016-01-01"),  "dateRange_to" : Date.parse("yyyy-MM-dd", "2016-01-02")
 * 	}
 * ```
 *
 * 'param' argument is Map<String, Object> which currently acceptable for dateRange_from/dateRange_to values.

 * This parameters are required for some reports when you need to determine reportable period (the same possibility presented on user interface).
 * If client app run in different time zone with server app you need to provide date range parameters
 * in corresponded server time zone to be sure that output report result will correct:  `Date.parse("yyyy-MM-dd Z", "2016-01-01 +1100")`
 *
 * If you want to print report from records source which has different type with report entity type (Account Transaction report from contacts records for example)
 * then you need provide 'entity' (entity "Contact")  parameter
 *
 * Keep 'param' map as generic (Object data type) to possible extension of acceptable arguments in future.
 *
 * 'background' argument is name of available ReportOverlay records which stored in onCourse system.
 *
 *
 * This will return PDF report represented in binary format, which you can for example save to a file or send in an email as an attachment:
 *
 * ```
 * smtp {
 * 		from "test@test.com"
 *		to "test@test.com"
 *		subject "report example"
 *		content "report printing example"
 *		attachment "report.pdf", "application/pdf", report {
 *          keycode "ish.onCourse.studentDetailsReport"
 *          records ObjectSelect.query(Contact).select(args.context)
 *		}
 * }
 * ```
 */
@API
class Report extends _Report implements AutomationTrait {


    private static final Logger logger = LogManager.getLogger()

    public static String IS_VISIBLE_PROPERTY = "isVisible"

    /**
     * Reports can be a list type where every record appears right after the previous, breaking pages only when we run
     * out of paper. Or they can have a page break after each record. For example, each invoice might start on a new page.
     *
     * @return the type of page breaking for this report
     */
    @Nullable
    @API
    PageBreakType getPageBreakType() {
        StringBuffer buff = new StringBuffer(getReport())
        String value =  DataPopulation.getPropertyFromXml(buff, AbstractReportDataSource.PRINT_OBJECT_ON_SEPARATE_PAGE_PROPERTY)

        if (value != null) {
            PageBreakType type = PageBreakType.getByAlias(value)

            if (type != null) {
                return type
            }
        }

        return PageBreakType.OFF
    }

    /**
     * A report can be set to automatically chain another report to immediately follow it and print together.
     *
     * @return the keycode of another report which follows this one
     */
    @Nullable
    @API
    String getFollowingReports() {
        StringBuffer buff = new StringBuffer(getReport())
        return DataPopulation.getPropertyFromXml(buff, AbstractReportDataSource.FOLLOWING_REPORTS_PROPERTY)
    }

    @Deprecated
    boolean hasFollowingReports() {
        return !StringUtils.isEmpty(getFollowingReports())
    }

    /**
     * @return report as string
     */
    @Nullable
    @API
    String getReport() {
        byte[] data

        data = getData()
        if (data != null && data.length > 0) {
            return new String(data)
        }
        return null
    }

    /**
     * @param aReport
     */
    void setReport(@Nullable final String aReport) {
        if (aReport != null) {
            setData(aReport.getBytes())
        } else {
            setData(null)
        }
    }

    private void compileReport() {
        byte[] compiledReport = null
        try {
            compiledReport = ReportService.compileReport(getReport())
        } catch (Exception e) {
            logger.warn("Report ({}) compilation failed", getKeyCode(), e)
        }
    }

    /**
     * @return the date and time this record was created
     */
    @API
    @Override
    Date getCreatedOn() {
        return super.getCreatedOn()
    }

    /**
     * @return report content as byte array
     */
    @Nonnull
    @API
    @Override
    byte[] getData() {
        return super.getData()
    }

    /**
     * @return description of this report
     */
    @API
    @Override
    String getDescription() {
        return super.getDescription()
    }

    /**
     * @return root entity for this report
     */
    @Nonnull
    @API
    @Override
    String getEntity() {
        return super.getEntity()
    }


    /**
     * @return true if report is shown in the print dialog selection list (mainly used to hide subreports)
     */
    @Nonnull
    @API
    Boolean getIsVisible() {
        return super.automationStatus == AutomationStatus.ENABLED
    }

    /**
     * @return unique key code of this report
     */
    @Nonnull
    @API
    @Override
    String getKeyCode() {
        return super.getKeyCode()
    }

    /**
     * @return the date and time this record was modified
     */
    @API
    @Override
    Date getModifiedOn() {
        return super.getModifiedOn()
    }

    /**
     * @return name of this report
     */
    @Nonnull
    @API
    @Override
    String getName() {
        return super.getName()
    }

    /**
     * @deprecated this is now obsolete since we use jasper's embedded ordering functionality,
     * but some old reports are still using this field
     */
    @Deprecated
    @Override
    String getSortOn() {
        return super.getSortOn()
    }

    /**
     * @return body of this report
     */
    @Nonnull
    @API
    @Override
    String getBody() {
        return getReport()
    }

    void setBody(String body) {
        setReport(body)
    }

    /**
     * @return The list of tags assigned to report
     */
    @Nonnull
    @API
    List<Tag> getTags() {
        List<Tag> tagList = new ArrayList<>(getTaggingRelations().size())
        for (ReportTagRelation relation : getTaggingRelations()) {
            if(relation.tag?.nodeType?.equals(NodeType.TAG))
                tagList.add(relation.getTag())
        }
        return tagList
    }

    @Override
    Class<? extends TagRelation> getTagRelationClass() {
        return CourseTagRelation.class
    }
    /**
     * @return all the automation bindings for this record, including both variables and options
     */
    @Nonnull
    @Override
    List<ReportAutomationBinding> getAutomationBindings() {
        return super.automationBindings
    }

    Class<? extends AutomationBinding> getAutomationBindingClass() {
        return ReportAutomationBinding
    }
}
