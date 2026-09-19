import { useEffect, useState, type JSX } from "react";
import { PostElement } from "../components/post/Post";
import type { CreatePostDto, Post } from "../types/postData";
import { deletePost, getAllPosts, savePost } from "../api/post";

export function HomePage(): JSX.Element {
  const [posts, setPosts] = useState<Post[]>([]);
  const [title, setTitle] = useState<string>("");
  const [content, setContent] = useState<string>("");

  useEffect(() => {
    getAllPosts()
      .then((res) => {
        setPosts(res ?? []);
      })
      .catch((err) => {
        console.error("Error fetching posts: ", err);
      });
  }, []);

  async function handleSubmit(e: React.SubmitEvent<HTMLFormElement>) {
    e.preventDefault();

    const newPost: CreatePostDto = { title, content };
    const post = await savePost(newPost);

    if (post !== undefined) {
      setPosts([...posts, post]);
    }
  }

  return (
    <div>
      <h1>Home Page</h1>

      <form className="container" method="POST" onSubmit={handleSubmit}>
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

        <input type="submit" value={"Post!"} />
      </form>

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
