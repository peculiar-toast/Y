import type { Profile } from "./Profile";

type CreatePostDto = {
  title: string;
  audio: Blob;
};

type Post = {
  id: number;
  audio: string;
  user: Profile,
} & CreatePostDto;

export type { CreatePostDto, Post };
