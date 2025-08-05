import {Routes, Route, useLocation} from 'react-router-dom';
import './App.css'
import LoginPage from "./pages/LoginPage.tsx";
import SignUpPage from "./pages/SignUpPage.tsx";
import ForgotPasswordPage from "./pages/ForgotPasswordPage.tsx";
import VerifyOtpPage from "./pages/VerifyOtpPage.tsx";
import ResetPasswordPage from "./pages/ResetPasswordPage.tsx";
import { useState} from "react";
import type {Task} from "./types/task.ts";
import ManageTaskPage from "./pages/ManageTaskPage.tsx";
import StatisticsPage from "./pages/StatisticsPage.tsx";
import SettingsPage from "./pages/SettingsPage.tsx";
import Sidebar from "./components/Sidebar.tsx";

function App() {
    const [todos, setTodos] = useState<Task[]>([]);
    const [originalTodos, setOriginalTodos] = useState<Task[]>([]);
    const noSidebarPaths = ['/tasks','/','/settings'];

    const location = useLocation();
    const shouldShowSidebar = noSidebarPaths.includes(location.pathname);
  return (
    <>
            {shouldShowSidebar && (
                <div className="fixed inset-y-0 left-0 w-64 z-50">
                    <Sidebar />
                </div>
            )}
            <div className={`flex-grow h-screen overflow-y-auto ${shouldShowSidebar ? 'ml-64' : ''}`}>
                <Routes>
                {/*page auth*/}
                <Route path="/login" element={<LoginPage />} />
                <Route path="/signUp" element={<SignUpPage />} />
                <Route path="/forgotPassword" element={<ForgotPasswordPage />} />
                <Route path="/verifyOtp" element={<VerifyOtpPage />} />
                <Route path="/resetPassword" element={<ResetPasswordPage />} />

                {/*page management*/}
                {/*<Route path="/" element={<ManageTaskPage todos={todos} setTodos={setTodos} originalTodos={originalTodos} setOriginalTodos={setOriginalTodos} />} />*/}
                <Route path="/tasks" element={<ManageTaskPage todos={todos} setTodos={setTodos} originalTodos={originalTodos} setOriginalTodos={setOriginalTodos} />} />
                <Route path="" element={<StatisticsPage />} />
                <Route path="/settings" element={<SettingsPage />} />
                {/*Not Found*/}
                <Route path="*" element={<div className="p-8 bg-white text-gray-900 min-h-screen rounded-l-xl text-center text-3xl font-bold">404 - Trang không tìm thấy</div>} />

            </Routes>
            </div>
    </>
  )
}

export default App
