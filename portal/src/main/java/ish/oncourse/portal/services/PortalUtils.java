package ish.oncourse.portal.services;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class PortalUtils {

    public static final int MILLISECONDS_IN_DAY = 1000 * 60 * 60 * 24;

    public static final SimpleDateFormat DATE_FORMATTER_dd_MMMM_yyyy = new SimpleDateFormat("dd MMMM yyyy",Locale.getDefault());

    public static final SimpleDateFormat DATE_FORMATTER_dd_MMM = new SimpleDateFormat("dd MMM",Locale.getDefault());

    public static final SimpleDateFormat DATE_FORMATTER_dd_MMM_E = new SimpleDateFormat("dd/MMM/E",Locale.getDefault());

    public static final SimpleDateFormat DATE_FORMATTER_MM_yyyy = new SimpleDateFormat("MM-yyyy",Locale.getDefault());

    public static final SimpleDateFormat DATE_FORMATTER_MMM = new SimpleDateFormat("MMM",Locale.getDefault());

    /**
     * Format for printing classes time.
     */
    public static final  SimpleDateFormat TIME_FORMATTER_h_mm_a = new SimpleDateFormat("h:mm a",Locale.getDefault());


    public static final String CONTENT_TYPE = "text/json";


}
