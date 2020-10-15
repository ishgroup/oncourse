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
  tagsAttachedToCourses = records*.course*.tags.flatten().unique()
  subj = courseClasses.keySet().collectMany { Course co -> co.tags }.findAll { Tag t -> NodeSpecialType.SUBJECTS.equals(t.getRoot().specialType) }.unique()[0]?.getRoot()
  if (subj) {
      addChild(subj, xml, courseClasses)
  }
}

def addChild(parent, xml, courseClasses) {
  parent.childTags.findAll{it.isWebVisible}.each { Tag child ->
    xml.mkp.yield("\n")
    xml.Tag() {
      if (tagsAttachedToCourses.contains(child)) {
          TagLevel2(child.name)
      } else {
          TagLevel1(child.name)
      }
      courseClasses.each { Course co, List<CourseClass> classes ->
        if (co.tags.contains(child)) {
          mkp.yield("\n")
          "Course"() {
            CourseName(co.name)
            mkp.yield("\n")
            Description(co.printedBrochureDescription)
            mkp.yield("\n")
            classes.each { CourseClass cc ->
              "Class"() {
                "Tutor"(cc.tutorRoles.collect{it.tutor.fullName}.join(", "))
                mkp.yield(" | ")

                // Suburb(${ cc?.room?.site?.suburb!=null ? "${cc?.room?.site?.suburb} | ": ""})
                // mkp.yield(" | ")

                if (cc?.room?.site?.suburb != null) {
                  Suburb(cc?.room?.site?.suburb + " | ")
                } else {
                  Suburb("")
                }

                ClassCode("Code "+ co.code + "-" + cc.code)
                mkp.yield(" | ")

                Sessions("${cc.sessionsCount} session${ cc.sessionsCount>1 ? 's':'' }")
                mkp.yield(" | ")
                Price("\$" + cc.feeIncGst?.toPlainString())
                mkp.yield(" | ")
                //Start/End dates
                if (!cc.startDateTime) {
                  Date("Self paced")
                } else if (cc.sessionsCount == 1) {
                  Date(cc?.startDateTime.format("EEE d MMMMM h:mma").replaceAll('AM','am').replaceAll('PM','pm'))
                } else {
                  Date(cc?.startDateTime.format("EEE d MMMMM h:mma").replaceAll('AM','am').replaceAll('PM','pm') + " - " + cc?.endDateTime.format("EEE d MMMMM h:mma").replaceAll('AM','am').replaceAll('PM','pm'))
                }
                mkp.yield("\n")
              }
            }
          }

        }
      }
      addChild(child, xml, courseClasses)
    }
  }
}

