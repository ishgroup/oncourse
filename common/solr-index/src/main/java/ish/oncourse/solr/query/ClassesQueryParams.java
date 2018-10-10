/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.solr.query;

import ish.oncourse.services.courseclass.ClassAge;

import java.util.*;
import java.util.stream.Collectors;

public class ClassesQueryParams {
    
    private Double price;
    private DayOption day;
    private String time;
    private Date after;
    private Date before;
    private List<Suburb> suburbs = new ArrayList<>();
    private Long siteId;
    private Long tutorId;
    private ClassAge classAge;
    private String siteKey;
    private Set<String> courses;
    
    public static ClassesQueryParams valueOf(SearchParams searchParams, String siteKey,  Set<String> courses) {
        ClassesQueryParams params = new ClassesQueryParams();
        params.price = searchParams.getPrice();
        params.time = searchParams.getTime();
        params.after = searchParams.getAfter();
        params.before = searchParams.getBefore();
        params.suburbs = searchParams.getSuburbs();
        params.siteId = searchParams.getSiteId();
        params.tutorId = searchParams.getTutorId();
        params.classAge = searchParams.getClassAge();
        params.siteKey = siteKey;
        params.courses = courses;
        return params;
    }
    
    
    
    public String getSiteKey() {
        return siteKey;
    }

    public void setSiteKey(String siteKey) {
        this.siteKey = siteKey;
    }

    public Set<String> getCourses() {
        return courses;
    }

    public String getCoursesString() {
        return courses.stream().collect(Collectors.joining(" "));
    }

    public void setCourses(Set<String> courses) {
        this.courses = courses;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public DayOption getDay() {
        return day;
    }

    public void setDay(DayOption day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Date getAfter() {
        return after;
    }

    public void setAfter(Date after) {
        this.after = after;
    }

    public Date getBefore() {
        return before;
    }

    public void setBefore(Date before) {
        this.before = before;
    }

    public List<Suburb> getSuburbs() {
        return suburbs;
    }

    public void setSuburbs(List<Suburb> suburbs) {
        this.suburbs = suburbs;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getTutorId() {
        return tutorId;
    }

    public void setTutorId(Long tutorId) {
        this.tutorId = tutorId;
    }
    
    public ClassAge getClassAge() {
        return classAge;
    }

    public void setClassAge(ClassAge classAge) {
        this.classAge = classAge;
    }
}
