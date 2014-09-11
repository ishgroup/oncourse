package ish.oncourse.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.services.Request;

import java.net.MalformedURLException;
import java.net.URL;

public class URLUtils {

    public static final ISHUrlValidator HTTP_URL_VALIDATOR = new ISHUrlValidator(new String[]{"http", "https"});

    public static final String URL_PATTERN = "%s://%s%s%s";

    public static final String URL_PATTERN_WITHOUT_CONTEXT = "%s://%s%s";

    /**
     * Determines if given URL is absolute. Assumption is that every absolute url must have
     * semicolon and slash in it and no slashes should precede first semicolon occurrence.
     *
     * @param url
     * @return true if url is absolute
     */
    public static boolean isAbsolute(String url) {

        if (url == null) {
            throw new IllegalArgumentException("URL cannot be null.");
        }

        int semicolonPosition = url.indexOf(':');
        int slashPosition = url.indexOf('/');

        return semicolonPosition > 0 && slashPosition > 0 && semicolonPosition < slashPosition;
    }

    public static URL buildURL(Request request, String path, boolean includeContext) throws MalformedURLException {
        String schema = request.isSecure() ? "https" : "http";
        String hostName = request.getServerName();
        String context = request.getContextPath();
        if (StringUtils.trimToNull(context) == null || !includeContext)
        {
            return new URL(String.format(URL_PATTERN_WITHOUT_CONTEXT, schema, hostName, path));
        }
        else
        {
            return new URL(String.format(URL_PATTERN, schema, hostName, context, path));
        }
    }

}
