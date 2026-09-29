import { Box, Stack, TextField, Button } from "@mui/material";
import { useState } from "react";
import PageContainer from "../components/PageContainer";
import { createUser } from "../api/user";

export default function UserCreationPage() {
    const [username, setUsername] = useState("")
    const [email, setEmail] = useState("")
    const [password, setPassword] = useState("")

    const [submitting, setSubmitting] = useState(false)
    
    async function handleCreateUser(
        event : React.FormEvent<HTMLFormElement>
    ) {
        event.preventDefault();
        setSubmitting(true)
        
        try {
            await createUser({ username, email, password })

            // TODO login / redirect
        } catch (error) {
            console.error(error);
        } finally {
            setSubmitting(false)
        }
    }

    function handleReset() {
        setUsername("")
        setEmail("")
        setPassword("")
    }

    const pageTitle = "Sign Up"

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
                        name="email"
                        label="Email"
                        placeholder="myemail@example.com"
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                        fullWidth
                    />

                    <TextField
                        name="password"
                        label="Password"
                        placeholder="What's on your mind?"
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        fullWidth
                        required
                    />

                    <Stack direction="row" spacing={1}>
                        <Button type="submit" variant="contained" disabled={submitting}>
                            {submitting ? "Creating..." : "Create user"}
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