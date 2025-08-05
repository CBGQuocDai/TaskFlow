export interface ApiResponse<T> {
    code: number;
    message: string;
    data: T;
}
export interface Users {
    id: number,
    name: string,
    email: string,
    avatarUrl: string,
    active: boolean,
}