import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./ForgotPassword.css";

function ResetPassword() {
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const navigate = useNavigate();

    const email = sessionStorage.getItem("resetEmail");

    const handleResetPassword = async (e) => {
        e.preventDefault();

        setError("");

        if (newPassword !== confirmPassword) {
            setError("Passwords do not match");
            return;
        }

        if (newPassword.length < 6) {
            setError("Password must be at least 6 characters");
            return;
        }

        setLoading(true);

        try {
            const response = await fetch(
                "https://velora-backend-x8ec.onrender.com/api/users/reset-password",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({
                        email,
                        newPassword,
                    }),
                }
            );

            const result = await response.text();

            if (!response.ok) {
                throw new Error("Unable to reset password");
            }

            if (result !== "Password Reset Successfully") {
                throw new Error(result);
            }

            sessionStorage.removeItem("resetEmail");
            sessionStorage.removeItem("resetOtpVerified");

            navigate("/login");

        } catch (error) {
            setError(error.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="forgot-page">
            <div className="forgot-card">

                <h2>Create New Password</h2>

                <p>
                    Enter a new password for your Velora account.
                </p>

                <form onSubmit={handleResetPassword}>

                    <div className="form-group">
                        <label>New password</label>

                        <input
                            type="password"
                            placeholder="Enter new password"
                            value={newPassword}
                            onChange={(e) =>
                                setNewPassword(e.target.value)
                            }
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Confirm password</label>

                        <input
                            type="password"
                            placeholder="Confirm new password"
                            value={confirmPassword}
                            onChange={(e) =>
                                setConfirmPassword(e.target.value)
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
                            ? "Resetting..."
                            : "Reset Password"}
                    </button>

                </form>

            </div>
        </div>
    );
}

export default ResetPassword;