import React from "react";
import {LogOut, Settings, BarChart2, ClipboardList} from "lucide-react";
import {Link, useLocation, useNavigate} from "react-router-dom";
import {logout} from "../services/AuthService.ts";

const Sidebar: React.FC = () => {
    const location = useLocation();
    const navigate = useNavigate();

    const commonButtonClasses = "flex items-center w-full px-4 py-3 text-gray-700 hover:bg-gray-200 transition-colors duration-200 rounded-lg";
    const activeButtonClasses = "bg-blue-100 text-blue-700 font-semibold";

    const handleLogout= async ()=>  {
        const token = localStorage.getItem("token")?? '';
        await logout(token)
        navigate('/login')
    }

    return (
        <div className="flex flex-col h-screen bg-white shadow-lg p-6 rounded-r-xl border-r border-gray-200">
            <div className="mb-10 text-2xl font-bold text-gray-800">
                TaskFlow
            </div>
            <nav className="flex-grow space-y-3">
                <Link
                    to="/"
                    className={`${commonButtonClasses} ${location.pathname === '/' ? activeButtonClasses : ''}`}
                >
                    <BarChart2 className="mr-3 h-5 w-5" />
                    Thống kê
                </Link>
                <Link
                    to="/tasks" // Changed to /dashboard as the primary task list
                    className={`${commonButtonClasses} ${location.pathname === '/tasks' ? activeButtonClasses : ''}`}
                >
                    <ClipboardList className="mr-3 h-5 w-5" />
                    Quản lý công việc
                </Link>
                <Link
                    to="/settings"
                    className={`${commonButtonClasses} ${location.pathname === '/settings' ? activeButtonClasses : ''}`}
                >
                    <Settings className="mr-3 h-5 w-5" />
                    Cài đặt
                </Link>
            </nav>
            <div className="mt-auto pt-4 border-t border-gray-200">
                <button
                    className="flex items-center w-full px-4 py-3 text-red-600 hover:bg-red-100 transition-colors duration-200 rounded-lg"
                    onClick={handleLogout}
                >
                    <LogOut className="mr-3 h-5 w-5" />
                    Đăng xuất
                </button>
            </div>
        </div>
    );
};

export default Sidebar;