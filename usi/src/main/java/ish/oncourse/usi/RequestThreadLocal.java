package ish.oncourse.usi;

public class RequestThreadLocal {

    public static final ThreadLocal<String> ip = new ThreadLocal<>();
    public static final ThreadLocal<String> requestEnvelop = new ThreadLocal<>();
    public static final ThreadLocal<String> responseEnvelop = new ThreadLocal<>();

}
