import { MembershipProduct, MembershipProductApi } from "@api/model";
import { DefaultHttpService } from "../../../../common/services/HttpService";

class MembershipProductService {
  readonly membershipProductApi = new MembershipProductApi(new DefaultHttpService());

  public getMembershipProduct(id: number): Promise<any> {
    return this.membershipProductApi.get(id);
  }

  public updateMembershipProduct(id: number, membershipProduct: MembershipProduct): Promise<any> {
    return this.membershipProductApi.update(id, membershipProduct);
  }

  public createMembershipProduct(membershipProduct: MembershipProduct): Promise<any> {
    return this.membershipProductApi.create(membershipProduct);
  }
}

export default new MembershipProductService();
