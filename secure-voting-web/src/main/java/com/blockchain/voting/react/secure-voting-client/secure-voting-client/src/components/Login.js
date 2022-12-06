import React, {useRef, useState} from "react";
import Form from "react-validation/build/form";
import Input from "react-validation/build/input";
import CheckButton from "react-validation/build/button";
import VoterLoginService from "../services/VoterLoginService";
import {useNavigate} from "react-router-dom";

const Login = () => {
    const form = useRef();
    const checkBtn = useRef();

    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState(``);

    const [user, setUser] = useState({
        username: "",
        password: ""
    });

    let navigate = useNavigate();

    const required = value => {
        if (!value) {
            return (
                <div className="alert alert-danger" role="alert">
                    This field is required!
                </div>
            );
        }
    };


    const onChangeUsername = (e) => {
        const newUsername = e.target.value;
        setUser(prevstate => ({
            ...prevstate,
            username: newUsername
        }));
        // console.log(user);
    }

    const onChangePassword = (e) => {
        const newPassword = e.target.value;
        setUser(prevstate => ({
            ...prevstate,
            password: newPassword
        }));
        // console.log(user);
    }

    const handleLogin = (e) => {
        e.preventDefault();

        setMessage("");
        setLoading(true);

        form.current.validateAll();

        if (checkBtn.current.context._errors.length === 0) {
            VoterLoginService.login(user.username, user.password).then(
                () => {
                    navigate("/candidates");
                    console.log(user.username, user.password)
                    window.location.reload();
                },
                error => {
                    const resMessage =
                        (error.response &&
                            error.response.data &&
                            error.response.data.message) ||
                        error.message ||
                        error.toString();

                    setLoading(false);
                    setMessage(resMessage);
                }
            );
        } else {
            setLoading(false);
        }
    }

    return (
        <div className="col-md-12">
            <div className="card card-container">
                <Form
                    onSubmit={handleLogin}
                    ref={form}
                >
                    <div className="card-body">
                        <div className="form-group">
                            <label htmlFor="username">Username</label>
                            <Input
                                type="text"
                                className="form-control"
                                name="username"
                                value={user.username}
                                onChange={onChangeUsername}
                                validations={[required]}
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="password">Password</label>
                            <Input
                                type="password"
                                className="form-control"
                                name="password"
                                value={user.password}
                                onChange={onChangePassword}
                                validations={[required]}
                            />
                        </div>

                        <br/>

                        <div className="form-group">
                            <button
                                className="btn btn-primary btn-block"
                                disabled={loading}
                            >
                                {loading && (
                                    <span className="spinner-border spinner-border-sm"></span>
                                )}
                                <span>Login</span>
                            </button>
                        </div>

                        {message && (
                            <div className="form-group">
                                <div className="alert alert-danger" role="alert">
                                    {message}
                                </div>
                            </div>
                        )}
                        <CheckButton
                            style={{display: "none"}}
                            ref={checkBtn}
                        />
                    </div>
                </Form>
            </div>
        </div>
    );
}

export default Login;