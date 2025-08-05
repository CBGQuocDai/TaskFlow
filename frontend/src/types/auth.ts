export interface LoginResponse {
    token: string;
}

export interface LoginRequest {
    email : string,
    password : string,
}
export interface RegisterRequest {
    email : string,
    password : string,
    name : string,

}
export interface VerifyOtpRequest {
    email : string,
    otp : string,
    actionType: string,
}