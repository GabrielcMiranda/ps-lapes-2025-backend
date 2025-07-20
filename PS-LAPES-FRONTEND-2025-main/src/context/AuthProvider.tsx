import { createContext, useState, ReactNode } from 'react';

export type AuthData = {
    user: string;
    roles: string[];
    accessToken: string;
};

export type AuthContextType = {
    auth: AuthData | null;
    setAuth: React.Dispatch<React.SetStateAction<AuthData | null>>;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const [auth, setAuth] = useState<AuthData | null>(null);

    return (
        <AuthContext.Provider value={{ auth, setAuth }}>
            {children}
        </AuthContext.Provider>
    );
};

export default AuthContext;
