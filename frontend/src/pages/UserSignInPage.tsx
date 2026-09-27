import { Box, Stack, TextField, Button } from "@mui/material";
import { useState } from "react";
import PageContainer from "../components/PageContainer";

export default function UserCreationPage() {
    const [username, setUsername] = useState<string | null>(null)
    const [email, setEmail] = useState<string | null>(null)
    const [password, setPassword] = useState<string | null>(null)

    const [submitting, setSubmitting] = useState(false)
    
    async function handleCreateUser() {
        throw "TODO not implemented"
    }

    function handleReset() {
        throw "TODO not implemented"
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
                        name="email"
                        placeholder="Email..."
                        value={email ? email : ""}
                        onChange={(e) => setEmail(e.target.value)}
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