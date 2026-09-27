import { useState } from "react";
import type { Post } from "../types/postData";
import { saveAudio, savePost } from "../api/post";
import { AudioRecorder } from "./AudioRecorder";
import { Box, Button, FormGroup, Stack, TextField, Typography } from "@mui/material";

type CreatePostFormProps = {
  onSubmit: (post: Post) => void;
};

export function CreatePostForm({ onSubmit: addPost }: CreatePostFormProps) {
  const [title, setTitle] = useState<string>("");
  const [content, setContent] = useState<string>("");
  const [audio, setAudio] = useState<Blob | null>(null);
  const [submitting, setSubmitting] = useState(false)

  function handleAudioCreated(newAudio: Blob) {
    setAudio(newAudio);
  }

  function handleReset () {
    setTitle("")
    setContent("")
    setAudio(null)
  }

  async function handleCreatePost(ev: React.SubmitEvent<HTMLFormElement>) {
    ev.preventDefault();

    if (submitting) return

    try {
      setSubmitting(true)

      if (audio) {
        await saveAudio(1, audio);
      }

      const post = await savePost({ title, content });
      
      if (post) {
        addPost(post);
      }
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

          <TextField
            name="content"
            label="Content"
            placeholder="What's on your mind?"
            value={content}
            onChange={(e) => setContent(e.target.value)}
            multiline
            minRows={3}
            fullWidth
          />

        <AudioRecorder onRecorded={handleAudioCreated} />

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
