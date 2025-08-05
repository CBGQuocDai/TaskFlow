import type { TaskStatic} from "../types/task.ts";
import React, {useCallback, useEffect, useState} from "react";
import { PieChart, Pie, Cell, ResponsiveContainer, BarChart, Bar, XAxis, YAxis, Tooltip, Legend } from 'recharts';
import {useNavigate} from "react-router-dom";
import axios from "axios";
import {HOST} from "../utils/constant.ts";

const StatisticsPage: React.FC = () => {
    const [totalTasks,setTotal] = useState<number>(0);
    const [completedTasks,setComplete] = useState<number>(0);
    const [inProgressTasks, setInProgressTasks] = useState<number>(0); // Đổi pending thành inProgressTasks
    const [overdueTasks, setOverdueTasks] = useState<number>(0); // Trạng thái mới cho task quá hạn
    const token = localStorage.getItem("token")??"";
    const [tasksDueCategoryData, setTasksDueCategoryData] = useState<any[]>([]);

    const navigate = useNavigate()
    // Dữ liệu cho biểu đồ tròn (Đã xong, Đang làm, Quá hạn)
    const pieChartData = [
        { name: 'Đã xong', value: completedTasks },
        { name: 'Đang làm', value: inProgressTasks },
        { name: 'Quá hạn', value: overdueTasks },
    ].filter(entry => entry.value > 0);

    // Màu sắc cho các phần của biểu đồ tròn: Xanh lá (Đã xong), Xanh dương (Đang làm), Đỏ (Quá hạn)
    const PIE_COLORS = ['#4CAF50', '#007bff', '#dc3545'];

    // Cập nhật tasksDueCategoryData và overdueTasks trong useEffect
    useEffect(() => {
        getStatic();
    }, []); // Chỉ chạy một lần khi component mount

    const getStatic = useCallback(async () => {
        try {
            const resp = await axios.get(`${HOST}/todos/stat`, {
                headers: {
                    "Authorization": `Bearer ${token}`
                }
            })
            const responseData = resp.data.data as TaskStatic;
            setTotal(responseData.totalTasks);
            setComplete(responseData.completedTasks);
            setOverdueTasks(responseData.overdueTasks);
            setInProgressTasks(responseData.inProgressTasks); // Cập nhật state inProgressTasks

            // Chuyển đổi dữ liệu dueSoonTasks từ API thành định dạng cho BarChart
            const newTasksDueCategoryData = [
                { name: 'Hôm nay', value: responseData.dueSoonToday },
                { name: 'Tuần này', value: responseData.dueSoonThisWeek },
                { name: 'Tuần sau', value: responseData.dueSoonNextWeek },
                { name: 'Sau 2 tuần', value: responseData.dueSoonAfterTwoWeeks },
            ]; // Lọc bỏ các mục có giá trị 0

            setTasksDueCategoryData(newTasksDueCategoryData);

        } catch (err: any) {
            // console.error(`Lỗi khi lấy thống kê mẫu: ${err.message || 'Không thể tải thống kê mẫu.'}`);
            // alert(`Lỗi khi lấy thống kê mẫu: ${err.message || 'Không thể tải thống kê mẫu.'}`);
            if(err.response.data.code === 1001) {
                navigate("/login")
            }
            setTotal(0);
            setComplete(0);
            setInProgressTasks(0);
            setOverdueTasks(0);
            setTasksDueCategoryData([]);
        }
    }, []);

    return (
        <div className="p-8 bg-white text-gray-900 min-h-screen rounded-l-xl">
            <h1 className="text-4xl font-extrabold mb-8 text-blue-700">Thống kê Công việc</h1>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-12">
                <div className="bg-blue-50 p-6 rounded-lg shadow-md border border-blue-200 text-center">
                    <h2 className="text-3xl font-bold text-blue-700">{totalTasks}</h2>
                    <p className="text-gray-600 mt-2">Tổng số công việc</p>
                </div>
                <div className="bg-green-50 p-6 rounded-lg shadow-md border border-green-200 text-center">
                    <h2 className="text-3xl font-bold text-green-700">{completedTasks}</h2>
                    <p className="text-gray-600 mt-2">Công việc đã hoàn thành</p>
                </div>
                <div className="bg-red-50 p-6 rounded-lg shadow-md border border-red-200 text-center">
                    <h2 className="text-3xl font-bold text-red-700">{overdueTasks}</h2>
                    <p className="text-gray-600 mt-2">Công việc quá hạn</p>
                </div>
                <div className="bg-yellow-50 p-6 rounded-lg shadow-md border border-yellow-200 text-center">
                    <h2 className="text-3xl font-bold text-yellow-700">{inProgressTasks}</h2>
                    <p className="text-gray-600 mt-2">Công việc đang làm</p>
                </div>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                <div className="bg-gray-50 p-6 rounded-lg shadow-xl border border-gray-200">
                    <h2 className="text-2xl font-semibold mb-4 text-gray-800">Tình trạng công việc</h2>
                    <ResponsiveContainer width="100%" height={300}>
                        <PieChart>
                            <Pie
                                data={pieChartData}
                                cx="50%"
                                cy="50%"
                                labelLine={false}
                                outerRadius={100}
                                fill="#8884d8"
                                dataKey="value"
                                // label={({ name, percent }: { name: string; percent: number }) => `${name}: ${(percent * 100).toFixed(0)}%`}
                            >
                                {pieChartData.map((entry  , index) => {
                                    console.log(entry)
                                    return (
                                        <Cell key={`cell-${index}`} fill={PIE_COLORS[index % PIE_COLORS.length]} />
                                    )

                                })}
                            </Pie>
                            <Tooltip />
                            <Legend />
                        </PieChart>
                    </ResponsiveContainer>
                </div>

                <div className="bg-gray-50 p-6 rounded-lg shadow-xl border border-gray-200">
                    <h2 className="text-2xl font-semibold mb-4 text-gray-800">Công việc sắp đến hạn</h2>
                    {tasksDueCategoryData.length > 0 ? ( // Sử dụng tasksDueCategoryData.length
                        <ResponsiveContainer width="100%" height={300}>
                            <BarChart
                                data={tasksDueCategoryData}
                                margin={{
                                    top: 5, right: 30, left: 20, bottom: 5,
                                }}
                            >
                                <XAxis dataKey="name" stroke="#6b7280" />
                                <YAxis allowDecimals={false} stroke="#6b7280" />
                                <Tooltip />
                                <Legend />
                                <Bar dataKey="value" name="Số công việc" fill="#8884d8" radius={[10, 10, 0, 0]} />
                            </BarChart>
                        </ResponsiveContainer>
                    ) : (
                        <p className="text-gray-600 text-center py-10">Không có công việc nào sắp đến hạn hoặc đang làm.</p>
                    )}
                </div>
            </div>
        </div>
    );
};

export default StatisticsPage;