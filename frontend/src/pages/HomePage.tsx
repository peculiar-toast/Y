import { useCallback, useEffect, useState, type JSX } from "react";
import { PostElement } from "../components/post/Post";
import type { Post } from "../types/postData";
import { deletePost, getAllPosts } from "../api/post";
import { Alert, Box, Button, IconButton, Stack, Tooltip, Typography } from "@mui/material";
import PageContainer from "../components/PageContainer";

import AddIcon from '@mui/icons-material/Add';
import RefreshIcon from '@mui/icons-material/Refresh';
import { Link } from "react-router";


export function HomePage(): JSX.Element {
  const [posts, setPosts] = useState<Post[]>([]);

  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<Error | null>(null)

  const fetchPosts = useCallback(async () => {
    try {
      setError(null)
      setIsLoading(true)

      const res = await getAllPosts()
      setPosts(res ?? [])
    } catch (err) {
      console.error("Error fetching posts: ", err)

      setError(
        err instanceof Error
          ? err
          : new Error("Failed to load posts")
      )
    } finally {
      setIsLoading(false)
    }
  }, [])

  useEffect(() => {
    fetchPosts()
  }, [])

  const handleRefresh = useCallback(() => {
    fetchPosts()
  }, [])

  const pageTitle = "Home Page"

  return (
    <PageContainer
      title={pageTitle}
      breadcrumbs={[{ title: pageTitle }]}
      actions={
        <Stack
          direction={"row"}
          spacing={1}
          sx={{ alignItems: 'center' }}
        >
          <Tooltip
            title="Reload posts"
            placement="right"
            enterDelay={1000}
          >
            <IconButton
              size="small"
              aria-label="refresh"
              onClick={handleRefresh}
              disabled={isLoading}
            >
              <RefreshIcon />
            </IconButton>
          </Tooltip>

          <Link to="/post/new" style={{ textDecoration: 'none' }}>
            <Button
              variant="contained"
              startIcon={<AddIcon />}
            >
              Create
            </Button>
          </Link>
        </Stack>
      }
    >
      <Box sx={{ width: '100%' }}>
        {error ? (
          <Alert severity="error">
            {error.message}
          </Alert>
        ) : isLoading ? (
          <Typography>Loading posts...</Typography>
        ) : posts.length === 0 ? (
          <Typography>No posts yet!</Typography>
        ) : (
          <Stack spacing={2}>
            {posts.map((p) => (
              <PostElement
                key={p.id}
                post={p}
                handleLikePost={() => { }}
                handleDeletePost={async () => {
                  try {
                    await deletePost(p.id);
                    setPosts((currentPosts) =>
                      currentPosts.filter((post) => post.id !== p.id)
                    );
                  } catch (error) {
                    console.error("error deleting post: ", error);
                  }
                }}
              />
            ))}
          </Stack>
        )}
      </Box>
    </PageContainer>
  );
}
