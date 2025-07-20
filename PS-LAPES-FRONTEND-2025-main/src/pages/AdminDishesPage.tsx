import { useState, useEffect } from 'react'
import api from '../services/api'

type Category = { id: string; name: string }

export default function AdminDishesPage() {
    const [name, setName] = useState('')
    const [description, setDescription] = useState('')
    const [price, setPrice] = useState('')
    const [image, setImage] = useState<File | null>(null)
    const [categories, setCategories] = useState<Category[]>([])
    const [selectedCategoryId, setSelectedCategoryId] = useState('')

    useEffect(() => {
        api.get('/categories').then(res => setCategories(res.data))
    }, [])

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()

        if (!name || !description || !price || !image || !selectedCategoryId) {
            alert('Preencha todos os campos!')
            return
        }

        const formData = new FormData()
        formData.append('name', name)
        formData.append('description', description)
        formData.append('price', price)
        formData.append('categoryId', selectedCategoryId)
        formData.append('image', image)

        try {
            await api.post('/menu/items', formData, {
                headers: { 'Content-Type': 'multipart/form-data' }
            })
            alert('Prato cadastrado com sucesso!')
            setName('')
            setDescription('')
            setPrice('')
            setImage(null)
            setSelectedCategoryId('')
        } catch (err) {
            alert('Erro ao cadastrar prato')
        }
    }

    return (
        <div className="max-w-xl mx-auto p-4">
            <h1 className="text-2xl font-bold mb-4">Cadastrar novo prato</h1>

            <form onSubmit={handleSubmit} className="space-y-4">
                <input
                    type="text"
                    value={name}
                    onChange={e => setName(e.target.value)}
                    placeholder="Nome do prato"
                    className="w-full border p-2"
                />
                <textarea
                    value={description}
                    onChange={e => setDescription(e.target.value)}
                    placeholder="Descrição"
                    className="w-full border p-2"
                />
                <input
                    type="number"
                    value={price}
                    onChange={e => setPrice(e.target.value)}
                    placeholder="Preço (em centavos)"
                    className="w-full border p-2"
                />
                <select
                    value={selectedCategoryId}
                    onChange={e => setSelectedCategoryId(e.target.value)}
                    className="w-full border p-2"
                >
                    <option value="">Selecione uma categoria</option>
                    {categories.map(c => (
                        <option key={c.id} value={c.id}>{c.name}</option>
                    ))}
                </select>
                <input
                    type="file"
                    accept="image/*"
                    onChange={e => setImage(e.target.files?.[0] ?? null)}
                    className="w-full border p-2"
                />
                <button type="submit" className="bg-blue-600 text-white px-4 py-2 rounded">
                    Cadastrar prato
                </button>
            </form>
        </div>
    )
}
