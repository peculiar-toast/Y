import { useState } from "react";
import type { Post } from "../types/postData";
import { savePost } from "../api/post";
import { AudioRecorder } from "./AudioRecorder";
import { Box, Button, FormControl, FormLabel, Stack, TextField } from "@mui/material";
import { useNavigate } from "react-router";

type CreatePostFormProps = {
  onSubmit: (post: Post) => void;
};

export function CreatePostForm({ onSubmit }: CreatePostFormProps) {
    const navigate = useNavigate();

  const [title, setTitle] = useState<string>("");
  const [audio, setAudio] = useState<Blob | null>(null);
  const [submitting, setSubmitting] = useState(false)

  function handleAudioCreated(newAudio: Blob) {
    setAudio(newAudio);
  }

  function handleReset() {
    setTitle("")
    setAudio(null)
  }

  async function handleCreatePost(ev: React.SubmitEvent<HTMLFormElement>) {
    ev.preventDefault();

    if (submitting) return
    if (!audio) return

    try {
      setSubmitting(true)

      const post = await savePost({ title, audio });

      if (post) {
        onSubmit(post);
      }

      navigate("/");
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <Box
      component="form"
      onSubmit={handleCreatePost}
      onReset={handleReset}
    >
      <Stack spacing={2}>
        <TextField
          name="title"
          placeholder="Title..."
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
          fullWidth
        />

        <FormControl component="fieldset">
          <FormLabel component="legend">
            Record Audio
          </FormLabel>
          <AudioRecorder onRecorded={handleAudioCreated} />
        </FormControl>

        <Stack direction="row" spacing={1}>
          <Button type="submit" variant="contained" disabled={submitting}>
            {submitting ? "Creating..." : "Create post"}
          </Button>
          <Button type="reset" variant="outlined" disabled={submitting}>
            Reset
          </Button>
        </Stack>
      </Stack>
    </Box>
  );
}
