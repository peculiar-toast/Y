import type { JSX } from "react";
import type { Post } from "../../types/postData";

import { Box, Button, Card, CardContent, Typography } from "@mui/material";
import AudioPlayer from "../AudioPlayer";

type PostElementProps = {
  post: Post;
  handleDeletePost: () => void;
};

export function PostElement({
  post,
  handleDeletePost,
}: PostElementProps): JSX.Element {
  let audioEl = <p>Could not load audio</p>;

  audioEl = <AudioPlayer src={post.audio} />;

  return (
    <Card variant="outlined">
      <CardContent>
        <Typography>{post.title ?? "No title"}</Typography>

        <Typography>{post.content}</Typography>

        <Box>{audioEl}</Box>
      </CardContent>

      <CardContent>
        <Button variant="contained">like</Button>
        <Button variant="outlined" onClick={handleDeletePost}>
          delete
        </Button>
      </CardContent>
    </Card>
  );
}
