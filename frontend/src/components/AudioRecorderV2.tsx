import type { CSSProperties } from "@mui/material";
import { useCallback, useEffect, useRef, useState } from "react";

type RecorderStatus =
  | "idle"
  | "requesting"
  | "recording"
  | "paused"
  | "stopping"
  | "error";

export interface AudioRecorderProps {
  /**
   * Called when an audio has been recorded and the Blob is ready for upload
   */
  onRecorded: (newAudio: Blob, durationMs: number) => void;

  /**
   * Called when user deletes current recording
   */
  onDelete?: () => void;

  /**
   * Max recording duration in milliseconds
   * Default: 5 minutes
   */
  maxDurationMs?: number;

  /**
   * Preferred audio MIME types.
   * Component automatically falls back when browser doesn't support one.
   */
  preferredMimeTypes?: string[];

  /**
   * Optional class name for outer container.
   */
  className?: string;
}

const MAX_RECORDING_DURATION_MS = 5 * 60 * 1000;

const DEFAULT_MIME_TYPES = [
  "audio/webm;codecs=opus",
  "audio/webm",
  "audio/mp4",
  "audio/ogg;codecs=opus",
];

const styles: Record<string, CSSProperties> = {
  root: {
    width: "100%",
    display: "flex",
    flexDirection: "column",
    gap: 12,
  },

  waveform: {
    width: "100%",
    height: 64,
    display: "block",
    borderRadius: 8,
    background: "#f5f5f5",
  },

  controls: {
    display: "flex",
    alignItems: "center",
    gap: 8,
  },

  button: {
    border: 0,
    borderRadius: 8,
    padding: "8px 14px",
    cursor: "pointer",
    fontSize: 14,
  },

  primaryButton: {
    background: "#1976d2",
    color: "#fff",
  },

  dangerButton: {
    background: "#d32f2f",
    color: "#fff",
  },

  secondaryButton: {
    background: "#e0e0e0",
    color: "#222",
  },

  timer: {
    fontVariantNumeric: "tabular-nums",
    fontFamily: "monospace",
    fontSize: 14,
    minWidth: 100,
  },

  error: {
    color: "#d32f2f",
    fontSize: 14,
  },

  preview: {
    width: "100%",
  },
};

function formatTime(ms: number): string {
  const totalSeconds = Math.floor(ms / 1000);
  const minutes = Math.floor(totalSeconds / 60);
  const seconds = totalSeconds % 60;

  return `${minutes}:${seconds.toString().padStart(2, "0")}`;
}

function getSupportedMimeType(preferredTypes: string[]): string | undefined {
  if (
    typeof MediaRecorder === "undefined" ||
    typeof MediaRecorder.isTypeSupported !== "function"
  ) {
    return undefined;
  }

  return preferredTypes.find((type) => MediaRecorder.isTypeSupported(type));
}

function Waveform({
  analyser,
  active,
}: {
  analyser: AnalyserNode | null;
  active: boolean;
}) {
  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const animationRef = useRef<number | null>(null);

  useEffect(() => {
    const canvas = canvasRef.current;

    if (!canvas) {
      return;
    }

    const ctx = canvas.getContext("2d");

    if (!ctx) {
      return;
    }

    const resizeCanvas = () => {
      const rect = canvas.getBoundingClientRect();
      const dpr = window.devicePixelRatio || 1;

      canvas.width = Math.max(1, Math.floor(rect.width * dpr));
      canvas.height = Math.max(1, Math.floor(rect.height * dpr));

      ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
    };

    resizeCanvas();

    const resizeObserver = new ResizeObserver(resizeCanvas);
    resizeObserver.observe(canvas);

    const drawIdle = () => {
      const width = canvas.clientWidth;
      const height = canvas.clientHeight;

      ctx.clearRect(0, 0, width, height);

      ctx.fillStyle = "#bdbdbd";

      const centerY = height / 2;
      const barWidth = 3;
      const gap = 3;
      const count = Math.floor(width / (barWidth + gap));

      for (let i = 0; i < count; i += 1) {
        const x = i * (barWidth + gap);
        const barHeight = 4;

        ctx.fillRect(x, centerY - barHeight / 2, barWidth, barHeight);
      }
    };

    const draw = () => {
      const width = canvas.clientWidth;
      const height = canvas.clientHeight;

      ctx.clearRect(0, 0, width, height);

      if (!analyser || !active) {
        drawIdle();
        return;
      }

      const data = new Uint8Array(analyser.fftSize);
      analyser.getByteTimeDomainData(data);

      const barWidth = 3;
      const gap = 3;
      const count = Math.floor(width / (barWidth + gap));
      const centerY = height / 2;

      ctx.fillStyle = "#1976d2";

      for (let i = 0; i < count; i += 1) {
        const dataIndex = Math.floor((i / count) * data.length);
        const sample = data[dataIndex] / 128 - 1;

        const amplitude = Math.min(1, Math.max(0.05, Math.abs(sample) * 2.5));

        const barHeight = Math.max(4, amplitude * height * 0.85);

        const x = i * (barWidth + gap);

        ctx.fillRect(x, centerY - barHeight / 2, barWidth, barHeight);
      }

      animationRef.current = requestAnimationFrame(draw);
    };

    draw();

    return () => {
      resizeObserver.disconnect();

      if (animationRef.current !== null) {
        cancelAnimationFrame(animationRef.current);
      }
    };
  }, [analyser, active]);

  return <canvas ref={canvasRef} style={styles.waveform} aria-hidden="true" />;
}

export function AudioRecorder({
  onRecorded,
  onDelete,
  maxDurationMs = MAX_RECORDING_DURATION_MS,
  preferredMimeTypes = DEFAULT_MIME_TYPES,
  className,
}: AudioRecorderProps) {
  const [status, setStatus] = useState<RecorderStatus>("idle");
  const [elapsedMs, setElapsedMs] = useState(0);
  const [audioUrl, setAudioUrl] = useState<string | null>(null);
  const [recordedBlob, setRecordedBlob] = useState<Blob | null>(null);
  const [error, setError] = useState<string | null>(null);

  const recorderRef = useRef<MediaRecorder | null>(null);
  const streamRef = useRef<MediaStream | null>(null);

  const audioContextRef = useRef<AudioContext | null>(null);
  const sourceRef = useRef<MediaStreamAudioSourceNode | null>(null);
  const analyserRef = useRef<AnalyserNode | null>(null);

  const chunksRef = useRef<Blob[]>([]);

  const startedAtRef = useRef<number | null>(null);
  const pausedAtRef = useRef<number | null>(null);
  const accumulatedPauseMsRef = useRef(0);

  const timerRef = useRef<number | null>(null);

  const cleanupAudioGraph = useCallback(() => {
    try {
      sourceRef.current?.disconnect();
    } catch {
      // Already disconnected.
    }

    try {
      analyserRef.current?.disconnect();
    } catch {
      // Already disconnected.
    }

    sourceRef.current = null;
    analyserRef.current = null;

    if (audioContextRef.current) {
      void audioContextRef.current.close().catch(() => undefined);
      audioContextRef.current = null;
    }
  }, []);

  const stopStream = useCallback(() => {
    streamRef.current?.getTracks().forEach((track) => {
      track.stop();
    });

    streamRef.current = null;
  }, []);

  const stopTimer = useCallback(() => {
    if (timerRef.current !== null) {
      window.clearInterval(timerRef.current);
      timerRef.current = null;
    }
  }, []);

  const cleanupRecorder = useCallback(() => {
    stopTimer();
    cleanupAudioGraph();
    stopStream();

    recorderRef.current = null;
    startedAtRef.current = null;
    pausedAtRef.current = null;
    accumulatedPauseMsRef.current = 0;
  }, [cleanupAudioGraph, stopStream, stopTimer]);

  const deleteRecording = useCallback(() => {
    cleanupRecorder();

    setRecordedBlob(null);
    setElapsedMs(0);
    setStatus("idle");
    setError(null);

    setAudioUrl((currentUrl) => {
      if (currentUrl) {
        URL.revokeObjectURL(currentUrl);
      }

      return null;
    });

    onDelete?.();
  }, [cleanupRecorder, onDelete]);

  const finalizeRecording = useCallback(() => {
    const recorder = recorderRef.current;

    if (!recorder) {
      return;
    }

    setStatus("stopping");

    if (recorder.state !== "inactive") {
      recorder.stop();
    }
  }, []);

  const startTimer = useCallback(() => {
    stopTimer();

    timerRef.current = window.setInterval(() => {
      const startedAt = startedAtRef.current;

      if (startedAt === null) {
        return;
      }

      const now = performance.now();

      let pausedMs = accumulatedPauseMsRef.current;

      if (pausedAtRef.current !== null) {
        pausedMs += now - pausedAtRef.current;
      }

      const elapsed = Math.max(0, now - startedAt - pausedMs);

      if (elapsed >= maxDurationMs) {
        setElapsedMs(maxDurationMs);
        finalizeRecording();
        return;
      }

      setElapsedMs(elapsed);
    }, 100);
  }, [finalizeRecording, maxDurationMs, stopTimer]);

  const startRecording = useCallback(async () => {
    if (status === "recording" || status === "paused") {
      return;
    }

    if (!navigator.mediaDevices?.getUserMedia) {
      setError("Microphone access is not supported by this browser.");
      setStatus("error");
      return;
    }

    if (typeof MediaRecorder === "undefined") {
      setError("Audio recording is not supported by this browser.");
      setStatus("error");
      return;
    }

    setStatus("requesting");
    setError(null);

    try {
      const stream = await navigator.mediaDevices.getUserMedia({
        audio: {
          echoCancellation: true,
          noiseSuppression: true,
          autoGainControl: true,
        },
      });

      streamRef.current = stream;

      const mimeType = getSupportedMimeType(preferredMimeTypes);

      const recorder = mimeType
        ? new MediaRecorder(stream, { mimeType })
        : new MediaRecorder(stream);

      recorderRef.current = recorder;
      chunksRef.current = [];

      recorder.ondataavailable = (event) => {
        if (event.data.size > 0) {
          chunksRef.current.push(event.data);
        }
      };

      recorder.onerror = () => {
        setError("Audio recording failed.");
        setStatus("error");

        cleanupRecorder();
      };

      recorder.onstop = () => {
        const actualMimeType = recorder.mimeType || mimeType || "audio/webm";

        const blob = new Blob(chunksRef.current, {
          type: actualMimeType,
        });

        const durationMs = elapsedMs;

        const nextUrl = URL.createObjectURL(blob);

        setAudioUrl((currentUrl) => {
          if (currentUrl) {
            URL.revokeObjectURL(currentUrl);
          }

          return nextUrl;
        });

        setRecordedBlob(blob);
        setElapsedMs(durationMs);
        setStatus("idle");

        cleanupRecorder();

        onRecorded?.(blob, durationMs);
      };

      // Build live waveform audio graph.
      const AudioContextClass =
        window.AudioContext ||
        (
          window as typeof window & {
            webkitAudioContext?: typeof AudioContext;
          }
        ).webkitAudioContext;

      if (AudioContextClass) {
        const audioContext = new AudioContextClass();

        const source = audioContext.createMediaStreamSource(stream);

        const analyser = audioContext.createAnalyser();

        analyser.fftSize = 512;
        analyser.smoothingTimeConstant = 0.75;

        source.connect(analyser);

        audioContextRef.current = audioContext;
        sourceRef.current = source;
        analyserRef.current = analyser;

        await audioContext.resume().catch(() => undefined);
      }

      startedAtRef.current = performance.now();
      pausedAtRef.current = null;
      accumulatedPauseMsRef.current = 0;

      recorder.start(250);

      setElapsedMs(0);
      setStatus("recording");

      startTimer();
    } catch (cause) {
      stopStream();

      if (cause instanceof DOMException && cause.name === "NotAllowedError") {
        setError(
          "Microphone permission denied. Allow microphone access and try again.",
        );
      } else if (
        cause instanceof DOMException &&
        cause.name === "NotFoundError"
      ) {
        setError("No microphone was found.");
      } else {
        setError("Could not start audio recording.");
      }

      setStatus("error");
    }
  }, [
    cleanupRecorder,
    elapsedMs,
    onRecorded,
    preferredMimeTypes,
    startTimer,
    status,
    stopStream,
  ]);

  const pauseRecording = useCallback(() => {
    const recorder = recorderRef.current;

    if (!recorder || recorder.state !== "recording") {
      return;
    }

    recorder.pause();

    pausedAtRef.current = performance.now();

    setStatus("paused");
  }, []);

  const resumeRecording = useCallback(() => {
    const recorder = recorderRef.current;

    if (!recorder || recorder.state !== "paused") {
      return;
    }

    const now = performance.now();

    if (pausedAtRef.current !== null) {
      accumulatedPauseMsRef.current += now - pausedAtRef.current;
    }

    pausedAtRef.current = null;

    recorder.resume();

    setStatus("recording");
  }, []);

  // Cleanup when component unmounts.
  useEffect(() => {
    return () => {
      stopTimer();
      cleanupAudioGraph();
      stopStream();

      if (audioUrl) {
        URL.revokeObjectURL(audioUrl);
      }
    };
  }, [audioUrl, cleanupAudioGraph, stopStream, stopTimer]);

  const hasRecording = recordedBlob !== null;
  const isRecording = status === "recording";
  const isPaused = status === "paused";
  const isBusy = status === "requesting" || status === "stopping";

  return (
    <div className={className} style={styles.root}>
      <Waveform analyser={analyserRef.current} active={isRecording} />

      <div style={styles.controls}>
        <span
          style={styles.timer}
          aria-live="polite"
          aria-label={`Recording duration ${formatTime(elapsedMs)}`}
        >
          {formatTime(elapsedMs)}
        </span>

        {!hasRecording && !isRecording && !isPaused && (
          <button
            type="button"
            onClick={startRecording}
            disabled={isBusy}
            style={{
              ...styles.button,
              ...styles.primaryButton,
            }}
          >
            {status === "requesting" ? "Requesting microphone…" : "Record"}
          </button>
        )}

        {(isRecording || isPaused) && (
          <>
            {isRecording ? (
              <button
                type="button"
                onClick={pauseRecording}
                style={{
                  ...styles.button,
                  ...styles.secondaryButton,
                }}
              >
                Pause
              </button>
            ) : (
              <button
                type="button"
                onClick={resumeRecording}
                style={{
                  ...styles.button,
                  ...styles.primaryButton,
                }}
              >
                Resume
              </button>
            )}

            <button
              type="button"
              onClick={finalizeRecording}
              style={{
                ...styles.button,
                ...styles.dangerButton,
              }}
            >
              Stop
            </button>
          </>
        )}

        {hasRecording && !isRecording && !isPaused && (
          <>
            <button
              type="button"
              onClick={startRecording}
              style={{
                ...styles.button,
                ...styles.primaryButton,
              }}
            >
              Record again
            </button>

            <button
              type="button"
              onClick={deleteRecording}
              style={{
                ...styles.button,
                ...styles.secondaryButton,
              }}
            >
              Delete
            </button>
          </>
        )}
      </div>

      {audioUrl && (
        <audio
          src={audioUrl}
          controls
          preload="metadata"
          style={styles.preview}
        />
      )}

      {error && (
        <div role="alert" style={styles.error}>
          {error}
        </div>
      )}
    </div>
  );
}
