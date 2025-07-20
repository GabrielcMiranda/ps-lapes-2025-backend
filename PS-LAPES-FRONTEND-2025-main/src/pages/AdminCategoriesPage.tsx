import { useState, useEffect } from 'react'
import api from '../services/api'

type Category = { id: string; name: string }

export default function AdminCategoriesPage() {
    const [categories, setCategories] = useState<Category[]>([])
    const [name, setName] = useState('')

    useEffect(() => {
        api.get('/categories').then(res => setCategories(res.data))
    }, [])

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        const res = await api.post('/categories', { name })
        setCategories(prev => [...prev, res.data])
        setName('')
    }

    const handleDelete = async (id: string) => {
        await api.delete(`/categories/${id}`)
        setCategories(prev => prev.filter(c => c.id !== id))
    }

    return (
        <div className="p-4 max-w-lg mx-auto">
            <h1 className="text-2xl font-bold mb-4">Gerenciar Categorias</h1>

            <form onSubmit={handleSubmit} className="flex gap-2 mb-4">
                <input value={name} onChange={e => setName(e.target.value)} placeholder="Nova categoria" className="border px-2 py-1 flex-1" />
                <button className="bg-blue-600 text-white px-4">Adicionar</button>
            </form>

            <ul>
                {categories.map(cat => (
                    <li key={cat.id} className="flex justify-between mb-2">
                        <span>{cat.name}</span>
                        <button onClick={() => handleDelete(cat.id)} className="text-red-600">Excluir</button>
                    </li>
                ))}
            </ul>
        </div>
    )
}
