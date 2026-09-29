import { createContext, useContext, useEffect, useState, type ReactNode } from "react";
import { getMe, loginUser } from "../api/user";

type User = {
    id: number;
    username: string;
    avatar: string | null;
}

type AuthContextType = {
    user: User | null;
    accessToken: string | null;
    loading: boolean;
    login: ( username: string, password: string ) => Promise<void>;
    logout: () => void;
}

const AuthContext = createContext<AuthContextType | null>(null);

type AuthProviderProps = {
    children: ReactNode;
};

export function AuthProvider({ children }: AuthProviderProps) {
    const [accessToken, setAccessToken] = useState<string | null>(
        localStorage.getItem("access_token"),
    );

    const [user, setUser] = useState<User | null>(null);
    const [loading, setLoading] = useState<boolean>(false);

    useEffect(() => {
        async function loadUser() {
            if (!accessToken) {
                setLoading(false);
                return;
            }

            try {
                const user = await getMe(accessToken);
                setUser(user.user);  
            } catch (error) {
                console.error("Failed to fetch user data:", error);
            } finally {
                setLoading(false);
            }
        }

        loadUser();
    }, [accessToken]);

    async function login(username: string, password: string) {
        const tokens = await loginUser({
            username,
            password,
        });

        localStorage.setItem("access_token", tokens.access);
        localStorage.setItem("refresh_token", tokens.refresh);

        setAccessToken(tokens.access);

        const user = await getMe(tokens.access);
        setUser(user.user);
    }

    function logout() {
        localStorage.removeItem("access_token");
        localStorage.removeItem("refresh_token");

        setAccessToken(null);
        setUser(null);
    }

    return (
        <AuthContext.Provider
            value={{
                user,
                accessToken,
                loading,
                login,
                logout,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    const context = useContext(AuthContext);

    if (!context) {
        throw new Error("useAuth must be used inside AuthProvider");
    }

    return context;
}