export class ModalService {
  constructor(private open: (ModalOptions) => ModalClose) {
  }

  openModal(options: ModalOptions): ModalClose {
    return this.open(options);
  }
}

export type ModalClose = () => void;

export interface ModalOptions {
  readonly content?: string;
  readonly onClose?: () => void;
  readonly width?: number;
  readonly height?: number;
  readonly showCloseButton?: boolean;
  readonly animation?: 'zoom' | 'fade' | 'flip' | 'door' | 'rotate' | 'slideUp' | 'slideDown' | 'slideLeft' | 'slideRight';
  readonly duration?: number;
}
