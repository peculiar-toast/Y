import type { AudioCommentData } from "./audioCommentData";
import type { AudioMessageData } from "./audioMessageData";
import type { TagData } from "./tagData";
import type { UserData } from "./userData";

export interface PostData {
    id: string;
    title: string;
    createdAt: Date;
    likes: number;
    user: UserData;
    content: AudioMessageData;
    comments: AudioCommentData[];
    tags: TagData[];
}