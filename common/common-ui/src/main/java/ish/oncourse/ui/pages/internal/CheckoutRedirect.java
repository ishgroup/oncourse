package ish.oncourse.ui.pages.internal;

import org.apache.tapestry5.ContentType;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.Meta;
import org.apache.tapestry5.util.TextStreamResponse;

import java.io.InputStream;

@Meta("tapestry.response-content-type=text/html")
public class CheckoutRedirect {

    private static final String HTML_SRC = "<html>\n" +
            "<head>\n" +
            "    <script>\n" +
            "        const query = window.location.href;\n" +
            "        const sessionIdMatch = query.match(/sessionId=([^&#?]+)/);\n" +
            "        const statusMatch = query.match(/paymentStatus=([^&#?]+)/);\n" +
            "        if (sessionIdMatch && sessionIdMatch[1] && statusMatch && statusMatch[1]) {\n" +
            "            window.parent.postMessage(\n" +
            "                {\n" +
            "                    payment: {\n" +
            "                        sessionId: sessionIdMatch[1],\n" +
            "                        status: statusMatch[1]\n" +
            "                    }\n" +
            "                },\n" +
            "                \"*\"\n" +
            "            );\n" +
            "        }\n" +
            "    </script>\n" +
            "</head>\n" +
            "<body>\n" +
            "</body>\n" +
            "</html>";

    StreamResponse onActivate() {
        return new TextStreamResponse("text/html", HTML_SRC);
    }
}
