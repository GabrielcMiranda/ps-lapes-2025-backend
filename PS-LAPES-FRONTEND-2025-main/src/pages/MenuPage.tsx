import { useEffect, useState } from 'react'
import api from '../services/api'
import { Link } from 'react-router-dom'

type Category = { id: string; name: string }
type Dish = { id: string; name: string; description: string; price: number; categoryId: string }

export default function MenuPage() {
    const [categories, setCategories] = useState<Category[]>([])
    const [dishes, setDishes] = useState<Dish[]>([])
    const [selected, setSelected] = useState<string | null>(null)
    const [isLoading, setIsLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        const fetchData = async () => {
            try {
                setIsLoading(true)
                const [catRes, dishRes] = await Promise.all([
                    api.get('/menu/categories'),
                    api.get('/menu/items')
                ])
                setCategories(catRes.data)
                setDishes(dishRes.data)
            } catch (err: any) {
                console.error(err)
                if (err.response?.status === 401) {
                    setError('Sessão expirada. Faça login novamente.')
                } else {
                    setError('Erro ao carregar cardápio.')
                }
            }
        }
        fetchData()
    }, [])

    const filtered = selected ? dishes.filter(d => d.categoryId === selected) : dishes

    return (
        <div className="p-4">
            <h1 className="text-2xl font-bold mb-4">Cardápio</h1>

            {isLoading && <p>Carregando...</p>}
            {error && <p className="text-red-500">{error}</p>}

            {!isLoading && !error && (
                <>
                    <div className="flex gap-2 mb-6">
                        {categories.map(c => (
                            <button
                                key={c.id}
                                className={`px-3 py-1 rounded ${selected === c.id ? 'bg-blue-600 text-white' : 'bg-gray-200'}`}
                                onClick={() => setSelected(c.id)}
                            >
                                {c.name}
                            </button>
                        ))}
                        <button onClick={() => setSelected(null)} className="underline text-sm">Ver todos</button>
                    </div>

                    <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
                        {filtered.map(d => (
                            <Link to={`/dish/${d.id}`} key={d.id} className="border p-4 rounded hover:shadow">
                                <h2 className="text-xl font-semibold">{d.name}</h2>
                                <p className="text-sm">{d.description}</p>
                                <p className="text-green-700 font-bold mt-2">R$ {(d.price / 100).toFixed(2)}</p>
                            </Link>
                        ))}
                    </div>
                </>
            )}
        </div>
    )
}
