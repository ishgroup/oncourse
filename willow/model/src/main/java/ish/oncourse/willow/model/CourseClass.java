package ish.oncourse.willow.model;

import java.util.Date;

public class CourseClass {
    private long id;
    private Course course;
    private String code;
    private CourseClassPrice price;
    private Date start;
    private Date end;
    private int availablePlaces; 
    private boolean finished; 
    private boolean cancelled; 
    private boolean allowByApplication; 
    private boolean paymentGatewayEnabled;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public CourseClassPrice getPrice() {
        return price;
    }

    public void setPrice(CourseClassPrice price) {
        this.price = price;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public int getAvailablePlaces() {
        return availablePlaces;
    }

    public void setAvailablePlaces(int availablePlaces) {
        this.availablePlaces = availablePlaces;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isAllowByApplication() {
        return allowByApplication;
    }

    public void setAllowByApplication(boolean allowByApplication) {
        this.allowByApplication = allowByApplication;
    }

    public boolean isPaymentGatewayEnabled() {
        return paymentGatewayEnabled;
    }

    public void setPaymentGatewayEnabled(boolean paymentGatewayEnabled) {
        this.paymentGatewayEnabled = paymentGatewayEnabled;
    }
}
