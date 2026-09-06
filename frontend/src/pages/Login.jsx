import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import {
    loginUser,
    verifyLoginOtp
} from "../services/api";
import "./Login.css";

function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const [otp, setOtp] = useState("");
const [otpStep, setOtpStep] = useState(false);

    const navigate = useNavigate();

    const handleLogin = async (e) => {
    e.preventDefault();

    setError("");
    setLoading(true);

    try {
        const response = await loginUser(email, password);

        console.log("LOGIN RESPONSE:", response);

        if (response === "User not found!" ||
            response === "Invalid Password!") {
            throw new Error(response);
        }

        if (response === "OTP Sent Successfully") {
            setOtpStep(true);
            return;
        }

        throw new Error("Unable to send OTP");

    } catch (error) {
        console.error("LOGIN ERROR:", error);
        setError(error.message);
    } finally {
        setLoading(false);
    }
};
const handleVerifyOtp = async (e) => {
    e.preventDefault();

    setError("");
    setLoading(true);

    try {
        const token = await verifyLoginOtp(email, otp);

        console.log("OTP VERIFY RESPONSE:", token);

        if (
            token === "User not found!" ||
            token === "OTP not generated" ||
            token === "Invalid OTP" ||
            token === "OTP Expired"
        ) {
            throw new Error(token);
        }

        localStorage.setItem("token", token);

        window.location.href = "/dashboard";

    } catch (error) {
        console.error("OTP VERIFY ERROR:", error);
        setError(error.message);
    } finally {
        setLoading(false);
    }
};
    return (
        <div className="login-page">

            <div className="login-container">

                {/* Left Section */}
                <div className="login-brand">

                    <h1>Velora</h1>

                    <p className="brand-tagline">
                        Take control of your money.
                    </p>

                    <p className="brand-description">
                        Track expenses, manage budgets, monitor subscriptions,
                        and understand your finances — all in one place.
                    </p>

                    <div className="brand-feature">
                        <span>✓</span>
                        <p>Smart expense tracking</p>
                    </div>

                    <div className="brand-feature">
                        <span>✓</span>
                        <p>Budget & savings insights</p>
                    </div>

                    <div className="brand-feature">
                        <span>✓</span>
                        <p>Subscription reminders</p>
                    </div>

                </div>

                {/* Right Section */}
                <div className="login-card">

                    <div className="login-header">
                        <h2>Welcome back</h2>
                        <p>
                            Sign in to continue to your Velora account.
                        </p>
                    </div>

                    {!otpStep ? (
    <form onSubmit={handleLogin}>

        {/* Email */}
        <div className="form-group">
            <label>Email address</label>

            <input
                type="email"
                placeholder="you@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
            />
        </div>

        {/* Password */}
        <div className="form-group">

            <div className="password-label">
                <label>Password</label>

                <Link to="/forgot-password">
    Forgot password?
</Link>
            </div>

            <input
                type="password"
                placeholder="Enter your password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
            />

        </div>

        {error && (
            <p className="login-error">
                {error}
            </p>
        )}

        <button
            type="submit"
            className="login-button"
            disabled={loading}
        >
            {loading ? "Sending OTP..." : "Sign in"}
        </button>

    </form>
) : (
    <form onSubmit={handleVerifyOtp}>

        <div className="form-group">
            <label>Enter OTP</label>

            <input
                type="text"
                placeholder="Enter 6-digit OTP"
                value={otp}
                onChange={(e) => setOtp(e.target.value)}
                maxLength="6"
                required
            />
        </div>

        <p className="otp-message">
            OTP sent to <strong>{email}</strong>
        </p>

        {error && (
            <p className="login-error">
                {error}
            </p>
        )}

        <button
            type="submit"
            className="login-button"
            disabled={loading}
        >
            {loading ? "Verifying..." : "Verify OTP"}
        </button>

        <button
            type="button"
            className="back-login-button"
            onClick={() => {
                setOtpStep(false);
                setOtp("");
                setError("");
            }}
        >
            Back to login
        </button>

    </form>
)}

                    {/* Register */}
                    <div className="register-link">
                        <span>Don't have an account?</span>

                        <Link to="/register">
                            Create account
                        </Link>
                    </div>

                </div>

            </div>

        </div>
    );
}

export default Login;