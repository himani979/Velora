import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./ForgotPassword.css";

function VerifyResetOtp() {
    const [otp, setOtp] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const navigate = useNavigate();

    const email = sessionStorage.getItem("resetEmail");

    const handleVerifyOtp = async (e) => {
        e.preventDefault();

        setError("");
        setLoading(true);

        try {
            const response = await fetch(
                "http://localhost:8081/api/users/verify-otp",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({
                        email,
                        otp,
                    }),
                }
            );

            const result = await response.text();

            if (!response.ok) {
                throw new Error("OTP verification failed");
            }

            if (
                result === "User not found" ||
                result === "OTP not generated" ||
                result === "Invalid OTP" ||
                result === "OTP Expired"
            ) {
                throw new Error(result);
            }

            sessionStorage.setItem(
                "resetOtpVerified",
                "true"
            );

            navigate("/reset-password");

        } catch (error) {
            setError(error.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="forgot-page">
            <div className="forgot-card">

                <h2>Verify OTP</h2>

                <p>
                    Enter the OTP sent to your registered email.
                </p>

                <form onSubmit={handleVerifyOtp}>

                    <div className="form-group">
                        <label>OTP</label>

                        <input
                            type="text"
                            placeholder="Enter 6-digit OTP"
                            value={otp}
                            onChange={(e) =>
                                setOtp(e.target.value)
                            }
                            maxLength="6"
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
                            ? "Verifying..."
                            : "Verify OTP"}
                    </button>

                </form>

            </div>
        </div>
    );
}

export default VerifyResetOtp;