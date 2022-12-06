import React, {useEffect, useRef, useState} from 'react';
import {Link, useParams, useNavigate} from "react-router-dom";
import CandidateService from "../services/CandidateService";
import VoterLoginService from "../services/VoterLoginService";
import Form from "react-validation/build/form";
import CheckButton from "react-validation/build/button";

const VoteForCandidateComponent = () => {

    const [FirstName, setFirstName] = useState('')
    const [LastName, setLastName] = useState('')
    const [GovId, setGovId] = useState('')
    const [Election, setElection] = useState('')
    const [Gender, setGender] = useState('')
    const [Constituency, setConstituency] = useState('')
    const [VoterId, setVoterId] = useState('')
    const [VoterFullName, setVoterFullName] = useState('')
    const [VoterGender, setVoterGender] = useState('')
    const [VoterLocation, setVoterLocation] = useState('')
    const [message, setMessage] = useState(``);

    const {id} = useParams();

    let navigate = useNavigate();

    const form = useRef();
    const checkBtn = useRef();

    const saveOrUpdateEmployee = (e) => {
        e.preventDefault();

        setMessage("");

        const vote = {
            VotedCandidate: {
                FirstName, LastName, GovId, Election, Gender, Constituency
            },

            VoterId, VoterFullName, VoterGender, VoterLocation
        }

        if (id && checkBtn.current.context._errors.length === 0) {
            CandidateService.voteForCandidate(id, vote).then((response) => {
                console.log(response.data)
                navigate('/candidates')
            }).catch(error => {
                const resMessage =
                    (error.response &&
                        error.response.data &&
                        error.response.data.message) ||
                    error.message ||
                    error.toString();

                setMessage(resMessage);
            })
        }
    }

    useEffect(() => {

        const currentUser = VoterLoginService.getCurrentUser();

        CandidateService.getCandidateById(id).then((response) => {
            const candidate = response.data[0];
            setFirstName(candidate.FirstName)
            setLastName(candidate.LastName)
            setGovId(candidate.GovId)
            setElection(candidate.Election.ElectionParty)
            setGender(candidate.Gender)
            setConstituency(candidate.Constituency)
            setVoterId(currentUser.id)
            setVoterFullName(currentUser.userFirstName + " " + currentUser.userLastName)
            setVoterGender(currentUser.userGender)
            setVoterLocation(currentUser.userLocation)
        }).catch(error => {
            console.log(error)
        })
        // console.log("Current Logged In user: ", VoterLoginService.getCurrentUser())
    }, [])

    const title = () => {

        if (id) {
            return <h5> Enter Voter Details</h5>
        }
    }

    return (
        <div>
            <br/><br/>
            <div className="container">
                <div className="row">
                    <div className="card col-md-100">
                        <Form
                            onSubmit={saveOrUpdateEmployee}
                            ref={form}
                        >
                            <div className="card-body">
                                <h5>Candidate Details</h5>
                                <div className="form-group mb-2">
                                    <label className="form-label"> First Name </label>
                                    <input
                                        type="text"
                                        name="Gender"
                                        className="form-control"
                                        defaultValue={FirstName}
                                        disabled={true}
                                    >
                                    </input>
                                </div>
                                <div className="form-group mb-2">
                                    <label className="form-label"> Last Name </label>
                                    <input
                                        type="text"
                                        name="Location"
                                        className="form-control"
                                        defaultValue={LastName}
                                        disabled={true}

                                    >
                                    </input>
                                </div>
                                <div className="form-group mb-2">
                                    <label className="form-label"> GovId </label>
                                    <input
                                        type="text"
                                        name="Gender"
                                        className="form-control"
                                        defaultValue={GovId}
                                        disabled={true}
                                    >
                                    </input>
                                </div>
                                <div className="form-group mb-2">
                                    <label className="form-label"> Election </label>
                                    <input
                                        type="text"
                                        name="Gender"
                                        className="form-control"
                                        defaultValue={Election}
                                        disabled={true}
                                    >
                                    </input>
                                </div>
                                <div className="form-group mb-2">
                                    <label className="form-label"> Gender </label>
                                    <input
                                        type="text"
                                        name="Gender"
                                        className="form-control"
                                        defaultValue={Gender}
                                        disabled={true}
                                    >
                                    </input>
                                </div>
                                <div className="form-group mb-2">
                                    <label className="form-label"> Constituency </label>
                                    <input
                                        type="text"
                                        name="Gender"
                                        className="form-control"
                                        defaultValue={Constituency}
                                        disabled={true}
                                        // onChange={(e) => setConstituency(e.target.value)}
                                    >
                                    </input>
                                </div>
                            </div>
                            <div className="card-body">
                                <h5>Voter Details</h5>
                                <div className="form-group mb-2">
                                    <label className="form-label"> Voter Id </label>
                                    <input
                                        type="text"
                                        placeholder="Enter Gender"
                                        name="Gender"
                                        className="form-control"
                                        value={VoterId}
                                        disabled={true}
                                        // onChange={(e) => setVoterId(e.target.value)}
                                    >
                                    </input>
                                </div>

                                <div className="form-group mb-2">
                                    <label className="form-label"> Voter Full Name </label>
                                    <input
                                        type="text"
                                        placeholder="Enter Gender"
                                        name="Gender"
                                        className="form-control"
                                        value={VoterFullName}
                                        disabled={true}
                                        // onChange={(e) => setVoterFullName(e.target.value)}
                                    >
                                    </input>
                                </div>

                                <div className="form-group mb-2">
                                    <label className="form-label"> Gender </label>
                                    <input
                                        type="text"
                                        name="Gender"
                                        className="form-control"
                                        value={VoterGender}
                                        disabled={true}
                                        // onChange={(e) => setVoterGender(e.target.value)}
                                    >
                                    </input>
                                </div>
                                <div className="form-group mb-2">
                                    <label className="form-label"> Location </label>
                                    <input
                                        type="text"
                                        placeholder="Enter Location"
                                        name="Location"
                                        className="form-control"
                                        value={VoterLocation}
                                        disabled={true}
                                        // onChange={(e) => setVoterLocation(e.target.value)}
                                    >
                                    </input>
                                </div>
                            </div>
                            <div className="card-body">
                                <button className="btn btn-success" style={{marginRight: "5px"}}> Submit
                                </button>
                                <Link to="/candidates" className="btn btn-danger"> Cancel </Link>
                            </div>
                            {message && (
                                <div className="form-group">

                                    <script className="alert alert-danger" role="alert">
                                        function myFunction() {
                                        alert(message, navigate("/candidates"))
                                    }
                                    </script>
                                </div>
                            )}
                            <CheckButton
                                style={{display: "none"}}
                                ref={checkBtn}
                            />
                        </Form>
                    </div>
                </div>

            </div>

        </div>
    )
}

export default VoteForCandidateComponent;
