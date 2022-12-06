import "./App.css";
import CandidateComponent from "./components/CandidateComponent";
import {Link, Route, Routes} from "react-router-dom";
import VoteForCandidateComponent from "./components/VoteForCandidateComponent";
import {Component} from "react";
import VoterLoginService from "./services/VoterLoginService";
import Login from "./components/Login";
import Register from "./components/Register";
import EventBus from "./components/EventBus";

class App extends Component {

    constructor(props) {
        super(props);
        this.logOut = this.logOut.bind(this);

        this.state = {
            currentUser: undefined,
        };
    }

    componentDidMount() {
        const user = VoterLoginService.getCurrentUser();

        if (user) {
            this.setState({
                currentUser: user
            });
        }

        EventBus.on("logout", () => {
            this.logOut();
        });
    }

    componentWillUnmount() {
        EventBus.remove("logout");
    }

    logOut() {
        VoterLoginService.logout();
        this.setState({
            currentUser: undefined,
        });
    }


    render() {

        const {currentUser} = this.state;

        return (
            <div>
                <nav className="navbar navbar-expand navbar-dark bg-dark">
                    <Link to={"/"} className="navbar-brand">
                        SecureVoting
                    </Link>
                    {
                        currentUser ? (
                            <div className="navbar-nav ml-auto">
                                <li className="nav-item">
                                    <Link to={"/candidates"} className="nav-link">
                                        {currentUser.username}
                                    </Link>
                                </li>
                                <li className="nav-item">
                                    <a href="/login" className="nav-link" onClick={this.logOut}>
                                        LogOut
                                    </a>
                                </li>
                            </div>
                        ) : (
                            <div className="navbar-nav ml-auto">
                                <li className="nav-item">
                                    <Link to={"/login"} className="nav-link">
                                        Login
                                    </Link>
                                </li>

                                <li className="nav-item">
                                    <Link to={"/register"} className="nav-link">
                                        Sign Up
                                    </Link>
                                </li>
                            </div>
                        )
                    }
                </nav>
                <div className="container mt-3">
                    <Routes>
                        <Route exact path={"/login"} element={<Login/>}></Route>

                        <Route exact path={"/register"} element={<Register/>}></Route>

                        <Route exact path={"/candidates"} element={<CandidateComponent/>}></Route>

                        <Route path={"/:id/vote"} element={<VoteForCandidateComponent/>}></Route>
                    </Routes>
                </div>

            </div>
        );
    }
}

export default App;
