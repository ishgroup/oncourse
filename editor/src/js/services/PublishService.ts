import {Version} from "../model";
import {PublishApi} from "../http/PublishApi";
import {DefaultHttpService} from "../common/services/HttpService";

class PublishService {
    readonly publishApi = new PublishApi(new DefaultHttpService());

    public getVersions(): Promise<Version[]> {
        return this.publishApi.getVersions();
    }

    public setVersion(id): Promise<any> {
        return this.publishApi.setVersion(id);
    }

    public publish(): Promise<any> {
        return this.publishApi.publish();
    }
}

export default new PublishService();
