import React, {useEffect, useState} from "react";
import {X} from "lucide-react";
import type { Task } from "../types/task";


interface AddTaskModalProps {
    isOpen: boolean;
    onClose: () => void;
    onSaveTask: (task: Task) => void; // Changed from onAddTask to onSaveTask
    taskToEdit?: Task | null; // Optional prop for editing existing task
}
const AddTaskModal: React.FC<AddTaskModalProps> = ({ isOpen, onClose, onSaveTask, taskToEdit }) => {
    const [title, setTitle] = useState<string>('');
    const [description, setDescription] = useState<string>('');
    const [dueDate, setDueDate] = useState<string>('');

    useEffect(() => {
        if (taskToEdit) {
            setTitle(taskToEdit.title);
            setDescription(taskToEdit.description);
            console.log(new Date(taskToEdit.dueDate));
            const date= new Date(taskToEdit.dueDate);
            setDueDate(`${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`);
        } else {
            setTitle('');
            setDescription('');
            setDueDate('');
        }

    }, [taskToEdit, isOpen]); // Reset form when modal opens or taskToEdit changes

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        console.log(dueDate)
        if (title.trim() === '' || dueDate.trim() === '') {
            alert('Vui lòng nhập tiêu đề và hạn chót cho công việc.');
            return;
        }

        const savedTask: Task= {
            id: taskToEdit ? taskToEdit.id : Date.now(), // Use existing ID or generate new
            title,
            description,
            dueDate: new Date(dueDate).toLocaleDateString('vi-VN'),
            completed: taskToEdit ? taskToEdit.completed : false, // Preserve completed status for edits
        }

        onSaveTask(savedTask);
        onClose();
    };

    if (!isOpen) return null;

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
                <h2 className="text-2xl font-bold text-gray-800 mb-6">{taskToEdit ? 'Sửa Công việc' : 'Thêm Công việc mới'}</h2>
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label htmlFor="task-title" className="block text-gray-700 text-sm font-medium mb-2">Tiêu đề</label>
                        <input
                            type="text"
                            id="task-title"
                            value={title}
                            onChange={(e: React.ChangeEvent<HTMLInputElement>) => setTitle(e.target.value)}
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-800"
                            placeholder="Nhập tiêu đề công việc"
                            required
                        />
                    </div>
                    <div>
                        <label htmlFor="task-description" className="block text-gray-700 text-sm font-medium mb-2">Mô tả (Tùy chọn)</label>
                        <textarea
                            id="task-description"
                            value={description}
                            onChange={(e: React.ChangeEvent<HTMLTextAreaElement>) => setDescription(e.target.value)}
                            rows={3}
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-800"
                            placeholder="Mô tả chi tiết công việc"
                        ></textarea>
                    </div>
                    <div>
                        <label htmlFor="task-dueDate" className="block text-gray-700 text-sm font-medium mb-2">Hạn chót</label>
                        <input
                            type="date"
                            id="task-dueDate"
                            value={dueDate}
                            onChange={(e: React.ChangeEvent<HTMLInputElement>) => setDueDate(e.target.value)}
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-800"
                            required
                        />
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
                            type="submit"
                            className="px-5 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors duration-200 font-semibold"
                        >
                            {taskToEdit ? 'Lưu thay đổi' : 'Thêm công việc'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default AddTaskModal;