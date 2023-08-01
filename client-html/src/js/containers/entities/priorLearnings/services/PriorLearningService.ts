import { PriorLearning, PriorLearningApi } from "@api/model";
import { DefaultHttpService } from "../../../../common/services/HttpService";

class PriorLearningService {
  readonly priorLearningApi = new PriorLearningApi(new DefaultHttpService());

  public getPriorLearning(id: number): Promise<any> {
    return this.priorLearningApi.get(id);
  }

  public updatePriorLearning(id: number, priorLearning: PriorLearning): Promise<any> {
    return this.priorLearningApi.update(id, priorLearning);
  }

  public createPriorLearning(priorLearning: PriorLearning): Promise<any> {
    return this.priorLearningApi.create(priorLearning);
  }

  public removePriorLearning(id: number): Promise<any> {
    return this.priorLearningApi.remove(id);
  }
}

export default new PriorLearningService();
