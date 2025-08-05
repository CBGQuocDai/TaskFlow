import React, {useState} from 'react';
import {Link, useNavigate} from 'react-router-dom'
import {Mail} from 'lucide-react'
import {forgotPassword} from "../services/AuthService.ts";
const ForgotPasswordPage = () => {
    const [email, setEmail] = useState('');
    const navigate = useNavigate();
    const handleVerify =async (e:React.FormEvent) => {
        // console.log('Xác thực email quên mật khẩu:', { email });
        e.preventDefault()
        const response = await forgotPassword(email);
        if(response.code ===1000){
            navigate("/verifyOtp", {state: {
                    email : email,
                    target: "/resetPassword",
                    actionType: "reset_password",
                }})
        }
    };

    return (
        <div className="min-h-screen bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center p-4 font-inter">
            <div className="bg-white p-8 rounded-xl shadow-2xl w-full max-w-md transform transition-all duration-300">
                <h2 className="text-3xl font-bold text-center text-gray-800 mb-8">Quên Mật Khẩu</h2>

                <form onSubmit={handleVerify} className="space-y-6">
                    <div>
                        <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-2">
                            Email của bạn
                        </label>
                        <div className="relative rounded-md shadow-sm">
                            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                <Mail className="h-5 w-5 text-gray-400" />
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
                        <button
                            type="submit"
                            className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition duration-150 ease-in-out transform hover:scale-105"
                        >
                            Xác thực
                        </button>
                    </div>
                </form>

                <div className="mt-6 text-center text-sm">
                    <Link to="/login" className="font-medium text-blue-600 hover:text-blue-500 transition duration-150 ease-in-out">
                        Quay lại Đăng nhập
                    </Link>
                </div>
            </div>
        </div>
    );
};
export default ForgotPasswordPage;