package ish.oncourse.solr.query;

import ish.oncourse.model.Tag;
import ish.oncourse.services.courseclass.ClassAge;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class SearchParams {
    private String s;
    private Double price;
    private DayOption day;
    private String time;
    private Tag subject;
    private Date after;
    private Date before;
    private List<Suburb> suburbs = new ArrayList<>();
    private Long siteId;
    private Long tutorId;
    private Boolean debugQuery = Boolean.FALSE;
    private TimeZone clientTimezone;
    private List<Tag> tags = new ArrayList<>();
	private ClassAge classAge;

    private List<Duration> durations = new ArrayList<>();

    public List<Tag> getTags() {
        return tags;
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public TimeZone getClientTimezone() {
        return clientTimezone;
    }

    public void setClientTimezone(TimeZone clientTimezone) {
        this.clientTimezone = clientTimezone;
    }

    public Boolean getDebugQuery() {
        return Boolean.TRUE.equals(debugQuery);
    }

    public void setDebugQuery(Boolean debugQuery) {
        this.debugQuery = debugQuery;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
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

    public Tag getSubject() {
        return subject;
    }

    public void setSubject(Tag subject) {
        this.subject = subject;
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

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public void addSuburb(Suburb suburb) {
        this.suburbs.add(suburb);
    }

    public List<Duration> getDurations() {
        return durations;
    }

    public void addDuration(Duration duration) {
        this.durations.add(duration);
    }

    public boolean isEmpty() {
        return tags.isEmpty() && suburbs.isEmpty() && durations.isEmpty() &&
                StringUtils.trimToNull(s) == null &&
                price == null &&
                day == null &&
                StringUtils.trimToNull(time) == null &&
                subject == null &&
                after == null &&
                before == null;
    }

    public Long getTutorId() {
        return tutorId;
    }

    public void setTutorId(Long tutorId) {
        this.tutorId = tutorId;
    }

    public static SearchParams valueOf(SearchParams params, boolean withoutSuburbs) {
        SearchParams result;
        try {
            result = (SearchParams) BeanUtils.cloneBean(params);
            if (withoutSuburbs) {
                result.suburbs.clear();
            }
            return result;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

	public ClassAge getClassAge() {
		return classAge;
	}

	public SearchParams setClassAge(ClassAge classAge) {
		this.classAge = classAge;
		return this;
	}

    public boolean hasSuburbs() {
        return suburbs != null && !suburbs.isEmpty();
    }
}
