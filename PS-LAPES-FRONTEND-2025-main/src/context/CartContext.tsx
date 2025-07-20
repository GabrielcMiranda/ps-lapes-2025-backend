import { createContext, useState, ReactNode, useContext } from 'react';

type Dish = {
    id: string;
    name: string;
    price: number;
    quantity: number;
};

type CartContextType = {
    cart: Dish[];
    addToCart: (dish: Dish) => void;
    removeFromCart: (id: string) => void;
    updateQuantity: (id: string, quantity: number) => void;
    clearCart: () => void;
};

const CartContext = createContext<CartContextType | undefined>(undefined);

export const CartProvider = ({ children }: { children: ReactNode }) => {
    const [cart, setCart] = useState<Dish[]>([]);

    const addToCart = (dish: Dish) => {
        setCart(prev => {
            const exists = prev.find(d => d.id === dish.id);
            if (exists) {
                return prev.map(d => d.id === dish.id ? { ...d, quantity: d.quantity + 1 } : d);
            }
            return [...prev, { ...dish, quantity: 1 }];
        });
    };

    const removeFromCart = (id: string) => {
        setCart(prev => prev.filter(d => d.id !== id));
    };

    const updateQuantity = (id: string, quantity: number) => {
        setCart(prev =>
            prev.map(d => d.id === id ? { ...d, quantity } : d)
        );
    };

    const clearCart = () => setCart([]);

    return (
        <CartContext.Provider value={{ cart, addToCart, removeFromCart, updateQuantity, clearCart }}>
            {children}
        </CartContext.Provider>
    );
};

export const useCart = () => {
    const context = useContext(CartContext);
    if (!context) throw new Error("useCart must be used within a CartProvider");
    return context;
};