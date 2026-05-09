type CreatePostDto = {
  title: string;
  content: string;
};

type Post = {
  id: number;
} & CreatePostDto;

export type { CreatePostDto, Post };
