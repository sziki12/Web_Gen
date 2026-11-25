import {createContext, useContext, useState} from "react";


const AuthContext = createContext({
    authenticated: false
})
export const AuthData = () => useContext(AuthContext)
export default function AuthState({children}) {
    let [authenticated, setAuthenticated] = useState(true)
    return <AuthContext.Provider value={{
        authenticated
    }}>
        {children}
    </AuthContext.Provider>
}