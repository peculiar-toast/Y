import { Box, IconButton, Slider, Typography } from "@mui/material";
import { useEffect, useRef, useState } from "react";

import PauseIcon from '@mui/icons-material/Pause';
import PlayArrowIcon from '@mui/icons-material/PlayArrow';


interface AudioPlayerProps {
    src: string;
}

export default function AudioPlayer({ src }: AudioPlayerProps) {
    const audioRef = useRef<HTMLAudioElement | null>(null)

    const [playing, setPlaying] = useState(false)
    const [currentTime, setCurrentTime] = useState(0)
    const [duration, setDuration] = useState(0)

    useEffect(() => {
        const audio = audioRef.current

        if (!audio) return

        const handleLoadedMetadata = () => {
            setDuration(audio.duration)
        }

        const handleTimeUpdate = () => {
            setCurrentTime(audio.currentTime)
        }

        const handleEnded = () => {
            setPlaying(false)
            setCurrentTime(0)
            audio.currentTime = 0
        }

        audio.addEventListener('loadedmetadata', handleLoadedMetadata);
        audio.addEventListener('timeupdate', handleTimeUpdate);
        audio.addEventListener('ended', handleEnded);

        return () => {
            audio.removeEventListener('loadedmetadata', handleLoadedMetadata);
            audio.removeEventListener('timeupdate', handleTimeUpdate);
            audio.removeEventListener('ended', handleEnded);
        };
    }, [src])

    const togglePlay = async () => {
        const audio = audioRef.current

        if (!audio) return

        if (audio.paused) {
            await audio.play()
            setPlaying(true)
        } else {
            audio.pause()
            setPlaying(false)
        }
    }

    const handleSeek = (_: Event, value: number | number[]) => {
        const audio = audioRef.current;

        if (!audio) return;

        const time = Array.isArray(value) ? value[0] : value;

        audio.currentTime = time;
        setCurrentTime(time);
    };

    const formatTime = (seconds: number) => {
        if (!Number.isFinite(seconds)) return '0:00';

        const minutes = Math.floor(seconds / 60);
        const remainingSeconds = Math.floor(seconds % 60);

        return `${minutes}:${remainingSeconds
            .toString()
            .padStart(2, '0')}`;
    };

    return (
    <Box
      sx={{ width: '100%', maxWidth: 400, display: "flex", alignItems: "center", gap: 1 }}
    >
      <audio ref={audioRef} src={src} preload="metadata" />

      <IconButton
        onClick={togglePlay}
        color="primary"
        size="small"
        aria-label={playing ? 'Pause' : 'Play'}
      >
        {playing ? <PauseIcon /> : <PlayArrowIcon />}
      </IconButton>

      <Slider
        size="small"
        min={0}
        max={duration || 1}
        value={Math.min(currentTime, duration || 1)}
        onChange={handleSeek}
        aria-label="Audio progress"
      />

      <Typography
        variant="caption"
        sx={{
          minWidth: 40,
          fontVariantNumeric: 'tabular-nums',
        }}
      >
        {formatTime(playing ? currentTime : duration)}
      </Typography>
    </Box>
  );
}