import React, {useCallback, useEffect, useState} from "react";
import type {Task} from "../types/task.ts";
import {ChevronDown, Edit, Plus, RotateCcw, SortAsc, Trash2} from "lucide-react";
import AddTaskModal from "../components/AddTaskModal.tsx";
import axios from "axios";
import {HOST} from "../utils/constant.ts";
import {useNavigate} from "react-router-dom";


interface TaskManagementProps { // Renamed from TaskManagementProps
    todos: Task[];
    setTodos: React.Dispatch<React.SetStateAction<Task[]>>;
    originalTodos: Task[];
    setOriginalTodos: React.Dispatch<React.SetStateAction<Task[]>>;
}

const ManageTaskPage: React.FC<TaskManagementProps> = ({ todos, setTodos, originalTodos, setOriginalTodos }) => {
    const [currentPage, setCurrentPage] = useState<number>(1);
    const [totalPages, setTotalPages] = useState<number>(1);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);
    const [isAddTaskModalOpen, setIsAddTaskModalOpen] = useState<boolean>(false);
    const [editingTask, setEditingTask] = useState<Task | null>(null); // State for editing task
    const [orderBy,setOrderBy] = useState<string>("id"); // State for sort status
    const [filterStatus, setFilterStatus] = useState<string>('all'); // New state for filter status: 'all', 'completed', 'pending'
    const limit = 10; // Mặc định limit là 10
    const navigate = useNavigate();
    const token =  localStorage.getItem('token');

    type TaskStatus = 'over' | 'done' | 'process';

// Hàm trợ giúp để dẫn xuất trạng thái
    const getTaskStatus = (task: Task): TaskStatus => {
        if (task.completed) {
            return 'done';
        }
        const today = new Date();
        today.setHours(0, 0, 0, 0); // Chuẩn hóa về đầu ngày


        const dueDate = new Date(task.dueDate); // Tháng là 0-indexed trong đối tượng Date
        dueDate.setHours(0, 0, 0, 0);

        if (dueDate.getTime() < today.getTime()) {
            return 'over';
        }
        return 'process';
    };

    const fetchTodos = useCallback(async (page: number, statusFilter: string, orderBy: string) => {
        setLoading(true);
        setError(null);
        try {
            if(token ==null) {
                navigate("/login")
            }
            const resp =await axios.get(
                `${HOST}/todos?page=${page}&limit=10&complete=${statusFilter}&orderBy=${orderBy}`,{
                headers: {
                    "Authorization": `Bearer ${token}`
                }
            })
            console.log(resp.data)
            setTodos(resp.data.data);
            setTotalPages(Math.ceil(resp.data.total/ limit)); // Total pages based on filtered data
        } catch (err: any) {
            if(err.response.data.code ===1001) {
                navigate("/login")
            }
            setError('Không thể tải công việc. Vui lòng thử lại.');
            console.error('Lỗi khi tải công việc:', err);
        } finally {
            setLoading(false);
        }
    }, [setTodos, navigate]);

    useEffect(() => {
        fetchTodos(currentPage, filterStatus,orderBy);
    }, [currentPage, fetchTodos, filterStatus,orderBy]); // Re-fetch when filterStatus changes

    const handlePageChange = (newPage: number) => {
        if (newPage > 0 && newPage <= totalPages) {
            setCurrentPage(newPage);
        }
    };

    const handleSaveTask = async (savedTask: Task) => {
        if (editingTask) {
            // Edit existing task
            try {
                const [day,month,year] = savedTask.dueDate.split("/");
                savedTask.dueDate = `${year}-${month}-${day}`
                const resp = await axios.put(`${HOST}/todos/${savedTask.id}`, {
                    title: savedTask.title,
                    description: savedTask.description,
                    dueDate: new Date(savedTask.dueDate),
                }, {
                    headers: {
                        "Authorization": `Bearer ${token}`
                    }
                })
                if(resp.data.code===1000) {
                    const updatedTodos = todos.map(todo =>
                        todo.id === savedTask.id ? savedTask : todo
                    );
                    const updatedOriginalTodos = originalTodos.map(todo =>
                        todo.id === savedTask.id ? savedTask : todo
                    );

                    setTodos(updatedTodos);
                    setOriginalTodos(updatedOriginalTodos);
                    alert('Công việc đã được cập nhật!');
                }
            }
            catch (e: any){
                console.error(e)
                if(e.response.data.code ===1001) {
                    navigate("/login")
                }else {

                    alert(e.response.data.code.message)
                }
            }
        } else {
            // Add new task
            try {
                const [day,month,year] = savedTask.dueDate.split("/");
                const resp = await axios.post(`${HOST}/todos`, {
                    title: savedTask.title,
                    description: savedTask.description,
                    dueDate: new Date(`${year}-${month}-${day}`),
                }, {
                    headers: {
                        "Authorization": `Bearer ${token}`
                    }
                })
                if(resp.data.code===1000) {
                    savedTask.dueDate = `${year}-${month}-${day}`
                    const newTodos = [savedTask, ...todos];
                    const newOriginalTodos = [savedTask, ...originalTodos];
                    setTodos(newTodos);
                    setOriginalTodos(newOriginalTodos);
                    alert('Công việc đã được thêm!');
                }
            }
            catch (e: any){
                console.error(e)
                if(e.response.data.code ===1001) {
                    navigate("/login")
                }else {

                    alert(e.response.data.code.message)
                }
            }

        }
        setEditingTask(null); // Clear editing task state
        setIsAddTaskModalOpen(false); // Close modal
        setCurrentPage(1); // Reset to first page after add/edit to see changes easily
    };

    const handleEditTask = (task: Task) => {
        setEditingTask(task);
        setIsAddTaskModalOpen(true);
    };

    const handleToggleSortByDeadline = () => {
        if (orderBy=='deadline') {
            setOrderBy("id")
            // alert('Thứ tự công việc đã được đặt lại!');
        } else {
            setOrderBy("deadline")
            // alert('Công việc đã được sắp xếp theo hạn chót!');
        }
    };

    const handleDeleteTask = async (id: number) => {
        console.log(id)
        if (window.confirm('Bạn có chắc chắn muốn xóa công việc này?')) {
            console.log("heheheh");
            try {
                const resp= await axios.delete(`${HOST}/todos/${id}`, {
                    headers: {
                        "Authorization": `Bearer ${token}`
                    }
                })
                console.log(resp)
                if (resp.data.code ===1000) {
                    const updatedTodos = todos.filter(todo => todo.id !== id);
                    const updatedOriginalTodos = originalTodos.filter(todo => todo.id !== id);
                    setTodos(updatedTodos);
                    setOriginalTodos(updatedOriginalTodos);
                    alert('Công việc đã được xóa!');
                    // setCurrentPage(1); // Reset to first page after deletion
                }
            } catch (e: any) {
                console.log(e.response.data)
                if(e.response.data.code ==1001) {
                    navigate('/login')
                }
            }



        }
    };

    const handleToggleComplete = async (id: number) => {
        try {
            const resp = await axios.put(`${HOST}/todos/changeCompleted/${id}`,{}, {
                headers: {
                    "Authorization": `Bearer ${token}`
                }
            })
            if(resp.data.code===1000) {
                const updatedTodos = todos.map(todo =>
                    todo.id === id ? { ...todo, completed: !todo.completed } : todo
                );
                const updatedOriginalTodos = originalTodos.map(todo =>
                    todo.id === id ? { ...todo, completed: !todo.completed } : todo
                );
                setTodos(updatedTodos);
                setOriginalTodos(updatedOriginalTodos);
            }
        }
        catch (e: any){
            console.error(e)
            if(e.response.data.code ===1001) {
                navigate("/login")
            }else {
                alert(e.response.data.code.message)
            }
        }

        //
        // // If current filter is 'completed' or 'pending', re-fetch to update list based on status change
        if (filterStatus !== 'all') {
            fetchTodos(currentPage, filterStatus,orderBy);
        }
    };

    function formatDateToString(date: Date): string {

        const day = String(date.getDate()).padStart(2, '0'); // Lấy ngày và thêm '0' vào trước nếu cần
        const month = String(date.getMonth() + 1).padStart(2, '0'); // Lấy tháng (0-11) và thêm '0' vào trước nếu cần
        const year = date.getFullYear(); // Lấy năm

        return `${day}-${month}-${year}`;
    }
    return (
        <div className="p-8 bg-white text-gray-900 min-h-screen rounded-l-xl">
            <h1 className="text-4xl font-extrabold mb-8 text-blue-700">Quản lý Công việc</h1>

            <div className="flex justify-between items-center mb-6 flex-wrap gap-4">
                <button
                    onClick={() => {
                        setIsAddTaskModalOpen(true);
                        setEditingTask(null);
                    }} // Xóa editingTask khi thêm mới
                    className="px-6 py-3 bg-green-600 text-white rounded-lg shadow-md hover:bg-green-700 transition-colors duration-200 font-semibold flex items-center"
                >
                    <Plus className="mr-2 h-5 w-5"/> Thêm Task
                </button>
                <div className="flex items-center space-x-4">
                    {/* Dropdown lọc */}
                    <div className="relative">
                        <select
                            value={filterStatus}
                            onChange={(e: React.ChangeEvent<HTMLSelectElement>) => {
                                setFilterStatus(e.target.value as TaskStatus | 'all');
                                setCurrentPage(1); // Đặt lại về trang đầu tiên khi bộ lọc thay đổi
                                setOrderBy("id")// Đặt lại sắp xếp khi bộ lọc thay đổi
                            }}
                            className="appearance-none bg-white border border-gray-300 text-gray-900 py-2 pl-3 pr-8 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                        >
                            <option value="all">Tất cả</option>
                            <option value="done">Đã xong</option>
                            <option value="process">Đang làm</option>
                            <option value="over">Quá hạn</option>
                        </select>
                        <ChevronDown
                            className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-500 pointer-events-none"
                            size={20}/>
                    </div>

                    {/* Nút sắp xếp */}
                    <button
                        onClick={handleToggleSortByDeadline}
                        className={`px-6 py-3 rounded-lg shadow-md transition-colors duration-200 font-semibold flex items-center
              ${orderBy === 'deadline' ? 'bg-indigo-700 hover:bg-indigo-800 text-white' : 'bg-indigo-600 hover:bg-indigo-700 text-white'}`}
                    >
                        {orderBy === 'deadline' ? <RotateCcw className="mr-2 h-5 w-5"/> :
                            <SortAsc className="mr-2 h-5 w-5"/>}
                        {orderBy === 'deadline' ? 'Hủy sắp xếp' : 'Sắp xếp theo Deadline'}
                    </button>
                </div>
            </div>

            {loading && <p className="text-blue-600">Đang tải công việc...</p>}
            {error && <p className="text-red-600">{error}</p>}

            {!loading && !error && todos.length > 0 && (
                <div className="bg-gray-50 p-6 rounded-lg shadow-xl border border-gray-200">
                    <ul className="space-y-4">
                        {todos.map((task: Task) => { // Đổi tên todo thành task
                            const status = getTaskStatus(task); // Dẫn xuất trạng thái để hiển thị
                            let itemBgClass = 'bg-gray-100'; // Default class for hover in process
                            let titleClass = 'text-gray-900';

                            if (status === 'done') {
                                itemBgClass = 'bg-green-100';
                                titleClass = 'line-through text-gray-500';
                            } else if (status === 'over') {
                                itemBgClass = 'bg-red-100'; // Đặt nền đỏ nhạt cho quá hạn
                                titleClass = 'text-red-800 font-semibold';
                            } else if (status === 'process') {
                                itemBgClass = 'bg-blue-50'; // Đặt nền xanh dương nhạt cho đang làm
                                titleClass = 'text-blue-800'; // Đặt màu chữ xanh dương đậm
                            }
                            // Hover effects
                            const hoverClass = status === 'done' ? 'hover:bg-green-200' :
                                status === 'over' ? 'hover:bg-red-200' :
                                    'hover:bg-blue-100'; // Hover cho đang làm

                            return (
                                <li
                                    key={task.id}
                                    className={`flex items-start justify-between p-4 rounded-md transition-colors duration-200 ${itemBgClass} ${hoverClass}`}
                                >
                                    {/* Checkbox để bật/tắt trạng thái */}
                                    {status !== 'done' ? ( // Chỉ hiển thị checkbox cho "đang làm" hoặc "quá hạn" để đánh dấu là đã xong
                                        <input
                                            type="checkbox"
                                            checked={false} // Luôn là false, vì việc chọn có nghĩa là đặt thành 'done'
                                            onChange={() => handleToggleComplete(task.id)}
                                            className="form-checkbox h-5 w-5 text-blue-500 rounded border-gray-400 focus:ring-blue-400 mr-3 mt-1"
                                            title="Đánh dấu hoàn thành"
                                        />
                                    ) : (
                                        <input
                                            type="checkbox"
                                            checked={true} // Đã xong
                                            onChange={() => handleToggleComplete(task.id)} // Cho phép bỏ đánh dấu đã xong
                                            className="form-checkbox h-5 w-5 text-blue-500 rounded border-gray-400 focus:ring-blue-400 mr-3 mt-1"
                                            title="Đã hoàn thành"
                                        />
                                    )}

                                    {/* Tiêu đề, hạn chót và mô tả ở giữa */}
                                    <div className="flex-1">
                                        <h3 className={`text-lg font-semibold ${titleClass}`}>
                                            {task.title}
                                        </h3>
                                        {task.description && ( // Hiển thị mô tả nếu có
                                            <p className="text-sm text-gray-700 mt-1 mb-2">{task.description}</p>
                                        )}
                                        <p className="text-sm text-gray-600">Hạn
                                            chót: {formatDateToString(new Date(task.dueDate))}</p>
                                        <span className={`text-xs font-semibold px-2 py-1 rounded-full mt-2 inline-block
                      ${status === 'done' ? 'bg-green-500 text-white' : ''}
                      ${status === 'process' ? 'bg-blue-500 text-white' : ''}
                      ${status === 'over' ? 'bg-red-500 text-white' : ''}`}
                                        >
                      {status === 'done' ? 'Đã xong' : status === 'process' ? 'Đang làm' : 'Quá hạn'}
                    </span>
                                    </div>

                                    {/* Nút chỉnh sửa và xóa ở ngoài cùng bên phải */}
                                    <div className="flex items-center space-x-2 ml-3">
                                        <button
                                            onClick={() => handleEditTask(task)}
                                            className="p-2 rounded-full text-blue-600 hover:bg-blue-100 hover:text-blue-800 transition-colors duration-200"
                                            aria-label={`Sửa công việc ${task.title}`}
                                        >
                                            <Edit size={18}/>
                                        </button>
                                        <button
                                            onClick={() => handleDeleteTask(task.id)}
                                            className="p-2 rounded-full text-red-600 hover:bg-red-700 transition-colors duration-200"
                                            aria-label={`Xóa công việc ${task.title}`}
                                        >
                                            <Trash2 size={18}/>
                                        </button>
                                    </div>
                                </li>
                            );
                        })}
                    </ul>

                    <div className="flex justify-center items-center mt-8 space-x-4">
                        <button
                            onClick={() => handlePageChange(currentPage - 1)}
                            disabled={currentPage === 1}
                            className="px-5 py-2 bg-blue-600 text-white rounded-lg shadow-md hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                        >
                            Trang trước
                        </button>
                        <span className="text-lg font-medium text-gray-800">
              Trang {currentPage} / {totalPages}
            </span>
                        <button
                            onClick={() => handlePageChange(currentPage + 1)}
                            disabled={currentPage === totalPages}
                            className="px-5 py-2 bg-blue-600 text-white rounded-lg shadow-md hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                        >
                            Trang sau
                        </button>
                    </div>
                </div>
            )}
            {!loading && !error && todos.length === 0 && (
                <p className="text-gray-600 text-center text-xl mt-10">Không có công việc nào.</p>
            )}

            <AddTaskModal
                isOpen={isAddTaskModalOpen}
                onClose={() => {
                    setIsAddTaskModalOpen(false);
                    setEditingTask(null);
                }} // Xóa editingTask khi đóng
                onSaveTask={handleSaveTask}
                taskToEdit={editingTask}
            />
        </div>
    );
};

export default ManageTaskPage;