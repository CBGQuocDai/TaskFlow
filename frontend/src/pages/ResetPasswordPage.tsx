import React, {useCallback, useEffect} from 'react';
import {Link , useNavigate} from "react-router-dom";
import {Lock} from "lucide-react";
import {resetPassword, verifyToken} from "../services/AuthService.ts";

const ResetPasswordPage: React.FC = () => {
    const [newPassword, setNewPassword] = React.useState<string>('');
    const [confirmPassword, setConfirmPassword] = React.useState<string>('');
    const navigate = useNavigate();

    const token= localStorage.getItem('token');

    const handleSavePassword =async  (e: React.FormEvent) => {
        e.preventDefault();
        if (newPassword === '' || confirmPassword === '') {
            alert('Vui lòng nhập đầy đủ mật khẩu mới và xác nhận mật khẩu.');
            return;
        }
        if (newPassword !== confirmPassword) {
            alert('Mật khẩu mới và xác nhận mật khẩu không khớp.');
            return;
        }
        if (newPassword.length <8) {
            alert('Mật khẩu phải có ít nhất 8 ký tự.');
            return;
        }
        if(token==null) {
            navigate('/login');
            return;
        }
        const resp = await resetPassword(token,newPassword);
        if(resp.code ===1000) {
            alert("đổi mật khẩu thành công")
            navigate('/');
            return;
        }
    };
    const validateToken = useCallback(async () => {
        if(token==null) return ;
        const resp = await verifyToken(token);
        if(resp.code === 1001)  navigate("/login");

    },[token,navigate]);
    useEffect(() => {
        validateToken()
    },[validateToken])
    return (
        <div className="min-h-screen bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center p-4 font-inter">
            <div className="bg-white p-8 rounded-xl shadow-2xl w-full max-w-md transform transition-all duration-300">
                <h2 className="text-3xl font-bold text-center text-gray-800 mb-8">Khôi phục Mật khẩu</h2>

                <form className="space-y-6" onSubmit={handleSavePassword}>
                    <div>
                        <label htmlFor="new-password" className="block text-sm font-medium text-gray-700 mb-2">
                            Mật khẩu mới
                        </label>
                        <div className="relative rounded-md shadow-sm">
                            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                <Lock className="h-5 w-5 text-gray-400" />
                            </div>
                            <input
                                type="password"
                                id="new-password"
                                name="newPassword"
                                value={newPassword}
                                onChange={(e) => setNewPassword(e.target.value)}
                                className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md text-gray-900 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                                placeholder="Nhập mật khẩu mới"
                                required
                            />
                        </div>
                    </div>

                    <div>
                        <label htmlFor="confirm-password" className="block text-sm font-medium text-gray-700 mb-2">
                            Nhập lại mật khẩu mới
                        </label>
                        <div className="relative rounded-md shadow-sm">
                            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                <Lock className="h-5 w-5 text-gray-400" />
                            </div>
                            <input
                                type="password"
                                id="confirm-password"
                                name="confirmPassword"
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                                className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md text-gray-900 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                                placeholder="Nhập lại mật khẩu mới"
                                required
                            />
                        </div>
                    </div>

                    <div>
                        <button
                            type="submit"
                            className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition duration-150 ease-in-out transform hover:scale-105"
                        >
                            Lưu Mật khẩu
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
export default ResetPasswordPage