import { Box, FormGroup, Grid, TextField } from "@mui/material";
import type { Post } from "../types/postData";

export default function PostForm() {
    return (
        <Box
            component={"form"}
            onSubmit={handleSubmit}
            noValidate
            autoComplete="off"
            onReset={handleReset}
            sx={{width: '100%'}}
        >
            <FormGroup>
                <Grid container spacing={2} sx={{ mb: 2, width: '100%' }}>
                    <TextField
                    >

                    </TextField>
                </Grid>
            </FormGroup>
        </Box>
    )
}