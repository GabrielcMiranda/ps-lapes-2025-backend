import { useEffect, useState } from 'react';
import api from '../services/api';

interface OrderDish {
    id: string;
    name: string;
    quantity: number;
    price: number;
}

interface Order {
    id: string;
    createdAt: string;
    total: number;
    status: string;
    dishes: OrderDish[];
}

export default function OrdersPage() {
    const [orders, setOrders] = useState<Order[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchOrders = async () => {
            try {
                setLoading(true);
                const response = await api.get('/orders');
                setOrders(response.data);
            } catch (err) {
                setError('Erro ao carregar pedidos');
            } finally {
                setLoading(false);
            }
        };
        fetchOrders();
    }, []);

    return (
        <div className="p-4">
            <h1 className="text-2xl font-bold mb-4">Meus Pedidos</h1>
            {loading && <p>Carregando...</p>}
            {error && <p className="text-red-500">{error}</p>}

            {orders.length === 0 && !loading && <p>Nenhum pedido encontrado.</p>}

            <div className="space-y-4">
                {orders.map(order => (
                    <div key={order.id} className="border rounded p-4">
                        <p className="text-sm text-gray-500">Pedido em {new Date(order.createdAt).toLocaleString()}</p>
                        <ul className="text-sm mt-2 list-disc pl-6">
                            {order.dishes.map(dish => (
                                <li key={dish.id}>
                                    {dish.quantity}x {dish.name} — R$ {(dish.price * dish.quantity / 100).toFixed(2)}
                                </li>
                            ))}
                        </ul>
                        <p className="font-bold mt-2">Total: R$ {(order.total / 100).toFixed(2)}</p>
                        <p className="text-sm text-blue-600 mt-1">Status: {order.status}</p>
                    </div>
                ))}
            </div>
        </div>
    );
}
