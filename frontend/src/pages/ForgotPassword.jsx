import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./ForgotPassword.css";

function ForgotPassword() {
    const [email, setEmail] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();

        setError("");
        setLoading(true);

        try {
            const response = await fetch(
                "http://localhost:8081/api/users/forgot-password",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({
                        email,
                    }),
                }
            );

            const result = await response.text();

            if (!response.ok) {
                throw new Error("Unable to send OTP");
            }

            if (result === "User not found") {
                throw new Error(result);
            }

            sessionStorage.setItem(
                "resetEmail",
                email
            );

            navigate("/verify-reset-otp");

        } catch (error) {
            setError(error.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="forgot-page">
            <div className="forgot-card">

                <h2>Forgot Password</h2>

                <p>
                    Enter your registered email address.
                    We’ll send you a verification OTP.
                </p>

                <form onSubmit={handleSubmit}>

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
                        {loading
                            ? "Sending OTP..."
                            : "Send OTP"}
                    </button>

                </form>

            </div>
        </div>
    );
}

export default ForgotPassword;