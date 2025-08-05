
export interface Task {
    id: number;
    title: string;
    description: string;
    dueDate: string;
    completed: boolean;
}
export interface TaskStatic {
    totalTasks: number,
    completedTasks: number,
    overdueTasks: number,
    inProgressTasks: number,
    dueSoonToday: number,
    dueSoonThisWeek: number,
    dueSoonNextWeek: number,
    dueSoonAfterTwoWeeks: number
}