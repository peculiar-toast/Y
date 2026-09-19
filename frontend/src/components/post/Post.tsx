import type { JSX } from "react";
import type { Post } from "../../types/postData";

type PostElementProps = {
  post: Post;
  handleDeletePost: () => void;
};

export function PostElement({
  post,
  handleDeletePost,
}: PostElementProps): JSX.Element {
  let audioEl = <p>Could not load audio</p>;

  if (post.id) {
    audioEl = (
      <audio
        src={`http://localhost:8000/api/audio/${post.id}/data`}
        controls
      ></audio>
    );
  }

  return (
    <div className="container post">
      <div className="row">
        <span className="col widget title">{post.title ?? "No title"}</span>

        <span className="col widget">{post.content}</span>

        <div className="row">{audioEl}</div>
      </div>

      <div className="row widget">
        <button className="col-1">like</button>
        <button className="col-1" onClick={handleDeletePost}>
          delete
        </button>
      </div>
    </div>
  );
}
