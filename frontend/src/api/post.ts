import type { CreatePostDto, Post } from "../types/postData";
import { apiCall } from "./call";

async function getAllPosts(): Promise<Post[]> {
  try {
    const response = (await apiCall("/posts")) as Post[];
    return response;
  } catch (error) {
    console.error("Error fetching all fields: ", error);
  }
  return [];
}

async function savePost(post: CreatePostDto): Promise<Post | undefined> {
  let response: Post | undefined;
  try {
    response = (await apiCall("/posts", "POST", post)) as Post | undefined;
    return response;
  } catch (error) {
    console.error("post:savePost: failed saving post: ", error);
  }
}

async function deletePost(id: number): Promise<void> {
  try {
    apiCall(`/posts/${id}`, "DELETE");
  } catch (error) {
    console.error("post:deletePost: failed deleting post: ", error);
  }
}

// TODO add actual users
async function saveAudio(userId: number, blob: Blob): Promise<string> {
  const form = new FormData();
  form.append("userId", userId.toString());
  form.append("file", blob);
  return (await apiCall(`/audio`, "POST", form)) as string;
}

export { getAllPosts, savePost, deletePost, saveAudio };
