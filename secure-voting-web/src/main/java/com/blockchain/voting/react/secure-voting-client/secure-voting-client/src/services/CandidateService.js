import axios from "axios";
import authHeader from "./auth-header";

const CANDIDATE_API_BASE_URL = "http://localhost:8082/candidates";

class CandidateService {
    async getCandidates() {
        return await axios.get(CANDIDATE_API_BASE_URL, {headers: authHeader()});
    }

    getCandidateById(candidateId) {
        return axios.get(CANDIDATE_API_BASE_URL + "/" + candidateId, {headers: authHeader()});
    }

    voteForCandidate(candidateId, vote) {
        return axios.post(CANDIDATE_API_BASE_URL + "/" + candidateId + "/vote", vote, {headers: authHeader()});
    }
}

export default new CandidateService();
