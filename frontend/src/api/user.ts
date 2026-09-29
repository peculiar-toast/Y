import { apiCall } from "./call"

type CreateUserDTO = {
    email: string,
} & UserDTO

type UserDTO = {
    username: string,
    password: string,
}

async function createUser({ username, email, password }: CreateUserDTO) {
    if (!username.trim()) throw new Error("Username cannot be empty");
    if (!password.trim()) throw new Error("Password cannot be empty");

    return apiCall("/auth/register/", "POST", { username, email, password })
}

async function loginUser({ username, password }: UserDTO) {
    if (!username.trim()) throw new Error("Username cannot be empty");
    if (!password.trim()) throw new Error("Password cannot be empty");

    return apiCall("/auth/token/", "POST", { username, password })
}

async function getMe(accessToken: string) {
    return apiCall(
        "/me/",
        "GET",
        {},
        {
            Authorization: `Bearer ${accessToken}`,
        },
    );
}

export { createUser, loginUser, getMe }