package ish.oncourse.portal.services.payment

/**
 * User: akoiro
 * Date: 3/07/2016
 */
abstract class AProcess {
    def Request request
    def Context context

    protected Response response = new Response()

    abstract Response process()
}