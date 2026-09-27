import { AppBar, Stack, styled, Toolbar, Typography, useTheme } from "@mui/material";
import type { ReactNode } from "react";
import { Link } from 'react-router';

const LogoContainer = styled('div')({
  position: 'relative',
  height: 40,
  display: 'flex',
  alignItems: 'center',
  '& img': {
    maxHeight: 40,
  },
});

export interface SocialHeaderProps {
    logo?: ReactNode,
    title?: string,
}

export default function SocialHeader({ logo, title }: SocialHeaderProps) {
    const theme = useTheme();

    return (
        <AppBar color="inherit" position="absolute" sx={{ displayPrint: 'none'}}>
            <Toolbar sx={{ backgroundColor: 'inherit', mx: { xs: -0.75, sm: -1 }}}>
                <Stack
                    direction="row"
                    sx={{
                        justifyContent: 'space-between',
                        alignItems: 'center',
                        flexWrap: 'wrap',
                        width: '100%',
                    }}
                >
                    <Stack direction={"row"} sx={{ alignItems: 'center'}}>
                        <Link to="/" style={{ textDecoration: 'none' }}>
                            <Stack direction={"row"} sx={{ alignItems: 'center'}}>
                                {logo ? <LogoContainer>{logo}</LogoContainer> : null}
                                {title ? (
                                    <Typography
                                        variant="h6"
                                        sx={{
                                            color: (theme.vars ?? theme).palette.primary.main,
                                            fontWeight: '700',
                                            ml: 1,
                                            whiteSpace: 'nowrap',
                                            lineHeight: 1,
                                        }}
                                    >
                                        {title}
                                    </Typography>
                                ) : null}
                            </Stack>
                        </Link>
                    </Stack>

                    <Stack direction={"row"} sx={{ alignItems: 'center'}}>
                        <Link to="/user/sign-up" style={{ textDecoration: 'none' }}>
                            <Typography
                                variant="h6"
                                sx={{
                                    color: (theme.vars ?? theme).palette.primary.main,
                                    fontWeight: '700',
                                    ml: 1,
                                    whiteSpace: 'nowrap',
                                    lineHeight: 1,
                                }}
                            >
                                Sign Up
                            </Typography>
                        </Link>
                        <Link to="/user/sign-in" style={{ textDecoration: 'none' }}>
                            <Typography
                                variant="h6"
                                sx={{
                                    color: (theme.vars ?? theme).palette.primary.main,
                                    fontWeight: '700',
                                    ml: 1,
                                    whiteSpace: 'nowrap',
                                    lineHeight: 1,
                                }}
                            >
                                Sign In
                            </Typography>
                        </Link>
                    </Stack>
                </Stack>
            </Toolbar>
        </AppBar>
    )
}