import {
 Diff, Room, RoomApi, RoomValidationApi 
} from "@api/model";
import { DefaultHttpService } from "../../../../common/services/HttpService";

class RoomService {
  readonly roomApi = new RoomApi(new DefaultHttpService());

  readonly roomValidationApi = new RoomValidationApi(new DefaultHttpService());

  public bulkChange(diff: Diff): Promise<any> {
    return this.roomApi.bulkChange(diff);
  }

  public createRoom(room: Room): Promise<any> {
    return this.roomApi.create(room);
  }

  public getRoom(id: number): Promise<any> {
    return this.roomApi.get(id);
  }

  public updateRoom(id: number, room: Room): Promise<any> {
    return this.roomApi.update(id, room);
  }

  public removeRoom(id: number): Promise<any> {
    return this.roomApi.remove(id);
  }

  public validateRemoveRoom(id: number): Promise<any> {
    return this.roomValidationApi.remove(id);
  }
}

export default new RoomService();
