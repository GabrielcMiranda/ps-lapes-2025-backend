import { useContext } from 'react'
import { Navigate, Outlet } from 'react-router-dom'
import AuthContext from '../context/AuthContext'

export default function PrivateRoute() {
    const { user } = useContext(AuthContext)

    if (!user) {
        return <Navigate to="/" replace /> // redireciona para login
    }

    return <Outlet />
}
