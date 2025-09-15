import type { JSX } from 'react'
import './styles/index.scss'
import 'bootstrap/dist/css/bootstrap.min.css'
import { HomePage } from './pages/HomePage';
import type { PostData } from './types/postData';

import audio from './assets/audio-01.mp3'; // TODO remove after making actual backend

const POSTS: PostData[] = [
    {
	id: "post-01",
		title: "Il mio primo messaggio vocale",
		createdAt: new Date("2025-09-01T10:15:00Z"),
		likes: 12,
		user: {
			id: "user-01",
			name: "Alice",
			avatarUrl: "https://example.com/avatars/alice.png",
		},
		content: {
			id: "audio-01",
			duration: 42,
			audioUrl: audio,
			name: "Saluto iniziale",
		},
		comments: [
			{
				id: "comment-01",
				postId: "post-01",
				userId: "user-02",
				audioUrl: "src/assets/audio/comment-01.mp3",
				likes: 3,
			},
			{
				id: "comment-02",
				postId: "post-01",
				userId: "user-03",
				audioUrl: "src/assets/audio/comment-02.mp3",
				likes: 1,
			},
		],
		tags: [
			{ id: "tag-01", name: "introduzione" },
			{ id: "tag-02", name: "social" },
		],
	},
	{
		id: "post-02",
		title: "Discussione su TypeScript",
		createdAt: new Date("2025-09-05T14:30:00Z"),
		likes: 34,
		user: {
			id: "user-02",
			name: "Bob",
			avatarUrl: "src/assets/avatars/bob.png",
		},
		content: {
			id: "audio-02",
			duration: 87,
			audioUrl: "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
			name: "Perché usare i tipi?",
		},
		comments: [
			{
				id: "comment-03",
				postId: "post-02",
				userId: "user-01",
				audioUrl: "src/assets/audio/comment-03.mp3",
				likes: 5,
			},
		],
		tags: [
			{ id: "tag-03", name: "typescript" },
			{ id: "tag-04", name: "programmazione" },
		],
	},
	{
		id: "post-03",
		title: "Esperimenti con React",
		createdAt: new Date("2025-09-07T09:45:00Z"),
		likes: 20,
		user: {
			id: "user-03",
			name: "Charlie",
		},
		content: {
			id: "audio-03",
			duration: 65,
			audioUrl: "src/assets/audio/audio-03.mp3",
		},
		comments: [],
		tags: [
			{ id: "tag-05", name: "react" },
			{ id: "tag-06", name: "frontend" },
		],
	},
];

function App(): JSX.Element {
  return (
    <>
      <HomePage posts={POSTS} />
    </>
  )
}

export default App
