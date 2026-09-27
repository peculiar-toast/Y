import { Box, Toolbar } from "@mui/material";
import { Outlet } from "react-router";
import SocialHeader from "./SocialHeader";
import SocialLogo from '../assets/logo.svg?react';


export default function SocialLayout() {
    return (
        <Box
            sx={{
                position: "relative",
                display: "flex",
                overflow: "hidden",
                height: "100%",
                width: "100%",
            }}
        >
            <SocialHeader logo={(<SocialLogo width={48} height={48}/>)} title="The Audio Social"/>

            <Box
                sx={{
                    display: "flex",
                    flexDirection: "column",
                    flex: 1,
                    minWidth: 0
                }}
            >
                <Toolbar sx={{ displayPrint: "none"}} />
                <Box
                    component="main"
                    sx={{
                        display: 'flex',
                        flexDirection: 'column',
                        flex: 1,
                        overflow: 'auto',
                    }}
                >
                    <Outlet />
                </Box>
            </Box>
        </Box>
    )
}