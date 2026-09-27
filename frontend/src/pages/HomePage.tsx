import { useCallback, useEffect, useState, type JSX } from "react";
import { PostElement } from "../components/post/Post";
import type { Post } from "../types/postData";
import { deletePost, getAllPosts } from "../api/post";
import { Alert, Box, Button, IconButton, Stack, Tooltip, Typography } from "@mui/material";
import PageContainer from "../components/PageContainer";

import AddIcon from '@mui/icons-material/Add';
import RefreshIcon from '@mui/icons-material/Refresh';


export function HomePage(): JSX.Element {
  const [posts, setPosts] = useState<Post[]>([]);

  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<Error | null>(null)

  useEffect(() => {
    getAllPosts()
      .then((res) => {
        setPosts(res ?? []);
      })
      .catch((err) => {
        console.error("Error fetching posts: ", err);
      });
  }, []);

  const handleRefresh = useCallback(() => {
    throw "TODO implement"
  }, [])

  const handleCreateClick = useCallback(() => {

  }, [])

  const pageTitle = "Home Page"

  return (
    <PageContainer
      title={pageTitle}
      breadcrumbs={[{ title: pageTitle }]}
      actions={
        <Stack direction={"row"} spacing={1} sx={{alignItems: 'center'}}>
          <Tooltip title="Reload posts" placement="right" enterDelay={1000}>
            <div>
              <IconButton size="small" aria-label="refresh" onClick={handleRefresh}>
                <RefreshIcon />
              </IconButton>
            </div>
          </Tooltip>
          <Button
            variant="contained"
            onClick={handleCreateClick}
            startIcon={<AddIcon />}
          >
            Create
          </Button>
        </Stack>
      }
    >
      <Box sx={{ flex: 1, width: '100%'}}>
        {error ? (
          <Box sx={{ flexGrow: 1 }}>
            <Alert severity="error">{error.message}</Alert>
          </Box>
        ) : (
          <Stack spacing={2}>
            {posts.length === 0 ? (
              <Typography>No posts yet!</Typography>
            ) : (
              posts.map((p) => (
                <PostElement
                  key={p.id}
                  post={p}
                  handleDeletePost={() => {
                    try {
                      deletePost(p.id);
                      setPosts(posts.filter((post) => post.id !== p.id));
                    } catch (error) {
                      console.error("error deleting post: ", error);
                    }
                  }}
                />
              ))
            )}
          </Stack>
        )}
      </Box>
    </PageContainer>
  );
}
