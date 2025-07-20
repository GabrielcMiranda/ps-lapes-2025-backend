import { Link } from 'react-router-dom';
import { useContext, useState } from 'react';
import AuthContext from '../context/AuthContext';
import { Menu, X } from 'lucide-react';

export default function Navbar() {
    const { user, signOut } = useContext(AuthContext);
    const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

    const toggleMobileMenu = () => setMobileMenuOpen(!mobileMenuOpen);

    return (
        <nav className="bg-white shadow sticky top-0 z-50" aria-label="Barra de navegação principal">
            <div className="max-w-screen-lg mx-auto px-4 py-3 flex items-center justify-between">
                <Link to="/" className="text-xl font-bold hover:text-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-400 rounded">LAPES</Link>

                <button onClick={toggleMobileMenu} className="sm:hidden text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-400 rounded" aria-label="Abrir menu">
                    {mobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
                </button>

                <ul className="hidden sm:flex gap-4 items-center">
                    <li><Link to="/menu" className="hover:underline focus:outline-none focus:ring-2 focus:ring-blue-400 rounded">Cardápio</Link></li>
                    {user && <li><Link to="/my-orders" className="hover:underline focus:outline-none focus:ring-2 focus:ring-blue-400 rounded">Meus Pedidos</Link></li>}
                    {user?.role === 'admin' && <li><Link to="/admin/orders" className="hover:underline focus:outline-none focus:ring-2 focus:ring-blue-400 rounded">Admin</Link></li>}
                    {user ? (
                        <li>
                            <button onClick={signOut} className="text-red-600 hover:underline focus:outline-none focus:ring-2 focus:ring-red-400 rounded">Sair</button>
                        </li>
                    ) : (
                        <li><Link to="/" className="hover:underline focus:outline-none focus:ring-2 focus:ring-blue-400 rounded">Login</Link></li>
                    )}
                </ul>
            </div>

            {mobileMenuOpen && (
                <div className="sm:hidden px-4 pb-4">
                    <ul className="flex flex-col gap-2">
                        <li><Link to="/menu" onClick={toggleMobileMenu} className="hover:underline focus:outline-none focus:ring-2 focus:ring-blue-400 rounded">Cardápio</Link></li>
                        {user && <li><Link to="/my-orders" onClick={toggleMobileMenu} className="hover:underline focus:outline-none focus:ring-2 focus:ring-blue-400 rounded">Meus Pedidos</Link></li>}
                        {user?.role === 'admin' && <li><Link to="/admin/orders" onClick={toggleMobileMenu} className="hover:underline focus:outline-none focus:ring-2 focus:ring-blue-400 rounded">Admin</Link></li>}
                        {user ? (
                            <li><button onClick={() => { toggleMobileMenu(); signOut(); }} className="text-red-600 hover:underline focus:outline-none focus:ring-2 focus:ring-red-400 rounded">Sair</button></li>
                        ) : (
                            <li><Link to="/" onClick={toggleMobileMenu} className="hover:underline focus:outline-none focus:ring-2 focus:ring-blue-400 rounded">Login</Link></li>
                        )}
                    </ul>
                </div>
            )}
        </nav>
    );
}