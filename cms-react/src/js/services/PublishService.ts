import {History} from "../model";
import {PublishApi} from "../http/PublishApi";

class PublishService {
    readonly publishApi = new PublishApi();

    public getVersions(): Promise<History> {
        return this.publishApi.getVersions();
    }

    public setVersion(id): Promise<History> {
        return this.publishApi.setVersion(id);
    }

    public publish(): Promise<History> {
        return this.publishApi.publish();
    }
}

export default new PublishService();
