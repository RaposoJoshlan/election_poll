import React, {Component} from "react";
import Form from "react-validation/build/form";
import Input from "react-validation/build/input";
import CheckButton from "react-validation/build/button";

import VoterLoginService from "../services/VoterLoginService";
import {Link} from "react-router-dom";

const required = value => {
    if (!value) {
        return (
            <div className="alert alert-danger" role="alert">
                This field is required!
            </div>
        );
    }
};

const vusername = value => {
    if (value.length < 3 || value.length > 20) {
        return (
            <div className="alert alert-danger" role="alert">
                The username must be between 3 and 20 characters.
            </div>
        );
    }
};

const vpassword = value => {
    if (value.length < 6 || value.length > 40) {
        return (
            <div className="alert alert-danger" role="alert">
                The password must be between 6 and 40 characters.
            </div>
        );
    }
};



const genders = [
    {value: 'Select-From-List', text:'Select-From-List'},
    {value: 'MALE', text: 'MALE'},
    {value: 'FEMALE', text: 'FEMALE'},
    {value: 'OTHER', text: 'OTHER'},
    {value: 'PRIVATE', text: 'PRIVATE'}
];

export default class Register extends Component {
    constructor(props) {
        super(props);
        this.handleRegister = this.handleRegister.bind(this);
        this.onChangeUsername = this.onChangeUsername.bind(this);
        this.onChangePassword = this.onChangePassword.bind(this);
        this.onChangeVoterFirstName = this.onChangeVoterFirstName.bind(this);
        this.onChangeVoterLastName = this.onChangeVoterLastName.bind(this);
        this.onChangeVoterGender = this.onChangeVoterGender.bind(this);
        this.onChangeVoterLocation = this.onChangeVoterLocation.bind(this);

        this.state = {
            username: "",
            password: "",
            VoterFirstName: "",
            VoterLastName: "",
            VoterGender: "",
            VoterLocation: "",
            successful: false,
            message: ""
        };
    }

    onChangeUsername(e) {
        this.setState({
            username: e.target.value
        });
    }

    onChangePassword(e) {
        this.setState({
            password: e.target.value
        });
    }

    onChangeVoterFirstName(e) {
        this.setState({
            VoterFirstName: e.target.value
        });
    }

    onChangeVoterLastName(e) {
        this.setState({
            VoterLastName: e.target.value
        });
    }

    onChangeVoterGender(e) {
        this.setState({
            VoterGender: e.target.value
        });
    }

    onChangeVoterLocation(e) {
        this.setState({
            VoterLocation: e.target.value
        });
    }

    handleRegister(e) {
        e.preventDefault();

        this.setState({
            message: "",
            successful: false
        });

        this.form.validateAll();



        if (this.checkBtn.context._errors.length === 0) {
            VoterLoginService.register(
                this.state.username,
                this.state.password,
                this.state.VoterFirstName,
                this.state.VoterLastName,
                this.state.VoterGender,
                this.state.VoterLocation
            ).then(
                response => {
                    this.setState({
                        message: response.data.message,
                        successful: true
                    });
                },
                error => {
                    const resMessage =
                        (error.response &&
                            error.response.data &&
                            error.response.data.message) ||
                        error.message ||
                        error.toString();

                    this.setState({
                        successful: false,
                        message: resMessage
                    });
                }
            );
        }

        console.log(this.state.VoterFirstName, this.state.VoterLastName, this.state.VoterGender, this.state.VoterLocation)
    }

    render() {
        return (
            <div className="col-md-12">
                <div className="card card-container">
                    <Form
                        onSubmit={this.handleRegister}
                        ref={c => {
                            this.form = c;
                        }}
                    >
                        {!this.state.successful && (
                            <div className="card-body">
                                <div className="form-group mb-2">
                                    <label htmlFor="username">Username</label>
                                    <Input
                                        placeholder="Enter username"
                                        type="text"
                                        className="form-control"
                                        name="username"
                                        value={this.state.username}
                                        onChange={this.onChangeUsername}
                                        validations={[required, vusername]}
                                    />
                                </div>

                                <div className="form-group mb-2">
                                    <label htmlFor="password">Password</label>
                                    <Input
                                        placeholder="Enter password"
                                        type="password"
                                        className="form-control"
                                        name="password"
                                        value={this.state.password}
                                        onChange={this.onChangePassword}
                                        validations={[required, vpassword]}
                                    />
                                </div>

                                <div className="form-group mb-2">
                                    <label htmlFor="VoterFirstName">First Name</label>
                                    <Input
                                        placeholder="Enter First Name"
                                        type="text"
                                        className="form-control"
                                        name="VoterFirstName"
                                        value={this.state.VoterFirstName}
                                        onChange={this.onChangeVoterFirstName}
                                        validations={[required]}
                                    />
                                </div>

                                <div className="form-group mb-2">
                                    <label htmlFor="VoterLastName">Last Name</label>
                                    <Input
                                        placeholder="Enter Last Name"
                                        type="text"
                                        className="form-control"
                                        name="VoterLastName"
                                        value={this.state.VoterLastName}
                                        onChange={this.onChangeVoterLastName}
                                        validations={[required]}
                                    />
                                </div>

                                <div className="form-group mb-2">
                                    <label className="form-label"> Voter Gender </label>
                                    <select
                                        placeholder="Select Gender"
                                        className="form-select"
                                        name="VoterGender"
                                        value={this.state.VoterGender}
                                        onChange={this.onChangeVoterGender}
                                    >
                                        {genders.map(item => {
                                            return (<option key={item.value} value={item.value}>{item.text}</option>)
                                        })}
                                    </select>
                                </div>

                                <div className="form-group mb-2">
                                    <label htmlFor="VoterLocation">Region</label>
                                    <Input
                                        placeholder="Enter Region"
                                        type="text"
                                        className="form-control"
                                        name="VoterLocation"
                                        value={this.state.VoterLocation}
                                        onChange={this.onChangeVoterLocation}
                                        validations={[required]}
                                    />
                                </div>
                                <br/>
                                <div className="form-group mb-2">
                                    <button className="btn btn-primary btn-block" style={{marginRight: "5px"}}>Sign Up
                                    </button>
                                    <Link to="/" className="btn btn-danger"> Cancel </Link>
                                </div>
                            </div>
                        )}

                        {this.state.message && (
                            <div className="form-group mb-2">
                                <div
                                    className={
                                        this.state.successful
                                            ? "alert alert-success"
                                            : "alert alert-danger"
                                    }
                                    role="alert"
                                >
                                    {this.state.message}
                                </div>
                            </div>
                        )}
                        <CheckButton
                            style={{display: "none"}}
                            ref={c => {
                                this.checkBtn = c;
                            }}
                        />
                    </Form>
                </div>
            </div>
        );
    }
}