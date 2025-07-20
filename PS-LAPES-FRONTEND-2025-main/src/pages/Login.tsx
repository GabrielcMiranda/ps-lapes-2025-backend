import { useRef, useState, useEffect, useContext } from "react";
import AuthContext, { AuthContextType } from "../context/AuthProvider";
import axios from '../services/api';
import { AxiosError } from "axios";

const LOGIN_URL = 'http://localhost:8080/auth/login';

const Login = () => {
  const { setAuth } = useContext(AuthContext) as AuthContextType;
  const userRef = useRef<HTMLInputElement>(null);
  const errRef = useRef<HTMLParagraphElement>(null);

  const [user, setUser] = useState('');
  const [pwd, setPwd] = useState('');
  const [errMsg, setErrMsg] = useState('');
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    userRef.current?.focus();
  }, []);

  useEffect(() => {
    setErrMsg('');
  }, [user, pwd]);

  interface SubmitEvent extends React.FormEvent<HTMLFormElement> { }

  const handleSubmit = async (e: SubmitEvent): Promise<void> => {
    e.preventDefault();

    try {
      const response = await axios.post(LOGIN_URL, JSON.stringify({ user, passWord: pwd }),
        {
          headers: { 'Content-Type': 'application/json' },
          withCredentials: true
        });
      console.log(JSON.stringify(response?.data));
      const accessToken = response?.data.accessToken;
      const roles = response?.data.roles;
      setAuth({ user, roles, accessToken });
      setUser('');
      setPwd('');
      setSuccess(true);
    } catch (err: unknown) {
      const error = err as AxiosError;
      if (!error.response) {
        setErrMsg('Sem resposta do servidor');
      } else if (error.response.status === 400) {
        setErrMsg('Nome de usuário ou senha inválidos');
      } else if (error.response.status === 401) {
        setErrMsg('Não autorizado');
      } else {
        setErrMsg('Login falhou');
      }
      errRef.current?.focus();
    }
  };

  return (
    <>
      {success ? (
        <section>
          <h1>Login bem-sucedido!</h1>
          <p>Você está logado!</p>
          <p>
            <a href="/home">Ir para Home</a>
          </p>
        </section>
      ) : (
        <section>
          <p ref={errRef} className={errMsg ? "errmsg" : "offscreen"} aria-live="assertive">
            {errMsg}
          </p>
          <h1>Login</h1>
          <form onSubmit={handleSubmit}>
            <label htmlFor="username">Nome de Usuário</label>
            <input
              type="text"
              id="username"
              ref={userRef}
              autoComplete="off"
              onChange={(e) => setUser(e.target.value)}
              value={user}
              required
            />

            <label htmlFor="password">Senha</label>
            <input
              type="password"
              id="password"
              autoComplete="off"
              onChange={(e) => setPwd(e.target.value)}
              value={pwd}
              required
            />

            <button type="submit">Entrar</button>
          </form>
          <p>
            Não tem uma conta? <a href="/register">Registrar</a>
          </p>
        </section>
      )}
    </>
  );
};

export default Login;