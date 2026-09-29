import { CreatePostForm } from "../components/CreatePostForm";
import PageContainer from "../components/PageContainer";

export default function PostCreationPage() {

    const pageTitle = "Create Post"

    return (
        <PageContainer
            title={pageTitle}
            breadcrumbs={[{ path: "/", title: "Home Page" }, { title: pageTitle }]}
        >
            <CreatePostForm onSubmit={(post) => console.log(post)} />
        </PageContainer>
    )
}