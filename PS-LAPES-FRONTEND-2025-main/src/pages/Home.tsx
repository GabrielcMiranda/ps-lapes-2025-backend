import React from 'react';

export function Home() {
    return (
        <div>
            <h1>Home</h1>
            <p>aperte o botao</p>
            <button onClick={() => (window.location.href = "/teste")}>Teste</button>
            <button onClick={() => (window.location.href = "/login")}>Login</button>
            <button onClick={() => (window.location.href = "/registro")}>Registrar</button>
        </div>
    );
}
export default Home;