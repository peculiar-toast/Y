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
  return (
    <div className="container post">
      <div className="row">
        <span className="col widget post-title">{post.title}</span>
        <span className="col widget post-date">{post.content}</span>
      </div>

      <div className="row">
        <button className="col-1">like</button>
      </div>

      <div className="row">
        <button className="col-1" onClick={handleDeletePost}>
          delete
        </button>
      </div>
    </div>
  );
}
