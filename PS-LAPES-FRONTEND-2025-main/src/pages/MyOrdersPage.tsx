import { useEffect, useState } from 'react'
import api from '../services/api'

type Order = {
    id: string
    status: string
    items: {
        id: string
        name: string
        quantity: number
        price: number // em centavos
    }[]
}

const getStatusBadge = (status: string) => {
    switch (status) {
        case 'Recebido':
            return 'bg-blue-200 text-blue-800'
        case 'Em preparo':
            return 'bg-yellow-200 text-yellow-800'
        case 'Pronto':
            return 'bg-green-200 text-green-800'
        case 'Entregue':
            return 'bg-gray-200 text-gray-800'
        default:
            return 'bg-red-200 text-red-800'
    }
}

export default function MyOrdersPage() {
    const [orders, setOrders] = useState<Order[]>([])

    useEffect(() => {
        api.get('/orders')
            .then(res => setOrders(res.data))
            .catch(() => alert('Erro ao carregar pedidos'))
    }, [])

    return (
        <div className="p-4">
            <h1 className="text-2xl font-bold mb-4">Meus Pedidos</h1>
            {orders.map(order => {
                const total = order.items.reduce((sum, item) => sum + item.price * item.quantity, 0)

                return (
                    <div key={order.id} className="border p-4 mb-4 rounded shadow-sm">
                        <h2 className="font-semibold text-lg mb-1">Pedido #{order.id}</h2>
                        <span className={`inline-block px-2 py-1 rounded text-sm font-medium ${getStatusBadge(order.status)}`}>
                            {order.status}
                        </span>

                        <ul className="list-disc pl-6 mt-2 text-sm">
                            {order.items.map(item => (
                                <li key={item.id}>
                                    {item.name} x {item.quantity} — {item.price * item.quantity} centavos
                                </li>
                            ))}
                        </ul>

                        <p className="mt-3 font-semibold">
                            Total: {total} centavos
                        </p>
                    </div>
                )
            })}
        </div>
    )
}
