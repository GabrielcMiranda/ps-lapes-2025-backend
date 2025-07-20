import { useEffect, useState } from 'react'
import api from '../services/api'
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer } from 'recharts'

type OrderItem = {
    id: string
    name: string
    quantity: number
    category: {
        id: string
        name: string
    }
    price: number
}

type Order = {
    id: string
    status: string
    total: number
    items: OrderItem[]
}

export default function AdminReportsPage() {
    const [orders, setOrders] = useState<Order[]>([])
    const [loading, setLoading] = useState(true)

    useEffect(() => {
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
        fetchOrders()
    }, [])

    const totalPedidos = orders.length
    const totalArrecadado = orders.reduce((acc, o) => acc + o.total, 0)

    const vendasPorCategoria: Record<string, { name: string; quantidade: number }> = {}

    orders.forEach(order => {
        order.items.forEach(item => {
            const catId = item.category?.id
            if (!catId) return

            if (!vendasPorCategoria[catId]) {
                vendasPorCategoria[catId] = { name: item.category.name, quantidade: 0 }
            }
            vendasPorCategoria[catId].quantidade += item.quantity
        })
    })

    const chartData = Object.values(vendasPorCategoria)

    const COLORS = ['#8884d8', '#82ca9d', '#ffc658', '#ff6b6b', '#00C49F', '#FFBB28']

    return (
        <div className="p-6">
            <h1 className="text-2xl font-bold mb-4">Relatórios</h1>

            {loading ? (
                <p>Carregando...</p>
            ) : (
                <>
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-6">
                        <div className="bg-white p-4 rounded shadow">
                            <h2 className="font-semibold">Total de Pedidos</h2>
                            <p className="text-2xl">{totalPedidos}</p>
                        </div>
                        <div className="bg-white p-4 rounded shadow">
                            <h2 className="font-semibold">Valor Arrecadado</h2>
                            <p className="text-2xl">R$ {(totalArrecadado / 100).toFixed(2)}</p>
                        </div>
                    </div>

                    <div className="bg-white p-4 rounded shadow">
                        <h2 className="font-semibold mb-2">Pratos Vendidos por Categoria</h2>
                        {chartData.length > 0 ? (
                            <ResponsiveContainer width="100%" height={300}>
                                <PieChart>
                                    <Pie
                                        data={chartData}
                                        dataKey="quantidade"
                                        nameKey="name"
                                        cx="50%"
                                        cy="50%"
                                        outerRadius={100}
                                        label
                                    >
                                        {chartData.map((_, i) => (
                                            <Cell key={i} fill={COLORS[i % COLORS.length]} />
                                        ))}
                                    </Pie>
                                    <Tooltip />
                                </PieChart>
                            </ResponsiveContainer>
                        ) : (
                            <p>Nenhum dado disponível.</p>
                        )}
                    </div>
                </>
            )}
        </div>
    )
}
