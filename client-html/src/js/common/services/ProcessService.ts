import { ProcessResult } from '@api/model';

class ProcessService {
  private processes: Record<string, WebSocket> = {};

  public startProcessTrack(processId: string): Promise<ProcessResult> {
    this.processes[processId] = new WebSocket(`/v1/job-status/${processId}`);
    
    return new Promise((resolve, reject) => {
      this.processes[processId].onmessage = (message: MessageEvent<ProcessResult>) => {
        resolve(message.data);
      };
      this.processes[processId].onerror = () => {
        reject('Failed');
      };
    });
  }

  public interruptProcess(processId: string): Promise<void> {
    return new Promise(resolve => {
      this.processes[processId].onclose = () => {
        resolve();
      };
      this.processes[processId].close();
    });
  }
}

export default new ProcessService();
