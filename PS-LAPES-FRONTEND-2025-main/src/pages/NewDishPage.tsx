import { useState } from 'react'
import api from '../services/api'
import { useNavigate } from 'react-router-dom'

export default function NewDishPage() {
    const [name, setName] = useState('')
    const [description, setDescription] = useState('')
    const [price, setPrice] = useState('')
    const [image, setImage] = useState<File | null>(null)
    const [error, setError] = useState('')
    const navigate = useNavigate()

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        if (!name || !description || !price) {
            setError('Preencha todos os campos.')
            return
        }

        const formData = new FormData()
        formData.append('name', name)
        formData.append('description', description)
        formData.append('price', (Number(price) * 100).toString()) // centavos
        if (image) formData.append('image', image)

        try {
            await api.post('/menu/items', formData, {
                headers: { 'Content-Type': 'multipart/form-data' }
            })
            navigate('/')
        } catch (err) {
            console.error(err)
            setError('Erro ao cadastrar prato.')
        }
    }

    return (
        <div className="p-4 max-w-md mx-auto">
            <h1 className="text-2xl font-bold mb-4">Novo Prato</h1>
            {error && <p className="text-red-600">{error}</p>}
            <form onSubmit={handleSubmit} className="flex flex-col gap-3">
                <input
                    type="text"
                    placeholder="Nome"
                    className="border p-2 rounded"
                    value={name}
                    onChange={e => setName(e.target.value)}
                />
                <textarea
                    placeholder="Descrição"
                    className="border p-2 rounded"
                    value={description}
                    onChange={e => setDescription(e.target.value)}
                />
                <input
                    type="number"
                    placeholder="Preço (ex: 29.90)"
                    className="border p-2 rounded"
                    value={price}
                    onChange={e => setPrice(e.target.value)}
                />
                <input
                    type="file"
                    className="border p-2 rounded"
                    onChange={e => setImage(e.target.files?.[0] || null)}
                />
                <button type="submit" className="bg-blue-600 text-white py-2 rounded">Cadastrar</button>
            </form>
        </div>
    )
}
