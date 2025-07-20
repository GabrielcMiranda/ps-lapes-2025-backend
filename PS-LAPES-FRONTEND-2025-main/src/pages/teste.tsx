import { useState } from 'react';

export function Teste() {
  const [count, setCount] = useState(0);

  return (
    <div className="min-h-screen bg-gray-900 flex items-center justify-center text-white">
      <div className="text-center space-y-6 bg-gray-800 p-10 rounded-2xl shadow-2xl">
        <h1 className="text-4xl font-bold">Contador</h1>
        <div className="text-6xl font-mono">{count}</div>
        <div className="space-x-4">
          <button
            onClick={() => setCount(count - 1)}
            className="bg-red-500 hover:bg-red-600 px-6 py-2 rounded-full text-lg font-semibold transition"
          >
            - Diminuir
          </button>
          <button
            onClick={() => setCount(0)}
            className="bg-yellow-500 hover:bg-yellow-600 px-6 py-2 rounded-full text-lg font-semibold transition"
          >
            Resetar
          </button>
          <button
            onClick={() => setCount(count + 1)}
            className="bg-green-500 hover:bg-green-600 px-6 py-2 rounded-full text-lg font-semibold transition"
          >
            + Aumentar
          </button>
          <br />
          <button onClick={() => (window.location.href = "/")} className="bg-blue-500 hover:bg-blue-600 px-6 py-2 rounded-full text-lg font-semibold transition">
            Voltar
          </button>
        </div>
        </div>
      </div>
  );
}