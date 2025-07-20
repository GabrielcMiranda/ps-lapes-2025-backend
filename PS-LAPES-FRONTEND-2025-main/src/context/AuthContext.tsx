import { createContext, useState, useEffect, ReactNode } from 'react'
import api from '../services/api'

type User = {
    id: string
    email: string
    name: string
    role: 'admin' | 'client'
}

type AuthContextType = {
    user: User | null
    signIn: (email: string, password: string) => Promise<void>
    signOut: () => void
}

const AuthContext = createContext<AuthContextType>({} as AuthContextType)

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const [user, setUser] = useState<User | null>(null)

    // Carrega user/token do localStorage ao iniciar
    useEffect(() => {
        const storedUser = localStorage.getItem('user')
        if (storedUser) {
            setUser(JSON.parse(storedUser))
        }
    }, [])

    // Função de login
    const signIn = async (email: string, password: string) => {
        const response = await api.post('/auth/login', { email, password })
        const { token, user: userData } = response.data

        localStorage.setItem('token', token)
        localStorage.setItem('user', JSON.stringify(userData))

        setUser(userData)
    }

    // Função de logout
    const signOut = () => {
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        setUser(null)
    }

    return (
        <AuthContext.Provider value={{ user, signIn, signOut }}>
            {children}
        </AuthContext.Provider>
    )
}

export default AuthContext
