import { useEffect, useState } from 'react'
import api from '../services/api'

interface OrderItem {
    id: string
    name: string
    quantity: number
}

interface Order {
    id: string
    status: string
    user: {
        id: string
        name: string
        email: string
    }
    items: OrderItem[]
}

const statusOptions = ["Recebido", "Em preparo", "Pronto", "Entregue"]

export default function AdminOrdersPage() {
    const [orders, setOrders] = useState<Order[]>([])
    const [loading, setLoading] = useState(true)

    const fetchOrders = async () => {
        try {
            const res = await api.get('/orders/all')
            setOrders(res.data)
        } catch {
            alert('Erro ao buscar pedidos')
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        fetchOrders()
    }, [])

    const updateStatus = async (id: string, newStatus: string) => {
        try {
            await api.patch(`/orders/${id}`, { status: newStatus })
            fetchOrders()
        } catch {
            alert('Erro ao atualizar status')
        }
    }

    return (
        <div className="p-6">
            <h1 className="text-2xl font-bold mb-4">Pedidos - Administração</h1>
            {loading ? <p>Carregando...</p> : (
                <div className="space-y-4">
                    {orders.map(order => (
                        <div key={order.id} className="border p-4 rounded shadow-sm">
                            <p className="font-semibold">Pedido #{order.id}</p>
                            <p>Cliente: {order.user.name} ({order.user.email})</p>
                            <p>Status atual: <strong>{order.status}</strong></p>
                            <select
                                className="mt-2 border rounded px-2 py-1"
                                value={order.status}
                                onChange={(e) => updateStatus(order.id, e.target.value)}
                            >
                                {statusOptions.map(s => (
                                    <option key={s} value={s}>{s}</option>
                                ))}
                            </select>
                            <ul className="mt-3 list-disc pl-5">
                                {order.items.map(i => (
                                    <li key={i.id}>{i.name} x {i.quantity}</li>
                                ))}
                            </ul>
                        </div>
                    ))}
                </div>
            )}
        </div>
    )
}
