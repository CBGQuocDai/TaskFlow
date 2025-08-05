import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import {GoogleOAuthProvider} from "@react-oauth/google";
import {BrowserRouter} from 'react-router-dom';
import {GG_CLIENT_ID} from "./utils/constant.ts";


createRoot(document.getElementById('root')!).render(
  <StrictMode>
      <GoogleOAuthProvider clientId={GG_CLIENT_ID} >
            <BrowserRouter>
                <App />
            </BrowserRouter>
      </GoogleOAuthProvider>
  </StrictMode>,
)
