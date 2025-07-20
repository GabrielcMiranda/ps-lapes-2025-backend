import React from 'react';
import { useRef, useState, useEffect } from 'react';
import { faCheck, faTimes, faInfoCircle } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Link } from 'react-router-dom';
import axios from '../services/api';

const USER_REGEX = /^[a-zA-Z][a-zA-Z0-9-_]{3,23}$/;
const PWD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%]).{8,24}$/;
const URL_REGISTRO = '/register';

const Registro = () => {
    const userRef = useRef<HTMLInputElement>(null);
    const errRef = useRef<HTMLParagraphElement>(null);

    const [user, setUser] = useState('');
    const [validName, setValidName] = useState(false);
    const [userFocus, setUserFocus] = useState(false);

    const [pwd, setPwd] = useState('');
    const [validPwd, setValidPwd] = useState(false);
    const [pwdFocus, setPwdFocus] = useState(false);

    const [matchPwd, setMatchPwd] = useState('');
    const [validMatch, setValidMatch] = useState(false);
    const [matchFocus, setMatchFocus] = useState(false);

    const [errMsg, setErrMsg] = useState('');
    const [success, setSuccess] = useState(false);

    useEffect(() => {
        userRef.current?.focus();
    }, []);

    useEffect(() => {
        const result = USER_REGEX.test(user);
        console.log(result);
        console.log(user);
        setValidName(result);
        // setValidName(USER_REGEX.test(user));
    }, [user]);

    useEffect(() => {
        const result = PWD_REGEX.test(pwd);
        console.log(result);
        console.log(pwd);
        setValidPwd(result);
        const match = pwd === matchPwd;
        setValidMatch(match);
        // setMatchPwd(pwd === matchPwd);
        // setValidMatch(PWD_REGEX.test(pwd) && match);
    }, [pwd, matchPwd]);

    useEffect(() => {
        setErrMsg('');
    }, [user, pwd, matchPwd]);

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const v1 = USER_REGEX.test(user);
        const v2 = PWD_REGEX.test(pwd);
        if (!v1 || !v2) {
            setErrMsg("Entrada inválida");
            return;
        }
        try {
            const response = await axios.post(URL_REGISTRO, JSON.stringify({ username: user, password: pwd }),
                {
                    headers: { 'Content-Type': 'application/json' },
                    withCredentials: true
                }
            );
            console.log(JSON.stringify(response));
            setSuccess(true);
            setUser('');
            setPwd('');
            setMatchPwd('');
            setValidName(false);
            setValidPwd(false);
            setValidMatch(false);
        } catch (err: any) {
            if (!err?.response) {
                setErrMsg('Sem resposta do servidor');
            } else if (err.response?.status === 409) {
                setErrMsg('Usuário já existe');
            } else {
                setErrMsg('Falha ao registrar');
            }
            errRef.current?.focus();
        }
    };

    return (
        <>
            {success ? (
                <section>
                    <h1>Registro realizado com sucesso!</h1>
                    <p>
                        <a href="/login">Faça login</a>
                    </p>
                </section>
            ) : (
                <section>
                    <p
                        ref={errRef}
                        className={`space-y-4 max-w-md mx-auto ${errMsg ? "errmsg" : "offscreen"}`}
                        aria-live="assertive"
                    >
                        {errMsg}
                    </p>
                    <h1>Registro</h1>
                    <form onSubmit={handleSubmit}>
                        <label htmlFor="username"> Nome de Usuário:
                            <span className={validName ? "valid" : "hide"}>
                                <FontAwesomeIcon icon={faCheck} />
                            </span>
                            <span className={validName || !user ? "hide" : "invalid"}>
                                <FontAwesomeIcon icon={faTimes} />
                            </span>
                        </label>
                        <input className="border border-gray-300 rounded px-4 py-2 w-full" placeholder="Nome"
                            type="text"
                            id="username"
                            ref={userRef}
                            autoComplete="off"
                            onChange={(e) => setUser(e.target.value)}
                            value={user}
                            required
                            aria-invalid={validName ? "false" : "true"}
                            aria-describedby="uidnote"
                            onFocus={() => setUserFocus(true)}
                            onBlur={() => setUserFocus(false)}
                        />
                        <p id="uidnote" className={userFocus && user && !validName ? "instructions" : "offscreen"}>
                            <FontAwesomeIcon icon={faInfoCircle} />
                            Deve conter 4 a 24 caracteres.<br />
                            Deve começar com uma letra. Permitidos: letras, números, traços e sublinhados.
                        </p>

                        <label htmlFor="password">Senha:
                            <span className={validPwd ? "valid" : "hide"}>
                                <FontAwesomeIcon icon={faCheck} />
                            </span>
                            <span className={validPwd || !pwd ? "hide" : "invalid"}>
                                <FontAwesomeIcon icon={faTimes} />
                            </span>
                        </label>
                        <input className="border border-gray-300 rounded px-4 py-2 w-full" placeholder="Senha"
                            type="password"
                            id="password"
                            onChange={(e) => setPwd(e.target.value)}
                            required
                            aria-invalid={validPwd ? "false" : "true"}
                            aria-describedby="pwdnote"
                            onFocus={() => setPwdFocus(true)}
                            onBlur={() => setPwdFocus(false)}
                        />
                        <p id="pwdnote" className={pwdFocus && !validPwd ? "instructions" : "offscreen"}>
                            <FontAwesomeIcon icon={faInfoCircle} />
                            Deve conter 8 a 24 caracteres.<br />
                            Deve conter letras maiúsculas e minúsculas, números e caracteres especiais.<br />
                            Caracteres Permitidos: <span aria-label="exclamation mark">!</span> <span aria-label="at symbol">@</span>
                            <span aria-label="hash">#</span> <span aria-label="dollar">$</span> <span aria-label="percent">%</span>.
                        </p>

                        <label htmlFor="confirm_pwd">Confirmar Senha:
                            <span className={validMatch && matchPwd ? "valid" : "hide"}>
                                <FontAwesomeIcon icon={faCheck} />
                            </span>
                            <span className={validMatch || !matchPwd ? "hide" : "invalid"}>
                                <FontAwesomeIcon icon={faTimes} />
                            </span>
                        </label>
                        <input className="border border-gray-300 rounded px-4 py-2 w-full" placeholder="Confirmar Senha"
                            type="password"
                            id="confirm_pwd"
                            onChange={(e) => setMatchPwd(e.target.value)}
                            required
                            aria-invalid={validMatch ? "false" : "true"}
                            aria-describedby="confirmnote"
                            onFocus={() => setMatchFocus(true)}
                            onBlur={() => setMatchFocus(false)}
                        />
                        <p id="confirmnote" className={matchFocus && !validMatch ? "instructions" : "offscreen"}>
                            <FontAwesomeIcon icon={faInfoCircle} />
                            A senha digitada deve ser igual à senha anterior.
                        </p>

                        <button className="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded shadow" disabled={!validName || !validPwd || !validMatch ? true : false}>Registrar</button>
                    </form>
                    <p>Já possui uma conta? <Link to="/login">Faça login</Link></p>
                </section>
            )}
        </>
    );
};
export default Registro;