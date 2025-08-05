import React, {useEffect, useRef, useState} from "react";
import {Lock,X} from "lucide-react";
interface EmailOtpModalProps {
    isOpen: boolean;
    onClose: () => void;
    onVerifyOtp: (otp: string) => void;
    onResendOtp: () => void;
    emailChangeMessage: string;
    setEmailChangeMessage: React.Dispatch<React.SetStateAction<string>>;
}
const EmailOtpModal: React.FC<EmailOtpModalProps> = ({ isOpen, onClose, onVerifyOtp, onResendOtp, emailChangeMessage, setEmailChangeMessage }) => {
    const [otp, setOtp] = useState<string>('');
    const [timeLeft, setTimeLeft] = useState<number>(120); // 2 minutes in seconds

    const timerRef = useRef<NodeJS.Timeout | null>(null);

    useEffect(() => {
        if (isOpen) {
            setTimeLeft(120);
            setOtp('');
            setEmailChangeMessage('');
            timerRef.current = setInterval(() => {
                setTimeLeft((prevTime) => {
                    if (prevTime <= 1) {
                        clearInterval(timerRef.current as NodeJS.Timeout);
                        setEmailChangeMessage('Mã OTP đã hết hạn. Vui lòng gửi lại.');
                        return 0;
                    }
                    return prevTime - 1;
                });
            }, 1000);
        } else {
            clearInterval(timerRef.current as NodeJS.Timeout);
        }
        return () => clearInterval(timerRef.current as NodeJS.Timeout);
    }, [isOpen, setEmailChangeMessage]);

    const handleResend = () => {
        onResendOtp();
        setTimeLeft(120);
        setEmailChangeMessage('Mã OTP đã được gửi lại.');
    };

    const handleVerify = () => {
        if (timeLeft === 0) {
            setEmailChangeMessage('Mã OTP đã hết hạn. Vui lòng gửi lại.');
            return;
        }
        onVerifyOtp(otp);
    };

    if (!isOpen) return null;

    const minutes = Math.floor(timeLeft / 60);
    const seconds = timeLeft % 60;

    return (
        <div className="fixed inset-0 flex items-center justify-center z-50 p-4"
             style={{ backgroundColor: 'rgba(17, 24, 39, 0.5)' }}>
            <div className="bg-white p-8 rounded-lg shadow-2xl w-full max-w-md transform transition-all duration-300 scale-100 opacity-100 relative">
                <button
                    onClick={onClose}
                    className="absolute top-3 right-3 text-gray-400 hover:text-gray-600 transition-colors"
                    aria-label="Close modal"
                >
                    <X size={24} />
                </button>
                <h2 className="text-2xl font-bold text-gray-800 mb-6">Xác thực Email bằng OTP</h2>

                {emailChangeMessage && (
                    <div className={`p-3 rounded-lg mb-4 text-sm ${emailChangeMessage.includes('hết hạn') || emailChangeMessage.includes('sai') ? 'bg-red-100 text-red-700' : 'bg-blue-100 text-blue-700'}`}>
                        {emailChangeMessage}
                    </div>
                )}

                <div className="space-y-4">
                    <div>
                        <label htmlFor="otp-input" className="block text-gray-700 text-sm font-medium mb-2">Nhập mã OTP (6 chữ số)</label>
                        <div className="relative">
                            <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-500" size={20} />
                            <input
                                type="text"
                                id="otp-input"
                                value={otp}
                                onChange={(e: React.ChangeEvent<HTMLInputElement>) => setOtp(e.target.value)}
                                maxLength={6}
                                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-800 text-center tracking-widest"
                                placeholder="------"
                                required
                            />
                        </div>
                    </div>

                    <div className="text-center text-gray-600 text-sm">
                        Mã OTP sẽ hết hạn sau: <span className="font-semibold text-blue-600">{minutes.toString().padStart(2, '0')}:{seconds.toString().padStart(2, '0')}</span>
                    </div>

                    <div className="flex justify-end space-x-3 mt-6">
                        <button
                            type="button"
                            onClick={onClose}
                            className="px-5 py-2 bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300 transition-colors duration-200 font-semibold"
                        >
                            Hủy
                        </button>
                        <button
                            type="button"
                            onClick={handleResend}
                            className="px-5 py-2 bg-yellow-500 text-white rounded-lg hover:bg-yellow-600 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200 font-semibold"
                        >
                            Gửi lại OTP
                        </button>
                        <button
                            type="button"
                            onClick={handleVerify}
                            className="px-5 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors duration-200 font-semibold"
                        >
                            Xác thực
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};
export default EmailOtpModal;