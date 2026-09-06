import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { registerUser } from "../services/api";
import "./Register.css";

function Register() {
    const [fullName, setFullName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const navigate = useNavigate();

    const handleRegister = async (e) => {
        e.preventDefault();

        setError("");
        setLoading(true);

        try {
            await registerUser({
                fullName,
                email,
                password
            });

            console.log("Registration successful!");

            // After registration, go to login
            navigate("/login");

        } catch (error) {
            console.error("Registration error:", error);
            setError(error.message || "Unable to create account");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="register-page">

            <div className="register-container">

                {/* Left Section */}
                <div className="register-brand">

                    <h1>Velora</h1>

                    <p className="register-tagline">
                        Start managing your money smarter.
                    </p>

                    <p className="register-description">
                        Create your account and get a clear view of your
                        expenses, budgets, savings, and subscriptions.
                    </p>

                    <div className="register-feature">
                        <span>✓</span>
                        <p>Track every expense</p>
                    </div>

                    <div className="register-feature">
                        <span>✓</span>
                        <p>Build better saving habits</p>
                    </div>

                    <div className="register-feature">
                        <span>✓</span>
                        <p>Stay ahead of upcoming payments</p>
                    </div>

                </div>

                {/* Right Section */}
                <div className="register-card">

                    <div className="register-header">
                        <h2>Create your account</h2>
                        <p>
                            Join Velora and take control of your finances.
                        </p>
                    </div>

                    <form onSubmit={handleRegister}>

                        <div className="form-group">
                            <label>Full name</label>

                            <input
                                type="text"
                                placeholder="Enter your name"
                                value={fullName}
                                onChange={(e) =>
                                    setFullName(e.target.value)
                                }
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label>Email address</label>

                            <input
                                type="email"
                                placeholder="you@example.com"
                                value={email}
                                onChange={(e) =>
                                    setEmail(e.target.value)
                                }
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label>Password</label>

                            <input
                                type="password"
                                placeholder="Create a password"
                                value={password}
                                onChange={(e) =>
                                    setPassword(e.target.value)
                                }
                                required
                            />
                        </div>

                        {error && (
                            <p className="register-error">
                                {error}
                            </p>
                        )}

                        <button
                            type="submit"
                            className="register-button"
                            disabled={loading}
                        >
                            {loading
                                ? "Creating account..."
                                : "Create account"}
                        </button>

                    </form>

                    <div className="login-link">
                        <span>Already have an account?</span>
                        <Link to="/login">Sign in</Link>
                    </div>

                </div>

            </div>

        </div>
    );
}

export default Register;