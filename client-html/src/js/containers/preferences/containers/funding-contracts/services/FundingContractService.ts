import { FundingContractApi, FundingSource } from "@api/model";
import { DefaultHttpService } from "../../../../../common/services/HttpService";

class FundingContractService {
  readonly fundingContractApi = new FundingContractApi(new DefaultHttpService());

  public getFundingContracts(): Promise<FundingSource[]> {
    return this.fundingContractApi.get();
  }

  public deleteFundingContract(id: number): Promise<any> {
    return this.fundingContractApi.remove(id);
  }

  public patchFundingContracts(fundingContracts: FundingSource[]): Promise<any> {
    return this.fundingContractApi.patch(fundingContracts);
  }

  public updateFundingContracts(fundingContracts: FundingSource[]): Promise<any> {
    return this.fundingContractApi.update(fundingContracts);
  }
}

export default new FundingContractService();
