import type { CreatePostDto, Post } from "../types/postData";
import { apiCall } from "./call";

async function getAllPosts(): Promise<Post[]> {
  try {
    const response = (await apiCall("/post")) as Post[];
    return response;
  } catch (error) {
    console.error("Error fetching all fields: ", error);
  }
  return [];
}

async function savePost(post: CreatePostDto): Promise<Post | undefined> {
  let response: Post | undefined;
  try {
    response = (await apiCall("/post", "POST", post)) as Post | undefined;
    return response;
  } catch (error) {
    console.error("post:savePost: failed saving post: ", error);
  }
}

async function deletePost(id: number): Promise<void> {
  try {
    apiCall(`/post/${id}`, "DELETE");
  } catch (error) {
    console.error("post:deletePost: failed deleting post: ", error);
  }
}

export { getAllPosts, savePost, deletePost };
