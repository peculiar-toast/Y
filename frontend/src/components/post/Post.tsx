import type { JSX } from "react";
import type { Post } from "../../types/postData";

import { Avatar, Box, Button, Card, CardActions, CardContent, Typography } from "@mui/material";
import AudioPlayer from "../AudioPlayer";

type PostElementProps = {
  post: Post;
  handleLikePost: () => void;
  handleDeletePost: () => void;
};

export function PostElement({
  post,
  handleLikePost,
  handleDeletePost,
}: PostElementProps): JSX.Element {
  return (
    <Card variant="elevation">
      <CardContent>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
          <Avatar
            src={post.user.avatar}
            alt={post.user.username}
            sx={{width: 40, height: 40}}
          >
            {post.user.username.charAt(0).toUpperCase()}
          </Avatar>

          <Typography variant="subtitle2" sx={{fontWeight: 600}}>
            {post.user.username}
          </Typography>
        </Box>
      
        <Typography variant="h6">{post.title || "No title"}</Typography>

        <Box sx={{mt: 2}}>
          <AudioPlayer src={post.audio} />
        </Box>
      </CardContent>

      <CardActions>
        <Button variant="contained" onClick={handleLikePost}>like</Button>
        <Button variant="outlined" onClick={handleDeletePost}>
          delete
        </Button>
      </CardActions>
    </Card>
  );
}
