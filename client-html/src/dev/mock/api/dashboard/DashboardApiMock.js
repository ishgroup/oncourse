import { promiseResolve } from "../../MockAdapter";
import { Category } from "../../../../../build/generated-sources/swagger-js/api";
export function dashboardApiMock(mock) {
    /**
     * List items
     **/
    this.api.onGet("/v1/dashboard/statistic").reply(config => {
        const statistics = this.db.getStatistic();
        return promiseResolve(config, statistics);
    });
    this.api.onGet("/v1/user/preference/category").reply(config => {
        const categories = Object.keys(Category).map((c) => ({
            url: "/",
            category: c,
            favorite: false
        }));
        return promiseResolve(config, { categories: categories, upgradePlanLink: "https://www.ish.com.au/oncourse/signup?securityKey=123456" });
    });
    this.api.onPut("/v1/user/preference/category").reply(config => {
        return promiseResolve(config, {});
    });
    this.api.onGet('https://www.ish.com.au/feed').reply(config => {
        return promiseResolve(config, JSON.stringify('<feed xmlns="http://www.w3.org/2005/Atom" ><generator uri="https://jekyllrb.com/" version="3.8.6">Jekyll</generator><link href="https://www.ish.com.au/feed.xml" rel="self" type="application/atom+xml" /><link href="https://www.ish.com.au/" rel="alternate" type="text/html" /><updated>2019-09-06T12:54:59+10:00</updated><id>https://www.ish.com.au/</id><title type="html">ish group</title><subtitle>ish onCourse. The most comprehensive and easy to use, marketing and enrolment system available.</subtitle><entry><title type="html">Multiple product sales</title><link href="https://www.ish.com.au/blog/announcements/product_quantity_online/" rel="alternate" type="text/html" title="Multiple product sales" /><published>2019-07-26T00:00:00+10:00</published><updated>2019-07-26T00:00:00+10:00</updated><id>https://www.ish.com.au/blog/announcements/product_quantity_online</id><content type="html" xml:base="https://www.ish.com.au/blog/announcements/product_quantity_online/">Products have long been a part of the online sales platform, but until now you could only sell them one at a time. Th...</content><author> <name>ari</name> </author><summary type="html">Products have long been a part of the online sales platform, but until now you could only sell them one at a time. Th...</summary> </entry><entry><title type="html">Using credit notes online</title><link href="https://www.ish.com.au/blog/announcements/credit_notes_online/" rel="alternate" type="text/html" title="Using credit notes online" /><published>2019-07-24T00:00:00+10:00</published><updated>2019-07-24T00:00:00+10:00</updated><id>https://www.ish.com.au/blog/announcements/credit_notes_online</id><content type="html" xml:base="https://www.ish.com.au/blog/announcements/credit_notes_online/">Your onCourse online enrolment system will shortly be able to take into account any credit students have when enrolli...</content><author> <name>ari</name> </author><summary type="html">Your onCourse online enrolment system will shortly be able to take into account any credit students have when enrolli...</summary> </entry><entry><title type="html">Java licensing</title><link href="https://www.ish.com.au/blog/announcements/java_licence_change/" rel="alternate" type="text/html" title="Java licensing" /><published>2019-07-22T00:00:00+10:00</published><updated>2019-07-22T00:00:00+10:00</updated><id>https://www.ish.com.au/blog/announcements/java_licence_change</id><content type="html" xml:base="https://www.ish.com.au/blog/announcements/java_licence_change/">Oracle has changed the licensing on their version of Java, so you might see new terms and conditions when you upgrade...</content><author> <name>ari</name> </author>  <summary type="html">Oracle has changed the licensing on their version of Java, so you might see new terms and conditions when you upgrade...</summary> </entry></feed>'));
    });
}
//# sourceMappingURL=DashboardApiMock.js.map