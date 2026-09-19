import { useState } from "react";
import type { Post } from "../types/postData";
import { saveAudio, savePost } from "../api/post";
import { AudioRecorder, type NotSentAudio } from "./AudioRecorder";

type CreatePostFormProps = {
  addPost: (post: Post) => void;
};

export function CreatePostForm({ addPost }: CreatePostFormProps) {
  const [title, setTitle] = useState<string>("");
  const [content, setContent] = useState<string>("");
  const [audio, setAudio] = useState<NotSentAudio | null>(null);

  function handleAudioCreated(newAudio: NotSentAudio) {
    setAudio(newAudio);
  }

  // TODO reset form fields
  async function handleCreatePost(ev: React.SubmitEvent<HTMLFormElement>) {
    ev.preventDefault();

    if (audio) {
      await saveAudio(1, audio.blob);
    }

    const post = await savePost({ title, content });

    if (post !== undefined) {
      addPost(post);
    }
  }

  return (
    <div className="container">
      <form onSubmit={async (ev) => handleCreatePost(ev)}>
        <label className="row">
          <span className="col">Title:</span>
          <input
            className="col"
            name="title"
            placeholder="Title..."
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
          />
        </label>

        <label className="row">
          <span className="col">Content:</span>
          <input
            className="col"
            placeholder="Content..."
            value={content}
            onChange={(e) => setContent(e.target.value)}
          />
        </label>

        <AudioRecorder handleAudioCreated={handleAudioCreated} />

        <input type="submit" value={"Create post!"} />
        <input type="reset" value={"Reset"} />
      </form>
    </div>
  );
}
