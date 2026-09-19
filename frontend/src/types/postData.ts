type CreatePostDto = {
  title: string;
  content: string;
};

type Post = {
  id: number;
  audio: string;
} & CreatePostDto;

export type { CreatePostDto, Post };
