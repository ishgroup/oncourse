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
    parent.childTags.sort{ it.name }.each { Tag child ->
        xml.mkp.yield("\n")
        xml.Tag(child.name) {
            courseClasses.each { Course co, List<CourseClass> classes ->
                if (co.tags.contains(child)) {
                    def thisFee = 0
                    mkp.yield("\n")
                    Course() {
                        CourseName(co.name?.replace("  ", " "))
                        mkp.yield("\n")
                        Description(co.printedBrochureDescription?.replace("  ", " "))

                        classes.each { CourseClass cc ->

                            Class() {
                                if (!cc.sessions.isEmpty()) {
                                    mkp.yield("\n\t")
                                    Session() {
                                        mkp.yield(cc.startDateTime?.format("EE") + " starts " + cc.startDateTime?.format("dd MMM") + ",")
                                        mkp.yield("\t")
                                        mkp.yield(cc.startDateTime?.format("h:mm a").toLowerCase().replace(":00", "") + " - " + cc.endDateTime?.format("h:mm a").toLowerCase().replace(":00", "") )
                                        mkp.yield("\n\t")
                                        mkp.yield(cc.sessionsCount.toString() + " session")
                                        mkp.yield(cc.sessionsCount>1 ? "s, " : ", ")
                                        mkp.yield("\t")
                                        Site(cc.room?.site?.name)

                                    }

                                if (thisFee != cc.feeIncGst) {
                                    def noCents = new java.text.DecimalFormat("###,##0")
                                    mkp.yield("\n")
                                    Fee("\$" + noCents.format(cc.feeIncGst?.toBigDecimal())  )
                                }
                                thisFee = cc.feeIncGst

                                cc.tutorRoles.each { CourseClassTutor tr ->
                                    mkp.yield("\t")
                                    Tutor(tr.tutor.fullName.replace("  ", " "))
                                }

                                
                                } 
                            }
                        }
                        mkp.yield("\n")
                        courseHorizontalLine()
                    }
                }
            }
            addChild(child, xml, courseClasses)
        }
    }
}
