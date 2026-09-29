import type { CreatePostDto, Post } from "../types/postData";
import { apiCall } from "./call";

async function getAllPosts(): Promise<Post[]> {
  try {
    const response = (await apiCall("/feed")) as Post[];
    return response;
  } catch (error) {
    console.error("Error fetching all fields: ", error);
  }
  return [];
}

async function savePost(post: CreatePostDto): Promise<Post | undefined> {
  const formData = new FormData();
  formData.append("title", post.title);
  formData.append("audio", post.audio);
  
  try {
    return (await apiCall("/posts/", "POST", formData)) as Post;
  } catch (error) {
    console.error("post:savePost: failed saving post: ", error);
  }
  return undefined;
}

async function deletePost(id: number): Promise<void> {
  try {
    apiCall(`/posts/${id}/`, "DELETE");
  } catch (error) {
    console.error("post:deletePost: failed deleting post: ", error);
  }
}

export { getAllPosts, savePost, deletePost };
