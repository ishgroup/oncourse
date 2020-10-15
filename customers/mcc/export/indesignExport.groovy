/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

// We need to create a new builder here with an IndentPrinter that doesn't indent or add new lines
// This is because inDesign is sensitive to where those new lines are added
import groovy.xml.MarkupBuilder
import ish.common.types.NodeSpecialType

xml = new MarkupBuilder(new IndentPrinter(output, "", false, false))
xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

xml.brochure {
    courseClasses = records.groupBy { it.course }.sort { it.key.name }
    subj = courseClasses.keySet().collectMany { Course co -> co.tags }.findAll { Tag t -> NodeSpecialType.SUBJECTS.equals(t.getRoot().specialType) }.unique()[0]?.getRoot()

    if (subj) {
        "Tag"(subj.name) {
           addChild(subj, xml, courseClasses)
        }
    }
}

def addChild(parent, xml, courseClasses) {
    parent.childTags.each { Tag child ->
        xml.mkp.yield("\n")
        xml.Tag(child.name) {
            courseClasses.each { Course co, List<CourseClass> classes ->
                if (co.tags.contains(child)) {
                    def thisFee = 0
                    mkp.yield("\n")
                    Course() {
                        mkp.yield("\n")
                        classes.each { CourseClass cc ->

                            Class() {
                                mkp.yield(cc.course.name?.toString())
                                if (thisFee != cc.feeIncGst) {
                                    def noCents = new java.text.DecimalFormat("###,##0")
                                    mkp.yield("\t")
                                    Fee("Fee\t\$" + noCents.format(cc.feeIncGst?.toBigDecimal())  )
                                }
                                thisFee = cc.feeIncGst
                                if (!cc.sessions.isEmpty()) {
                                    mkp.yield("\t")
                                    Session() {
                                        mkp.yield(cc.startDateTime?.format("EEE dd MMM") + " - " + cc.endDateTime?.format("dd MMM"))
                                        mkp.yield("\t")
                                        mkp.yield(cc.startDateTime?.format("h:mma").toLowerCase().replace(":00","") + " - " + cc.endDateTime?.format("h:mma").toLowerCase().replace(":00","") )
                                        mkp.yield("\t\t")
                                        mkp.yield(cc.sessionsCount.toString())
                                    }

                                }
                                cc.tutorRoles.each { CourseClassTutor tr ->
                                    mkp.yield("\t")
                                    Tutor(tr.tutor.fullName.replace("  ", " "))
                                }
                                mkp.yield("\t")
                                Site(cc.room?.site?.name)

                            }
                            mkp.yield("\n")
                        }
                    }
                }
            }
            addChild(child, xml, courseClasses)
        }
    }
}
