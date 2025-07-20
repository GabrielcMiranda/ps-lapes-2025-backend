import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import api from '../services/api'
import { useCart } from '../context/CartContext';

type Dish = {
    id: string
    name: string
    description: string
    price: number
    imageUrl: string
}

export default function DishDetailPage() {
    const { id } = useParams()
    const [dish, setDish] = useState<Dish | null>(null)
    const [isLoading, setIsLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const { addToCart } = useCart();

    useEffect(() => {
        const fetchDish = async () => {
            try {
                setIsLoading(true)
                const response = await api.get(`/menu/items/${id}`)
                setDish(response.data)
            } catch (err) {
                setError('Erro ao carregar prato.')
            } finally {
                setIsLoading(false)
            }
        }
        fetchDish()
    }, [id])

    if (isLoading) return <p className="p-4">Carregando prato...</p>
    if (error) return <p className="p-4 text-red-500">{error}</p>
    if (!dish) return <p className="p-4">Prato não encontrado.</p>

    return (
        <div className="p-4">
            <h1 className="text-2xl font-bold mb-2">{dish.name}</h1>
            <img src={dish.imageUrl} alt={dish.name} className="w-full max-w-md rounded" />
            <p className="mt-4 text-sm">{dish.description}</p>
            <p className="mt-2 text-green-700 font-bold text-lg">R$ {(dish.price / 100).toFixed(2)}</p>
            <button
                onClick={() => addToCart({ id: dish.id, name: dish.name, price: dish.price, quantity: 1 })}
                className="mt-4 bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
            >
                Adicionar ao carrinho
            </button>
        </div>
    )

}
