import axios from "axios";
import {HOST} from "../utils/constant.ts";


export const getInfo = async (token : string) => {
    try {
        const response=  await axios.get(`${HOST}/user/info`,{
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

export const changeInfo = async (token : string,name : string) => {
    try {
        const response = await axios.put(`${HOST}/user/changeInfo`,{
            name: name
        }, {
            headers: {
                "Authorization": `Bearer ${token}`
            }
        })
        return response.data;
    }
    catch (e: any) {
        return e.response.data;
    }
}
export const changeEmail = async (token : string,email : string) => {
    try {
        const response = await axios.put(`${HOST}/user/changeEmail`, {
            email: email
        }, {
            headers : {
                "Authorization": `Bearer ${token}`
            }
        });
        return response.data;
    }
    catch(e: any) {
        return e.response.data;
    }
}
export const changePassword = async (token : string,oldPassword : string, newPassword: string) => {
    try {
        const response = await axios.put(`${HOST}/user/changePassword`, {
            oldPassword: oldPassword, newPassword: newPassword
        }, {
            headers : {
                "Authorization": `Bearer ${token}`
            }
        });
        return response.data;
    }
    catch(e: any) {
        return e.response.data;
    }
}
export const changeAvatar = async (token : string, file: File) => {
    try {
        const  form = new FormData();
        form.append('avatar', file);
        const resp = await axios.put(`${HOST}/user/changeAvatar`, form, {
            headers : {
                "Authorization": `Bearer ${token}`,
                "content-type": "multipart/form-data"
            }
        })
        return resp.data;
    }
    catch(e: any) {
        return e.response.data;
    }
}