import axios from "axios";

const API_URL = "http://localhost:8082/auth/";

class VoterLoginService {
    async login(username, password) {
        const response = await axios
            .post(API_URL + "signin", {
                username,
                password
            });
        console.log("FROM LOGIN POST REQUEST: ", response.data.accessToken);
        if (response.data.accessToken) {
            localStorage.setItem("user", JSON.stringify(response.data));
        }
        return response.data;
    }

    logout() {
        localStorage.removeItem("user");
    }

    register(username, password, VoterFirstName, VoterLastName, VoterGender, VoterLocation) {
        return axios.post(API_URL + "signup", {
            username,
            password,
            VoterFirstName,
            VoterLastName,
            VoterGender,
            VoterLocation,
        });
    }

    getCurrentUser() {
        return JSON.parse(localStorage.getItem('user'));
        ;
    }
}

export default new VoterLoginService();