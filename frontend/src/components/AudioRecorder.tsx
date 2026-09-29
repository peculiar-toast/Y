import { CircularProgress, IconButton, Tooltip, Typography } from "@mui/material";
import Box from "@mui/material/Box";
import { useCallback, useEffect, useRef, useState } from "react";

import MicIcon from '@mui/icons-material/Mic';
import PauseIcon from '@mui/icons-material/Pause';
import PlayArrowIcon from '@mui/icons-material/PlayArrow';
import StopIcon from '@mui/icons-material/Stop';
import DeleteIcon from '@mui/icons-material/Delete';
import FiberManualRecordIcon from '@mui/icons-material/FiberManualRecord';
import AudioPlayer from "./AudioPlayer";

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

const MAX_DURATION_MS = 5 * 60 * 1000;

const MIME_TYPES = [
  "audio/webm;codecs=opus",
  "audio/webm",
  "audio/mp4",
  "audio/ogg;codecs=opus",
];

function formatTime(ms: number): string {
  const totalSeconds = Math.floor(ms / 1000);
  const minutes = Math.floor(totalSeconds / 60);
  const seconds = totalSeconds % 60;

  return `${minutes}:${seconds.toString().padStart(2, "0")}`;
}

function getMimeType(): string | undefined {
  if (typeof MediaRecorder === 'undefined') {
    return undefined;
  }

  return MIME_TYPES.find((type) =>
    MediaRecorder.isTypeSupported(type),
  );
}


export function AudioRecorder({
  onDelete,
  onRecorded,
  maxDurationMs = MAX_DURATION_MS,
}: AudioRecorderProps) {
  const [status, setStatus] = useState<RecorderStatus>("idle");
  const [elapsedMs, setElapsedMs] = useState(0);
  const [audioURL, setAudioURL] = useState("");
  const [error, setError] = useState<string | null>(null);

  const recorderRef = useRef<MediaRecorder | null>(null);
  const streamRef = useRef<MediaStream | null>(null);
  const chunksRef = useRef<Blob[]>([]);

  const startedAtRef = useRef<number | null>(null);
  const pausedAtRef = useRef<number | null>(null);
  const totalPausedMsRef = useRef(0);

  const timerRef = useRef<number | null>(null);

  const stopTimer = useCallback(() => {
    if (timerRef.current !== null) {
      window.clearInterval(timerRef.current);
      timerRef.current = null;
    }
  }, []);

  const stopStream = useCallback(() => {
    streamRef.current?.getTracks().forEach((track) => {
      track.stop();
    });

    streamRef.current = null;
  }, []);

  const cleanup = useCallback(() => {
    stopTimer();
    stopStream();

    recorderRef.current = null;
    startedAtRef.current = null;
    pausedAtRef.current = null;
    totalPausedMsRef.current = 0;
  }, [stopStream, stopTimer]);

  const deleteRecording = useCallback(() => {
    cleanup();

    setElapsedMs(0);
    setStatus("idle");
    setError(null);

    if (audioURL !== "") {
      URL.revokeObjectURL(audioURL);
      setAudioURL("");
    }

    onDelete?.();
  }, [cleanup, onDelete]);

  const stopRecording = useCallback(() => {
    const recorder = recorderRef.current;

    if (!recorder || recorder.state === "inactive") {
      return;
    }

    setStatus("stopping");
    recorder.stop();
  }, []);

  const updateTimer = useCallback(() => {
    const startedAt = startedAtRef.current;

    if (startedAt === null) {
      return;
    }

    const now = performance.now();

    let pausedMs = totalPausedMsRef.current;

    if (pausedAtRef.current !== null) {
      pausedMs += now - pausedAtRef.current;
    }

    const elapsed = Math.max(0, now - startedAt, pausedMs);

    if (elapsed >= maxDurationMs) {
      setElapsedMs(maxDurationMs);
      stopRecording()
      return;
    }

    setElapsedMs(elapsed)
  }, [maxDurationMs, stopRecording]);

  const startRecording = useCallback( async () => {
    if (
      status === "recording" ||
      status === "paused" ||
      status === "requesting"
    ) {
      return
    }

    if (!window.isSecureContext) {
      setError('Microphone access requires a secure context (HTTPS).')
      setStatus("error")
      return
    }

    if (!navigator.mediaDevices?.getUserMedia) {
      setError('This browser does not support microphone access.')
      setStatus("error")
      return
    }

    if (typeof MediaRecorder === 'undefined') {
      setError('This browser does not support audio recording.')
      setStatus("error")
      return
    }

    setStatus("requesting")
    setError(null)

    try {
      const stream = await navigator.mediaDevices.getUserMedia({
        audio: {
          echoCancellation: true,
          noiseSuppression: true,
          autoGainControl: true,
        }
      });

      streamRef.current = stream

      const mimeType = getMimeType();

      const recorder = mimeType
        ? new MediaRecorder(stream, {mimeType})
        : new MediaRecorder(stream)
      
      recorderRef.current = recorder
      chunksRef.current = []

      recorder.ondataavailable = (event) => {
        if (event.data.size > 0) {
          chunksRef.current.push(event.data)
        }
      }

      recorder.onstop = () => {
        const blob = new Blob(chunksRef.current, {
          type: recorder.mimeType || mimeType || 'audio/webm'
        })

        const url = URL.createObjectURL(blob)

        setAudioURL((current) => {
          if (current) {
            URL.revokeObjectURL(current)
          }

          return url;
        })

        setStatus('idle')

        const duration = elapsedMs

        cleanup()

        onRecorded?.(blob, duration)
      }

      startedAtRef.current = performance.now()
      pausedAtRef.current = null
      totalPausedMsRef.current = 0

      setElapsedMs(0)

      recorder.start(250)
      setStatus('recording')

      stopTimer()

      timerRef.current = window.setInterval(
        updateTimer,
        100
      )
    } catch (cause) {
      stopStream()

      if (
        cause instanceof DOMException &&
        cause.name === 'NotAllowedError'
      ) {
        setError('Microphone permission denied.');
      } else if (
        cause instanceof DOMException &&
        cause.name === 'NotFoundError'
      ) {
        setError('No microphone found.');
      } else {
        setError('Could not start recording.');
      }

      setStatus('error');
    }
  }, [
    cleanup,
    elapsedMs,
    onRecorded,
    status,
    stopStream,
    stopTimer,
    updateTimer
  ]);

  const pauseRecording = () => {
    const recorder = recorderRef.current

    if (!recorder || recorder.state !== 'recording') return

    recorder.pause()
    pausedAtRef.current = performance.now()

    stopTimer()
    setStatus('paused')
  }

  const resumeRecording = useCallback(() => {
    const recorder = recorderRef.current

    if (!recorder || recorder.state !== 'paused') return

    const now = performance.now()

    if (pausedAtRef.current !== null) {
      totalPausedMsRef.current += now - pausedAtRef.current
    }

    pausedAtRef.current = null

    recorder.resume()
    setStatus('recording')
  }, [])

  useEffect(() => {
    return () => {
      cleanup()

      if (audioURL) {
        URL.revokeObjectURL(audioURL)
      }
    }
  }, [audioURL, cleanup])

  if (
    status === "recording" ||
    status === "paused" ||
    status === "stopping"
  ) {
    return (
      <Box sx={{display: 'flex', alignItems: 'center', gap: 1}}>
        {status === 'recording' && (
          <FiberManualRecordIcon
            color="error"
            fontSize="small"
          />
        )}

        <Typography
          variant="body2"
          sx={{minWidth: 42, fontVariantNumeric: 'tabular-nums'}}
        >
          {formatTime(elapsedMs)}
        </Typography>

        {status === 'recording' && (
          <Tooltip title="Pause">
            <IconButton
              onClick={pauseRecording}
              size="small"
              color="inherit"
            >
              <PauseIcon />
            </IconButton>
          </Tooltip>
        )}

        {status === 'paused' && (
          <Tooltip title="Resume">
            <IconButton
              onClick={resumeRecording}
              size="small"
              color="primary"
            >
              <PlayArrowIcon />
            </IconButton>
          </Tooltip>
        )}

        <Tooltip title="Stop">
          <IconButton
            onClick={stopRecording}
            disabled={status === 'stopping'}
            size="small"
            color="error"
          >
            {status === 'stopping' ? (
              <CircularProgress size={20} />
            ) : (
              <StopIcon />
            )}
          </IconButton>
        </Tooltip>
      </Box>
    )
  }

  if (audioURL) {
    return (
      <Box sx={{display: 'flex', alignItems: 'center', gap: 1}}>
        <AudioPlayer
          src={audioURL}
        />        

        <Tooltip title="Delete recording">
          <IconButton
            onClick={deleteRecording}
            size="small"
            color="error"
          >
            <DeleteIcon />
          </IconButton>
        </Tooltip>
      </Box>
    )
  }

  return (
    <Box>
      <Tooltip title="Record voice message">
        <span>
          <IconButton
            onClick={startRecording}
            disabled={status === 'requesting'}
            color="primary"
          >
            {status === 'requesting' ? (
              <CircularProgress size={24} />
            ) : (
              <MicIcon />
            )}
          </IconButton>
        </span>
      </Tooltip>

      {status === 'error' && (
        <Typography
          variant="caption"
          color="error"
          sx={{ ml: 1 }}
        >
          {error}
        </Typography>
      )}
    </Box>
  );
}
