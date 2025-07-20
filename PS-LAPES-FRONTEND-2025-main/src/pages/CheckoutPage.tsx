import { useCart } from '../context/CartContext';
import api from '../services/api';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

export default function CheckoutPage() {
    const { cart, clearCart } = useCart();
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const total = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);

    const handleCheckout = async () => {
        setLoading(true);
        setError(null);
        try {
            const orderItems = cart.map(item => ({
                dishId: item.id,
                quantity: item.quantity
            }));

            await api.post('/orders', { items: orderItems });

            clearCart();
            navigate('/orders');
        } catch (err) {
            setError('Erro ao finalizar o pedido.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="p-4">
            <h1 className="text-2xl font-bold mb-4">Finalizar Pedido</h1>

            {cart.length === 0 ? (
                <p>Seu carrinho está vazio.</p>
            ) : (
                <>
                    <ul className="mb-4">
                        {cart.map(item => (
                            <li key={item.id} className="mb-2">
                                {item.name} x {item.quantity} - R$ {(item.price * item.quantity / 100).toFixed(2)}
                            </li>
                        ))}
                    </ul>

                    <p className="font-bold mb-4">Total: R$ {(total / 100).toFixed(2)}</p>

                    <button
                        className="bg-green-600 text-white px-4 py-2 rounded"
                        onClick={handleCheckout}
                        disabled={loading}
                    >
                        {loading ? 'Enviando...' : 'Confirmar Pedido'}
                    </button>

                    {error && <p className="text-red-500 mt-2">{error}</p>}
                </>
            )}
        </div>
    );
}