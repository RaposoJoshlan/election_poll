import React, {useEffect, useState} from "react";
import {Link} from "react-router-dom";
import CandidateService from "../services/CandidateService";
import voterLoginService from "../services/VoterLoginService";
import {useNavigate} from "react-router-dom";

function CandidateComponent() {
    const [loading, setLoading] = useState(false);
    const [candidates, setCandidates] = useState([]);
    const [currentUser, setCurrentUser] = useState({
        username: ""
    })

    let navigate = useNavigate();

    useEffect(() => {
        setLoading(true);
        console.log("set loading to true");
        getCandidates();
        //getCurrentUser();
        setLoading(false);
        console.log("set loading to false");
    }, []);

    const getCandidates = () => {
        CandidateService.getCandidates().then(response => {
            setCandidates(response.data);
            // console.log("Setting res.data to setCandidates");
            // console.log(response.data);
        });
    };

    const getCurrentUser = () => {
        const user = voterLoginService.getCurrentUser();

        if (!currentUser) {
            navigate("/")
        } else {
            setCurrentUser(user);
        }
    }

    if (loading) {
        return <p>Loading...</p>;
    }

    return (
        <div className="container">

            <h1 className="text-center">Candidate List</h1>
            <table className="table table-striped">
                <thead>
                <tr>
                    <th> Candidate Name</th>
                    <th> Gender</th>
                    <th> Constituency</th>
                    <th> Election Party</th>
                    <th> Action</th>
                </tr>
                </thead>
                <tbody>
                {candidates.map(candidate => (
                    <tr key={candidate.GovId}>
                        <td>
                            {candidate.FirstName} {candidate.LastName}
                        </td>
                        <td> {candidate.Gender}</td>
                        <td> {candidate.Constituency}</td>
                        <td> {candidate.Election.ElectionParty}</td>
                        <td>
                            <Link className={"btn btn-info"} to={`/${candidate.GovId}/vote`}>
                                Vote
                            </Link>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}

/*
class ListCandidatesComponent extends Component {
constructor(props) {
super(props)

this.state = {
candidates: []

}
}

componentDidMount() {
CandidateS.getCandidates().then((res) => {
this.setState({candidates:res.data})
});
}

render() {

return (
<div className="container">

<h1 className="text-center">Candidate List</h1>

<table className="table table-striped">
<thead>
<tr>
<th> Candidate Id</th>
<th> Candidate First Name</th>
<th> Candidate Last Name</th>
</tr>
</thead>
<tbody>
{
this.state.candidates.map(
candidate =>
<tr key={candidate.id}>
<td> {candidate.id}</td>
<td> {candidate.FirstName}</td>
<td> {candidate.LastName}</td>
</tr>
)
}
</tbody>
</table>
</div>

)

}
}
*/

export default CandidateComponent;
