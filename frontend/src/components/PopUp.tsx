import { useEffect, useRef, type ReactElement, type ReactNode } from "react";

type PopUpProps = {
  id: string;
  title: string;
  children: ReactNode;
  onClose?: () => void;
  onCancel?: () => void;
  onConfirm?: () => void;
};

export function PopUp({
  id,
  title,
  children,
  onCancel,
  onConfirm,
  onClose,
}: PopUpProps): ReactElement {
  const titleId = `${id}-label`;
  const modalRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const modalEl = modalRef.current;

    if (!modalEl) return;

    const handleHidden = () => onClose?.();

    modalEl.addEventListener("hidden.bs.modal", handleHidden);

    return () => {
      modalEl.removeEventListener("hidden.bs.modal", handleHidden);
    };
  }, [onClose]);

  return (
    <div
      className={"modal fade"}
      id={id}
      data-bs-backdrop="static"
      data-bs-keyboard="false"
      tabIndex={-1}
      aria-labelledby={id}
      aria-hidden="true"
      ref={modalRef}
    >
      <div className={"modal-dialog"}>
        <div className={"modal-content"}>
          <div className={"modal-header"}>
            <h5 id={titleId} className={"modal-title"}>
              {title}
            </h5>

            <button
              type="button"
              className="btn-close"
              data-bs-dismiss="modal"
              aria-label="Close"
            />
          </div>

          <div className="modal-body">{children}</div>

          <div className="modal-footer">
            <button
              type="button"
              className="btn btn-secondary"
              data-bs-dismiss="modal"
              onClick={onCancel}
            >
              Close
            </button>

            <button
              type="button"
              className="btn btn-primary"
              data-bs-dismiss="modal"
              onClick={onConfirm}
            >
              Save changes
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
