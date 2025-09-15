import type { JSX } from "react";
import type { PostData } from "../types/postData";
import { Post } from "../components/layout/Post";

export function HomePage({ posts }: { posts: PostData[] }): JSX.Element {

    const rendered: JSX.Element[] = posts.map(p => <Post key={p.id} post={p} />)

    return (
        <div className="container">
            {rendered}
        </div>
    )
}
