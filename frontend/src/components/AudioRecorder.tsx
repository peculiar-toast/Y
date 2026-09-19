import { useRef, useState } from "react";

export type NotSentAudio = {
  blob: Blob;
  url: string;
};

type AudioRecorderProps = {
  handleAudioCreated: (newAudio: NotSentAudio) => void;
};

export function AudioRecorder({ handleAudioCreated }: AudioRecorderProps) {
  const mediaRecorderRef = useRef<MediaRecorder>(null);
  const chunksRef = useRef<Blob[]>([]);

  const [recording, setRecording] = useState(false);
  const [audioURL, setAudioURL] = useState("");

  async function startRecording() {
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true });

    if (!stream) {
      console.error("Could not fetch audio interface");
      return;
    }

    const mediaRecorder = new MediaRecorder(stream);
    chunksRef.current = [];

    mediaRecorder.ondataavailable = (event) => {
      if (event.data.size > 0) {
        chunksRef.current.push(event.data);
      }
    };

    mediaRecorder.onstop = () => {
      const blob = new Blob(chunksRef.current, {
        type: mediaRecorder.mimeType,
      });

      const url = URL.createObjectURL(blob);

      setAudioURL(url);

      handleAudioCreated({ blob, url });

      stream.getTracks().forEach((track) => track.stop());
    };

    mediaRecorderRef.current = mediaRecorder;

    mediaRecorder.start();
    setRecording(true);
  }

  function stopRecording() {
    mediaRecorderRef.current?.stop();
    setRecording(false);
  }

  return (
    <>
      <div>
        <button type="button" onClick={startRecording} disabled={recording}>
          Start
        </button>

        <button type="button" onClick={stopRecording} disabled={!recording}>
          Stop
        </button>

        {audioURL && <audio controls src={audioURL} />}
      </div>
    </>
  );
}
