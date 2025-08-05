import type {LoginRequest, RegisterRequest, VerifyOtpRequest} from "../types/auth.ts";
import axios from "axios";
import {HOST} from "../utils/constant.ts";

export const verifyToken = async (token : string) => {
    try {
        const response=  await axios.post(`${HOST}/auth/token`,{},{
            headers: {
                "Authorization": `Bearer ${token}`,
            }
        }) ;
        // console.log(response);
        return response.data;
    } catch (err: any) {
        // console.log(err.message);
        return err.response.data;
    }
}
export const login = async (body : LoginRequest) => {
    const response = await axios.post(`${HOST}/auth/login`, body) ;
    return response.data;
}
export const loginWithGoogle = async (code : string) => {
    try {
        const response = await axios.post(`${HOST}/auth/googleLogin`, {
            code : code,
        });
        return response.data;
    } catch (e: any) {
        console.error(e);
        if(e.response.data.code==1002) {
            alert("có lỗi vui lòng, thử lại")
        }
    }
}
export const logout = async (token : string) => {
    try {
        const response = await axios.post(`${HOST}/auth/logout`, {}, {
            headers: {
                "Authorization": `Bearer ${token}`
            }
        })
        return response.data;
    }
    catch (e: any) {
        console.error(e);
        if(e.response.data.code==1001) {
            alert("phiên đăng nhập của bạn đã hết.")
        }
    }
}
export const register = async (body: RegisterRequest) => {
    try {
        const response = await axios.post(`${HOST}/auth/register`, body) ;
        return response.data;
    }catch (e: any) {
        console.error(e);
        if(e.response.data.code==1001) {
            alert("email đã tồn tại")
        }
    }
}
export const forgotPassword = async (email: string) => {
    try {
        const response = await axios.post(`${HOST}/auth/forgotPassword`, {
            email : email,
        })
        return response.data;
    }
    catch (e: any) {
        console.error(e);
        if(e.response.data.code==1012) {
            alert("email không tồn tại")
        }
    }
}
export const resendEmail = async (email: string) => {
    try {
        const response = await axios.post(`${HOST}/auth/resendEmail?email=${email}`) ;
        return response.data;
    }
    catch (e: any) {
        console.error(e);
        const err=e.response.data;
        if(err.code===1012) {
            alert("email không tồn tại");
        }
        if(err.code ===1005) {
            alert("email đã tồn tại");
        }
    }
}
export const verifyOtp = async (body: VerifyOtpRequest) => {
    try {
        const response = await axios.post(`${HOST}/auth/verifyOtp`,body)
        return response.data;
    }
    catch (e: any) {
        console.error(e);
        if(e.response.data.code==1010) {
            alert("otp không đúng")
        }
        if (e.response.data.code==1011) {
            alert("otp đã hết hạn")
        }
    }
}
export const resetPassword = async (token: string, password : string ) => {
    try {
        const response = await axios.post(`${HOST}/auth/resetPassword`,
            {password : password}, {
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });
        return response.data;
    }
    catch (e: any) {
        console.error(e);
        if(e.response.data.code==1009) {
            alert("Mật khẩu phải có ít nhất 8 kí tự")
        }
    }
}