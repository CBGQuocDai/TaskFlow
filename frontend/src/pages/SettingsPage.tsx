import React, {useCallback, useEffect, useRef, useState} from "react";
import {User, Upload, Mail, Lock} from "lucide-react";
import AvatarConfirmModal from "../components/AvatarConfirmModal.tsx";
import EmailOtpModal from "../components/EmailOtpModal.tsx";
import {changeAvatar, changeEmail, changeInfo, changePassword, getInfo} from "../services/UserService.ts";
import {useNavigate} from "react-router-dom";
import type {Users} from "../types";
import axios from "axios";
import {HOST} from "../utils/constant.ts";

const SettingsPage: React.FC = () => {

    const [email, setEmail] = useState<string>('user@example.com');
    const [originalEmail, setOriginalEmail] = useState<string>('user@example.com'); // To track if email changed
    const [displayName, setDisplayName] = useState<string>('Quốc Đại Đình');
    const [originalDisplayName, setOriginalDisplayName] = useState<string>('Quốc Đại Đình'); // To track if name changed
    const [avatarUrl, setAvatarUrl] = useState<string>('https://placehold.co/150x150/EEEEEE/000000?text=Avatar');

    const [password, setPassword] = useState<string>('');
    const [newPassword, setNewPassword] = useState<string>('');
    const [confirmNewPassword, setConfirmNewPassword] = useState<string>('');
    const [tempAvatarFile, setTempAvatarFile] = useState<File | null>(null); // Store file object temporarily
    const [tempAvatarUrl, setTempAvatarUrl] = useState<string>(''); // Store data URL for preview
    const [message, setMessage] = useState<string>('');
    const navigate = useNavigate();
    const token = localStorage.getItem('token');
    // State for OTP verification modal
    const [isEmailOtpModalOpen, setIsEmailOtpModalOpen] = useState<boolean>(false);
    const [otpMessage, setOtpMessage] = useState<string>('');

    // State for Avatar confirmation modal
    const [isAvatarConfirmModalOpen, setIsAvatarConfirmModalOpen] = useState<boolean>(false);

    const fileInputRef = useRef<HTMLInputElement>(null); // Ref for the hidden file input

    const handleAvatarFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0]; // Optional chaining for files
        if (file) {
            setTempAvatarFile(file);
            const reader = new FileReader();
            reader.onloadend = () => {
                setTempAvatarUrl(reader.result as string);
                setIsAvatarConfirmModalOpen(true);
            };
            reader.readAsDataURL(file);
        } else {
            setTempAvatarFile(null);
            setTempAvatarUrl('');
        }
    };

    // New handler for updating display name
    const handleUpdateDisplayName = async (e: React.FormEvent) => {
        e.preventDefault();
        setMessage('');
        if (displayName !== originalDisplayName) {
            const resp = await changeInfo(token??'', displayName);
            if(resp.code ===1000) {
                setOriginalDisplayName(displayName);
                setMessage('Tên hiển thị đã được cập nhật!');
            }
            else {
                if(resp.code ===1001) {
                    alert("phiên đăng nhập của bạn đã kết thúc, vui lòng đăng nhập lại")
                    navigate('/login');
                    return
                }
            }
        } else {
            setMessage('Không có thay đổi nào về tên hiển thị.');
        }
    };


    const handleConfirmAvatarChange = async () => {
        if (tempAvatarFile) {
            const resp = await changeAvatar(token??'', tempAvatarFile);
            if(resp.code ===1000) {
                setAvatarUrl(tempAvatarUrl); // Update main avatar URL
                setMessage('Ảnh đại diện đã được cập nhật!');
                setTempAvatarFile(null); // Clear temporary file
                setTempAvatarUrl(''); // Clear temporary URL
            }
            else {
                if(resp.code ===1012) {
                    alert("file không hợp lệ")
                }
            }

        }
        setIsAvatarConfirmModalOpen(false); // Close modal
    };

    const handleSaveEmail =  (e: React.FormEvent) => {
        e.preventDefault();
        setMessage('');
        if (email !== originalEmail) {
            if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
                setMessage('Vui lòng nhập một địa chỉ email hợp lệ.');
                return;
            }
            handleSendOtp();
        } else {

            alert('Email không thay đổi.');
        }
    };

    const handleChangePassword =  async (e: React.FormEvent) => {
        e.preventDefault();
        setMessage('');
        if (newPassword !== confirmNewPassword) {
            alert('Mật khẩu mới và xác nhận mật khẩu không khớp.');
            return;
        }
        if (newPassword.length < 8) {
            alert('Mật khẩu mới phải có ít nhất 8 ký tự.');
            return;
        }
        // Logic thay đổi mật khẩu
        const resp = await changePassword(token??'',password, newPassword);
        if(resp.code === 1000){
            alert('Mật khẩu đã được thay đổi thành công!');
            setPassword('');
            setNewPassword('');
            setConfirmNewPassword('');
            return
        }
        if(resp.code ===1001) {
            alert("phiên đăng nhập của bạn đã kết thúc, vui lòng đăng nhập lại")
            navigate('/login');
            return
        }
        if(resp.code===1011) {
            alert("mật khẩu hiện tại không đúng")
            return
        }
        if(resp.code ===1009) {
            alert("mật khẩu phải có ít nhất 8 kí tự")
            return
        }

    };

    const handleSendOtp = async () => {
        setOtpMessage('Đang gửi OTP...');
        const resp = await changeEmail(token??'', email)
        if(resp.code ===1000) {
            setIsEmailOtpModalOpen(true);
            setOtpMessage('Đã gửi thành công Otp')
            return;
        }else{
            if(resp.code===1005) {
                alert("email đã được sử dụng");
                setIsEmailOtpModalOpen(false);
                return;
            }
        }

    };

    const handleVerifyOtp =  async (otp: string) => {
        const resp = await axios.post(`${HOST}/auth/verifyOtp`,{
                email : email,
                otp: otp,
                actionType: "change_email"
            }, {
            headers: {
                "Authorization": `Bearer ${token}`,
            }
        })
        if(resp.data.code === 1000) {
            setOriginalEmail(email)
            localStorage.setItem("token", resp.data.data.token)
            alert("cập nhật email thành công`")
            setIsEmailOtpModalOpen(false);
        }
    };

    const getInformation = useCallback( async () => {
        if(token == null){
            navigate('/login');
            return;
        }
        const resp = await getInfo(token);
        if(resp.code === 1001){
            navigate('/login');
            return;
        }
        const u = resp.data as Users;
        setEmail(u.email);
        setOriginalEmail(u.email);
        setDisplayName(u.name);
        setOriginalDisplayName(u.name)
        setAvatarUrl(u.avatarUrl);
        console.log(u);
    },[token,navigate,setEmail,setDisplayName,setAvatarUrl]);
    useEffect(() => {
        getInformation()
    }, [getInformation]);
    return (
        <div className="p-8 bg-white text-gray-900 min-h-screen rounded-l-xl">
            <h1 className="text-4xl font-extrabold mb-8 text-blue-700">Cài đặt Tài khoản</h1>

            {message && (
                <div className="bg-blue-100 text-blue-800 p-4 rounded-lg mb-6 shadow-md" role="alert">
                    {message}
                </div>
            )}

            <div className="grid grid-cols-1 gap"> {/* Single column grid */}
                {/* Phần Ảnh đại diện */}
                <div className="flex justify-center items-center">
                    <div className="relative w-40 h-40 mb-4"> {/* Increased size to w-40 h-40 */}
                        <img src={tempAvatarUrl || avatarUrl} alt="Avatar"
                             className="w-full h-full rounded-full object-cover border-4 border-blue-500 shadow-lg"/>
                        <label htmlFor="avatar-upload"
                               className="absolute bottom-0 right-0 cursor-pointer bg-blue-500 text-white rounded-full p-2 shadow-md hover:bg-blue-600 transition-colors duration-200">
                            <Upload size={20}/>
                            <input
                                type="file"
                                id="avatar-upload"
                                accept="image/*"
                                onChange={handleAvatarFileChange}
                                className="hidden"
                                ref={fileInputRef} // Attach ref to hidden input
                            />
                        </label>
                    </div>
                </div>


                {/* Phần Thông tin cá nhân (Tên hiển thị) */}
                <div className="bg-gray-50 p-6 rounded-lg shadow-xl border border-gray-200">
                    <h2 className="text-2xl font-semibold mb-6 text-gray-900">Thông tin cá nhân</h2>
                    <form onSubmit={handleUpdateDisplayName} className="space-y-5">

                        {/* Tên hiển thị (Display Name) */}
                        <div>
                            <label htmlFor="display-name" className="block text-gray-700 text-sm font-medium mb-2">Tên
                                hiển thị</label>
                            <div className="relative">
                                <User className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-500"
                                      size={20}/>
                                <input
                                    type="text"
                                    id="display-name"
                                    value={displayName}
                                    onChange={(e: React.ChangeEvent<HTMLInputElement>) => setDisplayName(e.target.value)}
                                    className="w-full pl-10 pr-4 py-2 bg-white border border-gray-300 rounded-lg text-gray-900 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    required
                                />
                            </div>
                        </div>

                        <div className="flex justify-end space-x-3 pt-4">
                            <button
                                type="button"
                                onClick={() => setDisplayName(originalDisplayName)} // Simple cancel for display name
                                className="px-6 py-3 bg-gray-200 text-gray-800 rounded-lg shadow-md hover:bg-gray-300 transition-colors duration-200 font-semibold"
                            >
                                Hủy bỏ
                            </button>
                            <button
                                type="submit"
                                className="px-6 py-3 bg-blue-600 text-white rounded-lg shadow-md hover:bg-blue-700 transition-colors duration-200 font-semibold"
                            >
                                Cập nhật
                            </button>
                        </div>
                    </form>
                </div>

                {/* Thay đổi Email với OTP */}
                <div className="bg-gray-50 p-6 rounded-lg shadow-xl border border-gray-200">
                    <h2 className="text-2xl font-semibold mb-6 text-gray-900">Thay đổi Email</h2>
                    <form onSubmit={handleSaveEmail} className="space-y-5">
                        <div>
                            <label htmlFor="email-change" className="block text-gray-700 text-sm font-medium mb-2">Email hiện tại</label>
                            <div className="relative">
                                <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-500" size={20} />
                                <input
                                    type="email"
                                    id="email-change"
                                    value={email}
                                    onChange={(e: React.ChangeEvent<HTMLInputElement>) => setEmail(e.target.value)}
                                    className="w-full pl-10 pr-4 py-2 bg-white border border-gray-300 rounded-lg text-gray-900 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    required
                                />
                            </div>
                        </div>
                        <button
                            type="submit"
                            className="w-full px-6 py-3 bg-blue-600 text-white rounded-lg shadow-md hover:bg-blue-700 transition-colors duration-200 font-semibold"
                        >
                            Lưu Email
                        </button>
                    </form>
                </div>

                {/* Thay đổi mật khẩu */}
                <div className="bg-gray-50 p-6 rounded-lg shadow-xl border border-gray-200">
                    <h2 className="text-2xl font-semibold mb-6 text-gray-900">Thay đổi mật khẩu</h2>
                    <form onSubmit={handleChangePassword} className="space-y-5">
                        <div>
                            <label htmlFor="current-password" className="block text-gray-700 text-sm font-medium mb-2">Mật khẩu hiện tại</label>
                            <div className="relative">
                                <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-500" size={20} />
                                <input
                                    type="password"
                                    id="current-password"
                                    value={password}
                                    onChange={(e: React.ChangeEvent<HTMLInputElement>) => setPassword(e.target.value)}
                                    className="w-full pl-10 pr-4 py-2 bg-white border border-gray-300 rounded-lg text-gray-900 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    required
                                />
                            </div>
                        </div>
                        <div>
                            <label htmlFor="new-password" className="block text-gray-700 text-sm font-medium mb-2">Mật khẩu mới</label>
                            <div className="relative">
                                <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-500" size={20} />
                                <input
                                    type="password"
                                    id="new-password"
                                    value={newPassword}
                                    onChange={(e: React.ChangeEvent<HTMLInputElement>) => setNewPassword(e.target.value)}
                                    className="w-full pl-10 pr-4 py-2 bg-white border border-gray-300 rounded-lg text-gray-900 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    required
                                />
                            </div>
                        </div>
                        <div>
                            <label htmlFor="confirm-new-password" className="block text-gray-700 text-sm font-medium mb-2">Xác nhận mật khẩu mới</label>
                            <div className="relative">
                                <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-500" size={20} />
                                <input
                                    type="password"
                                    id="confirm-new-password"
                                    value={confirmNewPassword}
                                    onChange={(e: React.ChangeEvent<HTMLInputElement>) => setConfirmNewPassword(e.target.value)}
                                    className="w-full pl-10 pr-4 py-2 bg-white border border-gray-300 rounded-lg text-gray-900 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    required
                                />
                            </div>
                        </div>
                        <button
                            type="submit"
                            className="w-full px-6 py-3 bg-blue-600 text-white rounded-lg shadow-md hover:bg-blue-700 transition-colors duration-200 font-semibold"
                        >
                            Đổi mật khẩu
                        </button>
                    </form>
                </div>
            </div>

            <EmailOtpModal
                isOpen={isEmailOtpModalOpen}
                onClose={() => setIsEmailOtpModalOpen(false)}
                onVerifyOtp={handleVerifyOtp}
                onResendOtp={handleSendOtp}
                emailChangeMessage={otpMessage}
                setEmailChangeMessage={setOtpMessage}
            />

            <AvatarConfirmModal
                isOpen={isAvatarConfirmModalOpen}
                onClose={() => {
                    setIsAvatarConfirmModalOpen(false);
                    setTempAvatarFile(null); // Clear temporary file if modal closed without confirming
                    setTempAvatarUrl(''); // Clear temporary URL
                }}
                onConfirm={handleConfirmAvatarChange}
                newAvatarUrl={tempAvatarUrl}
            />
        </div>
    );
};
export default SettingsPage;