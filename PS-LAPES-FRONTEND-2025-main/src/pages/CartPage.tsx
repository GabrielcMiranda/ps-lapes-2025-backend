import { useCart } from "../context/CartContext";

export default function CartPage() {
    const { cart, removeFromCart, updateQuantity } = useCart();

    const total = cart.reduce((acc, item) => acc + item.price * item.quantity, 0);

    return (
        <div className="p-4">
            <h1 className="text-2xl font-bold mb-4">Seu Carrinho</h1>
            {cart.length === 0 ? (
                <p>O carrinho está vazio.</p>
            ) : (
                <>
                    <ul className="space-y-4">
                        {cart.map(item => (
                            <li key={item.id} className="border p-4 rounded flex justify-between items-center">
                                <div>
                                    <h2 className="text-lg font-semibold">{item.name}</h2>
                                    <p className="text-sm">Preço: R$ {(item.price / 100).toFixed(2)}</p>
                                </div>
                                <div className="flex items-center gap-2">
                                    <input
                                        type="number"
                                        min="1"
                                        value={item.quantity}
                                        onChange={e => updateQuantity(item.id, parseInt(e.target.value))}
                                        className="w-16 border rounded px-1"
                                    />
                                    <button onClick={() => removeFromCart(item.id)} className="text-red-500">Remover</button>
                                </div>
                            </li>
                        ))}
                    </ul>
                    <p className="mt-4 font-bold text-xl">Total: R$ {(total / 100).toFixed(2)}</p>
                    <a href="/checkout" className="block mt-4 bg-green-600 text-white px-4 py-2 rounded text-center">Finalizar Pedido</a>
                </>
            )}
        </div>
    );
}