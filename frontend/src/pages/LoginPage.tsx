import React, {useState} from 'react';
import {Mail, Lock} from 'lucide-react';
import {Link, useNavigate} from 'react-router-dom';
import {useGoogleLogin} from '@react-oauth/google';
import {login, loginWithGoogle} from '../services/AuthService.ts'
import type {ApiResponse} from "../types";
import type {LoginResponse} from "../types/auth.ts";

const LoginPage: React.FC = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();
    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const response = await login({email, password}) as ApiResponse<LoginResponse>;
            if(response.code === 1000){
                localStorage.setItem("token", response.data.token);
            }
        } catch (e: any) {
            console.log(e.response.data);
            if(e.response.data.code === 1006) {
                alert("email hoặc mật khẩu không chính xác");
            }
        }
        navigate("/");
    }
    const googleLogin = useGoogleLogin({
        onSuccess: async (response) => {
            console.log(response.code);
            const resp = await loginWithGoogle(response.code) as ApiResponse<LoginResponse>;
            if(resp.code === 1000){
                localStorage.setItem("token", resp.data.token);
            }
            navigate("/");
        },
        onError: (errorResponse) => {
            console.log("Lỗi đăng nhập Google:", errorResponse);
            alert("Đã xãy ra lỗi")
        },
        flow:'auth-code'
    });
    const handleGoogleLogin = async () => {
        googleLogin()
    }
    return (
        <div
            className="min-h-screen bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center p-4 font-inter">
            <div className="bg-white p-8 rounded-xl shadow-2xl w-full max-w-md transform transition-all duration-300">
                <h2 className="text-3xl font-bold text-center text-gray-800 mb-8">Đăng Nhập</h2>
                <form onSubmit={handleLogin} className="space-y-6">
                    <div>
                        <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-2">
                            Email
                        </label>
                        <div className="relative rounded-md shadow-sm">
                            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                <Mail className="h-5 w-5 text-gray-400"/>
                            </div>
                            <input
                                type="email"
                                id="email"
                                name="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md text-gray-900 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                                placeholder="your@example.com"
                                required
                            />
                        </div>
                    </div>

                    <div>
                        <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-2">
                            Mật khẩu
                        </label>
                        <div className="relative rounded-md shadow-sm">
                            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                <Lock className="h-5 w-5 text-gray-400"/>
                            </div>
                            <input
                                type="password"
                                id="password"
                                name="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md text-gray-900 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                                placeholder="••••••••"
                                required
                            />
                        </div>
                    </div>

                    <div className="flex items-center justify-between">
                        <div className="text-sm">
                            <Link to="/forgotPassword"
                                  className="font-medium text-blue-600 hover:text-blue-500 transition duration-150 ease-in-out">
                                Quên mật khẩu?
                            </Link>
                        </div>
                    </div>

                    <div>
                        <button
                            type="submit"
                            className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition duration-150 ease-in-out transform hover:scale-105"
                        >
                            Đăng Nhập
                        </button>
                    </div>
                </form>

                <div className="mt-6">
                    <div className="relative">
                        <div className="absolute inset-0 flex items-center">
                            <div className="w-full border-t border-gray-300"/>
                        </div>
                        <div className="relative flex justify-center text-sm">
                            <span className="px-2 bg-white text-gray-500">Hoặc</span>
                        </div>
                    </div>

                    <div className="mt-6">
                        <button
                            onClick={handleGoogleLogin}
                            className="w-full flex justify-center items-center py-2 px-4 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition duration-150 ease-in-out transform hover:scale-105"
                        >
                            <img src="/gg.png" alt="" className="w-5 h-5 mr-1"/>
                            Đăng nhập với Google
                        </button>
                    </div>
                </div>

                <div className="mt-6 text-center text-sm">
                    Bạn chưa có tài khoản?{' '}
                    <Link to="/signUp"
                          className="font-medium text-blue-600 hover:text-blue-500 transition duration-150 ease-in-out">
                        Đăng ký
                    </Link>
                </div>
            </div>
        </div>
    );
};

export default LoginPage;