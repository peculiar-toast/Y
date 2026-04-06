import type { JSX } from "react";
import type { PostData } from "../../types/postData";
import { AudioMessage } from "./AudioMessage";

export function Post({ post }: { post: PostData }): JSX.Element {
    return (
        <div className="container post">
            <div className="row">
                <span className="col widget account-info">{post.user.name}</span>
                <span className="col widget post-title">{post.title}</span>
                <span className="col widget post-date">{post.createdAt.toDateString()}</span>
            </div>
	<div className="row">
	    <AudioMessage audioMessageData={post.content} />
	</div>
            <div className="row">
                <button className="col-1">like</button>
            </div>
        </div>
    )
}
