import { useEffect, useState } from 'react'
import api from '../services/api'

type User = {
    id: string
    name: string
    email: string
    role: 'admin' | 'client'
}

export default function AdminUsersPage() {
    const [users, setUsers] = useState<User[]>([])
    const [loading, setLoading] = useState(true)

    const fetchUsers = async () => {
        try {
            const res = await api.get('/users')
            setUsers(res.data)
        } catch {
            alert('Erro ao buscar usuários')
        } finally {
            setLoading(false)
        }
    }

    const updateRole = async (id: string, newRole: 'admin' | 'client') => {
        try {
            await api.patch(`/users/${id}`, { role: newRole })
            fetchUsers()
        } catch {
            alert('Erro ao atualizar tipo de usuário')
        }
    }

    const deleteUser = async (id: string) => {
        if (!window.confirm('Tem certeza que deseja excluir este usuário?')) return
        try {
            await api.delete(`/users/${id}`)
            fetchUsers()
        } catch {
            alert('Erro ao excluir usuário')
        }
    }

    useEffect(() => {
        fetchUsers()
    }, [])

    return (
        <div className="p-6">
            <h1 className="text-2xl font-bold mb-4">Usuários - Administração</h1>
            {loading ? (
                <p>Carregando...</p>
            ) : (
                <table className="w-full border border-gray-300 rounded-md overflow-hidden">
                    <thead className="bg-gray-100">
                        <tr>
                            <th className="p-2 text-left">Nome</th>
                            <th className="p-2 text-left">Email</th>
                            <th className="p-2 text-left">Tipo</th>
                            <th className="p-2 text-left">Ações</th>
                        </tr>
                    </thead>
                    <tbody>
                        {users.map((user) => (
                            <tr key={user.id} className="border-t">
                                <td className="p-2">{user.name}</td>
                                <td className="p-2">{user.email}</td>
                                <td className="p-2">
                                    <select
                                        className="border rounded px-2 py-1"
                                        value={user.role}
                                        onChange={(e) =>
                                            updateRole(user.id, e.target.value as 'admin' | 'client')
                                        }
                                    >
                                        <option value="client">Cliente</option>
                                        <option value="admin">Administrador</option>
                                    </select>
                                </td>
                                <td className="p-2">
                                    <button
                                        className="text-red-600 hover:underline"
                                        onClick={() => deleteUser(user.id)}
                                    >
                                        Excluir
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </div>
    )
}
