import { useEffect, useState, type JSX } from "react";
import { PostElement } from "../components/post/Post";
import type { Post } from "../types/postData";
import { deletePost, getAllPosts } from "../api/post";
import { CreatePostForm } from "../components/CreatePostForm";

export function HomePage(): JSX.Element {
  const [posts, setPosts] = useState<Post[]>([]);

  useEffect(() => {
    getAllPosts()
      .then((res) => {
        setPosts(res ?? []);
      })
      .catch((err) => {
        console.error("Error fetching posts: ", err);
      });
  }, []);

  return (
    <div>
      <h1>Home Page</h1>

      <CreatePostForm
        addPost={(p: Post) => {
          setPosts([...posts, p]);
        }}
      />

      {posts.length === 0 ? (
        <p>No posts yet!</p>
      ) : (
        posts.map((p) => (
          <div className="row" key={p.id}>
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
          </div>
        ))
      )}
    </div>
  );
}
