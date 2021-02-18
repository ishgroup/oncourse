package ish.oncourse.solr.query;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class Duration {
    private final static Logger logger = LogManager.getLogger();

    private static final String  SELF_PACED_CONDITION = "selfpaced";
    private Integer days;
    private Condition condition;
    private Boolean selfPaced = false;

    private Duration() {}
    
    public static String validate(String duration)  {
        if (StringUtils.trimToNull(duration) == null) {
            return "Empty search criteria";
        }

        if (SELF_PACED_CONDITION.equalsIgnoreCase(duration)) {
            return null;
        }

        Condition condition = Condition.valueOf(duration.charAt(0));
        if (condition != null) {
            duration = duration.substring(1);
        }

        if (StringUtils.isNumeric(duration)) {
            int days = Integer.parseInt(duration);
            if (days < 1) {
                return "Wrong search criteria: " + duration;
            }
        } else {
           return "days count should be numeric: " + duration;
        }

        return null;
    }
    
    
    public static Duration valueOf(String search) {
        
        if (StringUtils.trimToNull(search) == null) {
            logger.error("Empty search criteria");
        }
        
        Duration duration = new Duration();
        
        if (SELF_PACED_CONDITION.equalsIgnoreCase(search)) {
            duration.selfPaced = true;
            return duration;
        } 

        duration.condition = Condition.valueOf(search.charAt(0));
        if (duration.condition == null) {
            duration.condition = Condition.QE;
        } else {
            search = search.substring(1);
        }
        
        if (StringUtils.isNumeric(search)) {
            duration.days = Integer.valueOf(search);
            if (duration.days < 1) {
                logger.error("Wrong search criteria: " + search);
                return null;
            }
        } else {
            logger.error("days count should be numeric: " + search);
            return null;
        }
        
        return duration;
        
    }

    public Integer getDays() {
        return days;
    }

    public Condition getCondition() {
        return condition;
    }

    public Boolean isSelfPaced() {
        return selfPaced;
    }

    
    public enum Condition {
        
        QE('='),
        LT('<'),
        GT('>');
        
        private char value;

        Condition(char value) {
            this.value = value;
        }
        static Condition valueOf(char value) { 
            return Arrays.stream(values()).filter(condition -> condition.value == value).findFirst().orElse(null);
        }
        
    }
}
