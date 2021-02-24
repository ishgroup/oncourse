/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.plugin.cloudassess

import ish.oncourse.API
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.scripting.ScriptClosure
import ish.oncourse.server.scripting.ScriptClosureTrait

/**
 * Integration allows us to establish interaction between CloudAssess and onCourse enrol system.
 *
 * Add enrolments to CloudAssess automatically as they are enrolled in onCourse.*
 * ```
 * cloudassess {
 *     name "cloud assess integration 1"
 *     action "enrol"
 *     enrolment e
 * }
 * ```
 *
 * Name property here is the name of CloudAssess integration which should be created in onCourse beforehand.
 *
 * Specify a CloudAssess course code which is different to the onCourse course code with the options 'code' parameter.
 * ```
 * cloudassess {
 *     name "cloud assess integration 1"
 *     action "enrol"
 *     enrolment e
 *     code "ABCT.2017"
 * }
 * ```
 *
 * *Create a course in CloudAssess.*
 *
 * ```
 * cloudassess {
 *     name "cloud assess integration 1"
 *     action "course"
 *     courseName "ISH Test Course 1"
 *     code "ABCT.2017"
 *     qualification "SITHFAB201"
 * }
 * ```
 * This syntax will create a course with the name and code as specified. CloudAssess has a concept of "qualification"
 * which is different to the onCourse Qualification. In CloudAssess it represents something more like a "course template".
 *
 * If this course code already exists in CloudAssess, then this script does nothing and doesn't throw an error.
 *
 * Because CloudAssess doesn't cope well with thousands of enrolments in a single course, this feature is useful to split
 * up enrolments into one CloudAssess course per month (or any other period).
 *
 * *Fetch outcomes from CloudAssess*
 *
 * ```
 * updated_outcomes = cloudassess {
 *     name "cloud assess integration 1"
 *     action "outcomes"
 *     since now - 7
 * }
 * ```
 * This will return a list of outcomes modified in CloudAssess in the last 7 days. These outcomes are in a different context to the one
 * the script runs in, so you'll need to copy them across in order to save the data in onCourse.
 *
 * ```
 * updated_outcomes.each() { o ->
 *     def o1 = args.context.localObject(o)
 *     o1.status = o.status
 * }
 * args.context.commitChanges()
 * ```
 */
@API
@ScriptClosure(key = "cloudassess", integration = CloudAssessIntegration)
class CloudAssessScriptClosure implements ScriptClosureTrait<CloudAssessIntegration> {
    static final ENROL = "enrol"
    static final COURSE = "course"
    static final OUTCOMES = "outcomes"

    String action

    Enrolment enrolment

    String code
    String courseName
    String qualification

    Date since

    /**
     * Set CloudAssess action: "enrol" is only supported at this time.
     *
     * @param action list action string
     */
    @API
    void action(String action) {
        this.action = action
    }

    /**
     * Set enrolment which will be created in CloudAssess.
     *
     * @param enrolment integrating enrolment
     */
    @API
    void enrolment(Enrolment enrolment) {
        this.enrolment = enrolment
    }

    @API
    void code(String code) {
        this.code = code
    }

    @API
    void courseName(String courseName) {
        this.courseName = courseName
    }

    @API
    void qualification(String qualification) {
        this.qualification = qualification
    }

    @API
    void since(Date since) {
        this.since = since
    }
    /**
     * Execute the closure with the configuration from the passed integration
     *
     * @param integration
     */
    @Override
    Object execute(CloudAssessIntegration integration) {
        switch (action) {
            case CloudAssessScriptClosure.ENROL:
                integration.enrol(enrolment, code)
                break
            case CloudAssessScriptClosure.COURSE:
                integration.course(code, courseName, qualification)
                break
            case CloudAssessScriptClosure.OUTCOMES:
                integration.outcomes(since)
                break
            default:
                throw new IllegalArgumentException("Unknown action: ${action}")
        }
        return null
    }
}
