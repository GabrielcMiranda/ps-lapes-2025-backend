import { BrowserRouter } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import { Router } from './Router'
import { CartProvider } from './context/CartContext'
import Navbar from './components/Navbar'

export default function App() {
  return (
    <AuthProvider>
      <CartProvider>
        <BrowserRouter>
          <Navbar />
          <Router />
        </BrowserRouter>
      </CartProvider>
    </AuthProvider>
  )
}
