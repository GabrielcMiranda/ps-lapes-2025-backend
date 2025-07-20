// src/Router.tsx
import { Routes, Route } from 'react-router-dom'

import { Teste } from './pages/teste'
import Login from './pages/Login'
import Register from './pages/Register'

import { Home } from './pages/Home'
import MenuPage from './pages/MenuPage'
import DishDetailPage from './pages/DishDetailPage'
import CartPage from './pages/CartPage'
import CheckoutPage from './pages/CheckoutPage'
import OrdersPage from './pages/OrderPage'

import AdminCategoriesPage from './pages/AdminCategoriesPage'
import AdminDishesPage from './pages/AdminDishesPage'
import NewDishPage from './pages/NewDishPage'
import AdminOrdersPage from './pages/AdminOrdersPage'
import AdminUsersPage from './pages/AdminUsersPage'

import PrivateRoute from './routes/PrivateRoute'
import AdminRoute from './routes/AdminRoute'

export function Router() {
  return (
    <Routes>
      {/* Rotas públicas */}
      <Route path="/" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/teste" element={<Teste />} />

      {/* Rotas privadas para usuários autenticados/com login */}
      <Route element={<PrivateRoute />}>
        <Route path="/home" element={<Home />} />
        <Route path="/menu" element={<MenuPage />} />
        <Route path="/dish/:id" element={<DishDetailPage />} />
        <Route path="/cart" element={<CartPage />} />
        <Route path="/checkout" element={<CheckoutPage />} />
        <Route path="/orders" element={<OrdersPage />} />
      </Route>

      {/* Rotas para adm */}
      <Route element={<AdminRoute />}>
        <Route path="/admin/categories" element={<AdminCategoriesPage />} />
        <Route path="/admin/dishes" element={<AdminDishesPage />} />
        <Route path="/admin/new-dish" element={<NewDishPage />} />
        <Route path="/admin/orders" element={<AdminOrdersPage />} />
        <Route path="/admin/users" element={<AdminUsersPage />} />
      </Route>
    </Routes>
  )
}
