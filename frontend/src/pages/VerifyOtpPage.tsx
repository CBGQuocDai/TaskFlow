import React, {useEffect, useState} from 'react';
import {Link, useNavigate, useLocation} from 'react-router-dom';
import {resendEmail, verifyOtp} from "../services/AuthService.ts";
import type {ApiResponse} from "../types";
import type {LoginResponse} from "../types/auth.ts";
interface OtpInputProps {
    value: string; // Giá trị hiện tại của ô input
    index: number; // Chỉ số của ô input trong mảng OTP
    onKeyDown: (e: React.KeyboardEvent<HTMLInputElement>, index: number) => void; // Xử lý sự kiện nhấn phím
    onChange: (e: React.ChangeEvent<HTMLInputElement>, index: number) => void; // Xử lý sự kiện thay đổi giá trị
    onPaste: (e: React.ClipboardEvent<HTMLInputElement>) => void; // Xử lý sự kiện dán
    inputRef: React.RefObject<HTMLInputElement|null>; // Ref để quản lý focus
}

const OtpInput = ({ value, index, onKeyDown, onChange, onPaste, inputRef } : OtpInputProps) => {
    return (
        <input
            ref={inputRef} // Gán ref cho input để có thể điều khiển focus từ component cha
            type="text"
            maxLength={1} // Chỉ cho phép nhập 1 ký tự
            value={value}
            onChange={(e) => onChange(e, index)}
            onKeyDown={(e) => onKeyDown(e, index)}
            onPaste={onPaste}
            className="w-12 h-12 text-center text-2xl font-semibold border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200 shadow-sm"
            inputMode="numeric" // Gợi ý bàn phím số trên thiết bị di động để cải thiện trải nghiệm người dùng
        />
    );
};
const VerifyOtpPage = () => {
    const [otp, setOtp] = React.useState<string[]>(new Array(6).fill('')); // State lưu trữ 6 chữ số của OTP
    const initialTimeInSeconds = 2 * 60; // 2 phút * 60 giây/phút
    const [timeLeft, setTimeLeft] = useState(initialTimeInSeconds);
    const [isRunning, setIsRunning] = useState(true);
    // Mảng các ref cho từng ô input để có thể điều khiển focus giữa các ô
    const inputRefs = React.useMemo(() => (
        Array.from({ length: 6 }, () => React.createRef<HTMLInputElement>())
    ), []);
    // const inputRefs = React.useRef<Array<React.RefObject<HTMLInputElement>>>(
    //     Array(6).fill(0).map((_, i) => React.createRef<HTMLInputElement>())
    // );

    const navigate = useNavigate(); // Hook từ react-router-dom để điều hướng giữa các trang
    const location = useLocation();
    const email: string = (location.state as { email?: string })?.email || 'email của bạn';
    const target: string = (location.state as { target?: string })?.target || 'email của bạn';
    const actionType: string = (location.state as { actionType?: string })?.actionType || 'email của bạn';
    // Xử lý sự kiện thay đổi giá trị của từng ô input OTP
    const handleChange = (e: React.ChangeEvent<HTMLInputElement>, index: number) => {
        const { value } = e.target;
        // Đảm bảo chỉ cho phép nhập số (0-9)
        if (/[^0-9]/.test(value) && value !== '') {
            return;
        }

        const newOtp = [...otp]; // Tạo bản sao của mảng OTP hiện tại
        newOtp[index] = value; // Cập nhật giá trị của ô input tại vị trí index
        setOtp(newOtp); // Cập nhật state OTP

        // Tự động chuyển focus sang ô tiếp theo nếu ô hiện tại có giá trị và không phải là ô cuối cùng
        if (value && index < 5) {
            inputRefs[index + 1].current?.focus();
        }
    };

    // Xử lý sự kiện nhấn phím (đặc biệt là Backspace và phím mũi tên)
    const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>, index: number) => {
        if (e.key === 'Backspace' && !otp[index] && index > 0) {
            // Nếu ô hiện tại trống và người dùng nhấn Backspace, chuyển focus về ô trước đó
            inputRefs[index - 1].current?.focus();
            // Đồng thời xóa giá trị của ô trước đó để người dùng dễ dàng sửa
            const newOtp = [...otp];
            newOtp[index - 1] = '';
            setOtp(newOtp);
        } else if (e.key === 'ArrowRight' && index < 5) {
            // Di chuyển focus sang phải bằng phím mũi tên phải
            inputRefs[index + 1].current?.focus();
        } else if (e.key === 'ArrowLeft' && index > 0) {
            // Di chuyển focus sang trái bằng phím mũi tên trái
            inputRefs[index - 1].current?.focus();
        }
    };

    // Xử lý sự kiện dán (paste) mã OTP từ clipboard
    const handlePaste = (e: React.ClipboardEvent<HTMLInputElement>) => {
        e.preventDefault(); // Ngăn chặn hành vi dán mặc định của trình duyệt
        const pasteData = e.clipboardData.getData('text').trim(); // Lấy dữ liệu từ clipboard
        // Kiểm tra nếu dữ liệu dán là 6 chữ số
        if (pasteData.length === 6 && /^[0-9]+$/.test(pasteData)) {
            const newOtp = pasteData.split(''); // Chuyển chuỗi thành mảng các ký tự
            setOtp(newOtp); // Cập nhật state OTP với mã đã dán
            inputRefs[5].current?.focus();
        }
    };

    // Xử lý khi người dùng nhấn nút "Xác nhận"
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault(); // Ngăn chặn hành vi submit form mặc định
        const fullOtp = otp.join(''); // Nối các chữ số lại thành chuỗi OTP hoàn chỉnh
        if (fullOtp.length === 6) {
            console.log('Mã OTP đã gửi:', fullOtp);
            console.log(email,fullOtp,actionType,target)
            const resp = await verifyOtp({
                email : email,
                otp : fullOtp,
                actionType: actionType,
            }) as ApiResponse<LoginResponse>;
            if(resp.code ===1000) {
                localStorage.setItem('token', resp.data.token);
                navigate(target);
            }
        } else {
            alert('Vui lòng nhập đầy đủ 6 chữ số của mã OTP.');
        }
    };

    // Xử lý khi người dùng nhấn nút "Gửi lại email"
    const handleResendEmail = async () => {
        const resp = await resendEmail(email);
        if (resp.code ===1000) {
            setTimeLeft(initialTimeInSeconds);
            alert("otp đã được gửi lại");
        }
    };

    const formatTime = (seconds: number): string => {
        const minutes = Math.floor(seconds / 60);
        const remainingSeconds = seconds % 60;
        return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`;
    };

    useEffect(() => {
        let timer: NodeJS.Timeout;

        if ( timeLeft > 0) {
            timer = setInterval(() => {
                setTimeLeft((prevTime:  number) => prevTime - 1);
            }, 1000); // Cập nhật mỗi giây
        } else if (timeLeft === 0) {
            setIsRunning(false);
            // Bạn có thể thêm logic khi đếm ngược kết thúc ở đây, ví dụ: phát âm thanh, hiển thị thông báo
            console.log('Đếm ngược đã kết thúc!');
        }

        // Dọn dẹp timer khi component unmount hoặc khi isRunning thay đổi
        return () => clearInterval(timer);
    }, [isRunning, timeLeft]);

    return (
        <div className="min-h-screen bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center p-4 font-inter">
            <div className="bg-white p-8 rounded-xl shadow-2xl w-full max-w-md transform transition-all duration-300">
                <h2 className="text-3xl font-bold text-center text-gray-800 mb-8">Xác nhận OTP</h2>
                <p className="text-center text-gray-600 mb-8 text-lg">
                    Một mã xác minh gồm 6 chữ số đã được gửi đến email của bạn.
                </p>

                <form onSubmit={handleSubmit} className="space-y-6">
                    <div className="flex justify-center gap-3 md:gap-4">
                        {otp.map((digit, index) => (
                            <OtpInput
                                key={index}
                                index={index}
                                value={digit}
                                onChange={handleChange}
                                onKeyDown={handleKeyDown}
                                onPaste={handlePaste}
                                inputRef={inputRefs[index]} // Truyền ref cho từng input
                            />
                        ))}
                    </div>

                    <div className="text-center text-gray-600 text-sm">
                        Mã OTP sẽ hết hạn sau: <span
                        className="font-semibold text-blue-600">{formatTime(timeLeft)}</span>
                    </div>

                    <button
                        type="submit"
                        className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition duration-150 ease-in-out transform hover:scale-105"
                    >
                        Xác nhận
                    </button>
                </form>

                <div className="mt-6 text-center text-sm space-y-3">
                    <button
                        onClick={handleResendEmail}
                        className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-blue-600 bg-blue-50 hover:bg-blue-100 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition duration-150 ease-in-out"
                    >
                        Gửi lại email
                    </button>
                    <Link
                        to={actionType==="register"? "/login" : "/forgotPassword"}
                        className="font-medium text-blue-600 hover:text-blue-500 transition duration-150 ease-in-out block mt-3"
                    >
                        Quay lại
                    </Link>
                </div>
            </div>
        </div>
    );
};

export default VerifyOtpPage;