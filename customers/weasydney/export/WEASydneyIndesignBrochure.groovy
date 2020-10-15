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
    courseClasses = records.groupBy { CourseClass cc -> cc.course }
    subj = courseClasses.keySet().collectMany { Course co -> co.tags }.findAll { Tag t -> NodeSpecialType.SUBJECTS.equals(t.getRoot().specialType) }.unique()[0]?.getRoot()

    if (subj) {
        "Tag"(subj.name) {
            addChild(subj, xml, courseClasses)
        }
    }
}

def addChild(parent, xml, courseClasses) {
    parent.childTags.each { Tag child ->
        xml.Tag(child.name) {
            courseClasses.each { Course co, List<CourseClass> classes ->
                if (co.tags.contains(child)) {
                    mkp.yield("\n")
                    "Course"() {
                        CourseName(co.name)
                        mkp.yield("\n")
                        Description(co.printedBrochureDescription)
                        mkp.yield("\n")
                        classHorizontalLine()

                        classCount = classes.size()
                        classes.eachWithIndex { CourseClass cc, index ->
                            mkp.yield("\n")
                            "Class"() {
                                cc.tutorRoles.each { CourseClassTutor tr ->
                                    "Tutor"(tr.tutor.fullName + " ")
                                    "Qual"(tr.tutor.contact.honorific)
                                }
                                if (!cc.sessions.isEmpty()) {
                                    mkp.yield("\n")
                                    mkp.yield((cc.sessionsCount>1) ? cc.sessionsCount.toString() + " meetings\t" : "1 meeting\t")
                                    mkp.yield(cc.startDateTime?.format("h:mma").toLowerCase().replace(":00", "").replace('12pm', '12 noon') +
                                            "&ndash;" + cc.endDateTime?.format("h:mma").toLowerCase().replace(":00", "").replace('12pm', '12 noon') +
                                            " " + cc.startDateTime?.format("EEE d MMMMM") )
                                }
                                def noCents = new java.text.DecimalFormat("###,##0")
                                mkp.yield("\n\t")
                                Price("\$" + noCents.format(cc.feeIncGst?.toBigDecimal() ) )
                                mkp.yield("\t")
                                Discounts() {
                                    cc.discounts.findAll { discount -> !discount.hideOnWeb && (discount.validTo>new Date() || !discount.validTo) }.each { Discount discount ->
                                        if (discount.discountDollar) {
                                            Price("Conc \$" + noCents.format((cc.feeExGst.toBigDecimal() - discount.discountDollar.toBigDecimal()) * 1.1) )
                                        } else {
                                            Price("Conc \$" + noCents.format(cc.feeIncGst.toBigDecimal() * ( 1 - discount.discountPercent.toBigDecimal()) ))
                                        }
                                    }
                                }
                            }
                            if (index < classCount-1) {
                                mkp.yield("\n")
                                mkp.yield("OR")
                                mkp.yield("\n")
                                classHorizontalLine()
                            }
                        }
                        mkp.yield("\n")
                        courseHorizontalLine()
                    } // end course
                }
            } // end classes
            addChild(child, xml, courseClasses)
        }
    }
}
