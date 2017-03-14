package ish.oncourse.willow.model;

public class CourseClass {
    private long id;
    private Course course;
    private String code;
    private CourseClassPrice price;

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
}
