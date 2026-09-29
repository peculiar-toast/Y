import { Box, Stack, TextField, Button } from "@mui/material";
import { useState } from "react";
import PageContainer from "../components/PageContainer";
import { useAuth } from "../auth/AuthProvider";
import { useNavigate } from "react-router";

export default function UserCreationPage() {
    const { login } = useAuth();
    const navigate = useNavigate();

    const [username, setUsername] = useState("")
    const [password, setPassword] = useState("")

    const [submitting, setSubmitting] = useState(false)

    async function handleCreateUser(
        event: React.FormEvent<HTMLFormElement>
    ) {
        event.preventDefault()
        setSubmitting(true)

        try {
            await login(username, password)
            navigate("/", { replace: true })
        } catch (err) {
            console.error(err);
        } finally {
            setSubmitting(false)
        }
    }

    function handleReset() {
        setUsername("")
        setPassword("")
    }

    const pageTitle = "Sign In"

    return (
        <PageContainer
            title={pageTitle}
            breadcrumbs={[{ path: "/", title: "Home Page" }, { title: pageTitle }]}
        >
            <Box
                component="form"
                onSubmit={handleCreateUser}
                onReset={handleReset}
            >
                <Stack spacing={2}>
                <TextField
                        name="username"
                        label="Username"
                        placeholder="Username..."
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                        fullWidth
                    />

                    <TextField
                        name="password"
                        label="Password"
                        placeholder="What's on your mind?"
                        value={password ? password : ""}
                        onChange={(e) => setPassword(e.target.value)}
                        fullWidth
                        required
                    />

                    <Stack direction="row" spacing={1}>
                        <Button type="submit" variant="contained" disabled={submitting}>
                            {submitting ? "Loggin in..." : "Log in"}
                        </Button>
                        <Button type="reset" variant="outlined" disabled={submitting}>
                            Reset
                        </Button>
                    </Stack>
                </Stack>
            </Box>
        </PageContainer>
    );
}