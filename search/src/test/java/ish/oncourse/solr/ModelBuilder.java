package ish.oncourse.solr;

import ish.common.types.CourseEnrolmentType;
import ish.oncourse.model.*;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;
import java.util.Date;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class ModelBuilder {
    private ObjectContext context;
    private College college;

    public Session createSession(CourseClass courseClass) {
        Session session = context.newObject(Session.class);
        session.setCollege(college);
        session.setCourseClass(courseClass);
        session.setStartDate(DateUtils.addDays(new Date(), 2));
        session.setEndDate(DateUtils.addDays(new Date(), 3));
        session.setCreated(new Date());
        session.setModified(new Date());
        context.commitChanges();
        return session;
    }

    public Site createSite() {
        Site site = context.newObject(Site.class);
        site.setName("Site " + RandomStringUtils.random(5));
        site.setCollege(college);
        site.setModified(new Date());
        site.setCreated(new Date());
        site.setIsWebVisible(true);
        site.setIsVirtual(false);
        site.setLatitude(BigDecimal.valueOf(-RandomUtils.nextDouble(31.8, 31.9)));
        site.setLongitude(BigDecimal.valueOf(RandomUtils.nextDouble(115.8, 115.9)));
        return site;
    }

    public Room createRoom(Site site) {
        Room room = context.newObject(Room.class);
        room.setCollege(college);
        room.setModified(new Date());
        room.setCreated(new Date());
        room.setCapacity(10);
        room.setName("Room " + RandomStringUtils.random(5));
        room.setSite(site);
        return room;
    }

    public Site createSiteForCourseClass(CourseClass courseClass) {
        Site site = createSite();
        Room room = createRoom(site);
        courseClass.setRoom(room);
        context.commitChanges();
        return site;
    }

    public CourseClass createCourseClass(Course course) {
        CourseClass courseClass = context.newObject(CourseClass.class);
        courseClass.setCollege(college);
        courseClass.setCode(RandomStringUtils.randomAlphanumeric(5));
        courseClass.setCourse(course);
        courseClass.setIsWebVisible(true);
        courseClass.setIsActive(true);
        courseClass.setMaximumPlaces(5);
        courseClass.setIsDistantLearningCourse(false);
        courseClass.setModified(new Date());
        courseClass.setCreated(new Date());
        context.commitChanges();
        return courseClass;
    }

    public Course createCourse() throws InterruptedException {
        Date now = new Date();
        Course course = context.newObject(Course.class);
        course.setCollege(college);
        course.setCode(RandomStringUtils.randomAlphabetic(10));
        course.setName("Course" + RandomStringUtils.randomAlphabetic(10) + "name");
        course.setIsWebVisible(true);
        course.setIsVETCourse(false);
        course.setIsSufficientForQualification(false);
        course.setEnrolmentType(CourseEnrolmentType.OPEN_FOR_ENROLMENT);
        course.setCreated(now);
        course.setModified(now);

        context.commitChanges();
        return course;
    }

    public Site createSiteForSession(Session session) {
        Site site = this.createSite();
        Room room = this.createRoom(site);
        session.setRoom(room);
        context.commitChanges();
        return site;
    }

    public Tag createTag() {
        Tag tag = context.newObject(Tag.class);
        tag.setCollege(college);
        tag.setModified(new Date());
        tag.setCreated(new Date());
        tag.setName(RandomStringUtils.randomAlphabetic(10));
        tag.setIsWebVisible(true);
        tag.setDetail(RandomStringUtils.randomAlphabetic(255));
        tag.setShortName(RandomStringUtils.randomAlphabetic(5));
        context.commitChanges();
        return tag;
    }

    public TaggableTag linkTagToCourse(Course course, Tag tag) {
        Taggable taggable = context.newObject(Taggable.class);
        taggable.setCollege(college);
        taggable.setCreated(new Date());
        taggable.setModified(new Date());
        taggable.setEntityWillowId(course.getId());
        taggable.setEntityIdentifier("Course");

        TaggableTag taggableTag = context.newObject(TaggableTag.class);
        taggableTag.setCollege(college);
        taggableTag.setCreated(new Date());
        taggableTag.setModified(new Date());
        taggableTag.setTag(tag);
        taggableTag.setTaggable(taggable);

        context.commitChanges();
        return taggableTag;
    }

    public static ModelBuilder valueOf(ObjectContext context, College college) {
        ModelBuilder result = new ModelBuilder();
        result.context = context;
        result.college = college;
        return result;
    }


}
