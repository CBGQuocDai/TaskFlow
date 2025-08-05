import React from "react";
import {X} from "lucide-react";

interface AvatarConfirmModalProps {
    isOpen: boolean;
    onClose: () => void;
    onConfirm: () => void;
    newAvatarUrl: string;
}


const AvatarConfirmModal: React.FC<AvatarConfirmModalProps> = ({ isOpen, onClose, onConfirm, newAvatarUrl }) => {
    if (!isOpen) return null;

    return (
        <div className="fixed inset-0  flex items-center justify-center z-50 p-4"
             style={{ backgroundColor: 'rgba(17, 24, 39, 0.5)' }}>
            <div className="bg-white p-8 rounded-lg shadow-2xl w-full max-w-md transform transition-all duration-300 scale-100 opacity-100 relative text-center">
                <button
                    onClick={onClose}
                    className="absolute top-3 right-3 text-gray-400 hover:text-gray-600 transition-colors"
                    aria-label="Close modal"
                >
                    <X size={24} />
                </button>
                <h2 className="text-2xl font-bold text-gray-800 mb-6">Xác nhận thay đổi ảnh đại diện</h2>
                <p className="text-gray-700 mb-6">Bạn có chắc muốn thay đổi ảnh đại diện thành ảnh này?</p>
                <div className="mb-6 flex justify-center">
                    <img src={newAvatarUrl} alt="New Avatar Preview" className="w-32 h-32 rounded-full object-cover border-4 border-blue-500 shadow-lg" />
                </div>
                <div className="flex justify-center space-x-3 mt-6">
                    <button
                        type="button"
                        onClick={onClose}
                        className="px-5 py-2 bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300 transition-colors duration-200 font-semibold"
                    >
                        Hủy
                    </button>
                    <button
                        type="button"
                        onClick={onConfirm}
                        className="px-5 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors duration-200 font-semibold"
                    >
                        Xác nhận
                    </button>
                </div>
            </div>
        </div>
    );
};
export default AvatarConfirmModal;