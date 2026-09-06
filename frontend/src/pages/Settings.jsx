import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import Sidebar from "../components/Sidebar";
import "./Settings.css";

const API_BASE_URL = "http://localhost:8081";

function Settings() {

    const navigate = useNavigate();

    // =====================================================
    // PROFILE
    // =====================================================

    const [profile, setProfile] = useState({
        fullName: "",
        email: "",
        phoneNumber: ""
    });

    const [loadingProfile, setLoadingProfile] = useState(true);
    const [savingProfile, setSavingProfile] = useState(false);

    const [profileError, setProfileError] = useState("");


    // =====================================================
    // FINANCE PREFERENCES
    // =====================================================

    const [currency, setCurrency] = useState(
        localStorage.getItem("velora_currency") || "INR"
    );

    const [budgetPeriod, setBudgetPeriod] = useState(
        localStorage.getItem("velora_budget_period") || "monthly"
    );

    const [financialMonthStart, setFinancialMonthStart] = useState(
        localStorage.getItem("velora_financial_month_start") || "1"
    );


    // =====================================================
    // NOTIFICATIONS
    // =====================================================

    const [subscriptionReminders, setSubscriptionReminders] = useState(
        localStorage.getItem("velora_subscription_reminders") !== "false"
    );

    const [budgetAlerts, setBudgetAlerts] = useState(
        localStorage.getItem("velora_budget_alerts") !== "false"
    );

    const [weeklySummary, setWeeklySummary] = useState(
        localStorage.getItem("velora_weekly_summary") === "true"
    );

    const [monthlyReport, setMonthlyReport] = useState(
        localStorage.getItem("velora_monthly_report") !== "false"
    );


    // =====================================================
    // APPEARANCE
    // =====================================================

    const [theme, setTheme] = useState(
        localStorage.getItem("velora_theme") || "light"
    );

    const [dashboardLayout, setDashboardLayout] = useState(
        localStorage.getItem("velora_dashboard_layout") || "comfortable"
    );


    // =====================================================
    // PASSWORD
    // =====================================================

    const [showPasswordForm, setShowPasswordForm] = useState(false);

    const [currentPassword, setCurrentPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");

    const [changingPassword, setChangingPassword] = useState(false);


    // =====================================================
    // TOKEN
    // =====================================================

    const getToken = () => {
        return localStorage.getItem("token");
    };


    // =====================================================
    // AUTH HEADERS
    // =====================================================

    const authHeaders = () => ({
        Authorization: `Bearer ${getToken()}`
    });


    // =====================================================
    // LOAD PROFILE
    // =====================================================

    const loadProfile = async () => {

        try {

            setLoadingProfile(true);
            setProfileError("");

            const token = getToken();

            if (!token) {
                setProfileError(
                    "Login token not found. Please log in again."
                );

                return;
            }


            const response = await fetch(
                `${API_BASE_URL}/api/users/me`,
                {
                    method: "GET",
                    headers: authHeaders()
                }
            );


            if (!response.ok) {

                const errorText = await response.text();

                console.error(
                    "PROFILE API ERROR:",
                    response.status,
                    errorText
                );

                setProfileError(
                    `Unable to load profile (${response.status}).`
                );

                return;
            }


            const data = await response.json();


            setProfile({
                fullName: data.fullName || "",
                email: data.email || "",
                phoneNumber: data.phoneNumber || ""
            });


        } catch (error) {

            console.error(
                "PROFILE LOAD ERROR:",
                error
            );

            setProfileError(
                "Unable to connect to the server."
            );

        } finally {

            setLoadingProfile(false);
        }
    };


    // =====================================================
    // INITIAL LOAD
    // =====================================================

    useEffect(() => {
        loadProfile();
    }, []);
    // =====================================================
// APPLY THEME
// =====================================================

useEffect(() => {

    const applyTheme = () => {

        let selectedTheme = theme;

        if (theme === "system") {

            const prefersDark =
                window.matchMedia(
                    "(prefers-color-scheme: dark)"
                ).matches;

            selectedTheme =
                prefersDark ? "dark" : "light";
        }

        document.documentElement.setAttribute(
            "data-theme",
            selectedTheme
        );
    };

    applyTheme();

}, [theme]);


    // =====================================================
    // PROFILE CHANGE
    // =====================================================

    const handleProfileChange = (e) => {

        const {
            name,
            value
        } = e.target;


        setProfile(prev => ({
            ...prev,
            [name]: value
        }));
    };


    // =====================================================
    // SAVE PROFILE
    // =====================================================

    const saveProfile = async () => {

        if (!profile.fullName.trim()) {

            alert(
                "Please enter your full name."
            );

            return;
        }


        try {

            setSavingProfile(true);


            const response = await fetch(
                `${API_BASE_URL}/api/users/me`,
                {
                    method: "PUT",

                    headers: {
                        "Content-Type": "application/json",
                        ...authHeaders()
                    },

                    body: JSON.stringify({
                        fullName: profile.fullName.trim(),
                        phoneNumber: profile.phoneNumber.trim()
                    })
                }
            );


            if (!response.ok) {

                const errorText = await response.text();

                console.error(
                    "PROFILE UPDATE API ERROR:",
                    response.status,
                    errorText
                );

                throw new Error(
                    "Failed to save profile"
                );
            }


            const data = await response.json();


            setProfile({
                fullName: data.fullName || "",
                email: data.email || profile.email,
                phoneNumber: data.phoneNumber || ""
            });


            alert(
                "Profile updated successfully."
            );


        } catch (error) {

            console.error(
                "PROFILE UPDATE ERROR:",
                error
            );

            alert(
                "Unable to update profile."
            );


        } finally {

            setSavingProfile(false);
        }
    };


    // =====================================================
    // SAVE PREFERENCES
    // =====================================================

    const savePreferences = () => {

        localStorage.setItem(
            "velora_currency",
            currency
        );

        localStorage.setItem(
            "velora_budget_period",
            budgetPeriod
        );

        localStorage.setItem(
            "velora_financial_month_start",
            financialMonthStart
        );

        localStorage.setItem(
            "velora_subscription_reminders",
            String(subscriptionReminders)
        );

        localStorage.setItem(
            "velora_budget_alerts",
            String(budgetAlerts)
        );

        localStorage.setItem(
            "velora_weekly_summary",
            String(weeklySummary)
        );

        localStorage.setItem(
            "velora_monthly_report",
            String(monthlyReport)
        );

        localStorage.setItem(
            "velora_theme",
            theme
        );

        localStorage.setItem(
            "velora_dashboard_layout",
            dashboardLayout
        );


        alert(
            "Preferences saved."
        );
    };


    // =====================================================
    // CHANGE PASSWORD
    // =====================================================

    const handlePasswordChange = async (e) => {

        e.preventDefault();


        if (!currentPassword) {

            alert(
                "Enter your current password."
            );

            return;
        }


        if (newPassword.length < 6) {

            alert(
                "New password must contain at least 6 characters."
            );

            return;
        }


        if (newPassword !== confirmPassword) {

            alert(
                "New passwords do not match."
            );

            return;
        }


        try {

            setChangingPassword(true);


            const response = await fetch(
                `${API_BASE_URL}/api/users/me/password`,
                {
                    method: "PUT",

                    headers: {
                        "Content-Type": "application/json",
                        ...authHeaders()
                    },

                    body: JSON.stringify({
                        currentPassword,
                        newPassword
                    })
                }
            );


            const result = await response.text();


            if (!response.ok) {

                console.error(
                    "PASSWORD API ERROR:",
                    response.status,
                    result
                );

                throw new Error(
                    result || "Password change failed"
                );
            }


            if (
                result
                    .toLowerCase()
                    .includes("incorrect")
            ) {

                alert(result);

                return;
            }


            alert(result);


            setCurrentPassword("");
            setNewPassword("");
            setConfirmPassword("");

            setShowPasswordForm(false);


        } catch (error) {

            console.error(
                "PASSWORD ERROR:",
                error
            );

            alert(
                "Unable to change password."
            );


        } finally {

            setChangingPassword(false);
        }
    };


    // =====================================================
    // CANCEL PASSWORD FORM
    // =====================================================

    const cancelPasswordChange = () => {

        setShowPasswordForm(false);

        setCurrentPassword("");
        setNewPassword("");
        setConfirmPassword("");
    };


    // =====================================================
    // LOGOUT
    // =====================================================

    const handleLogout = () => {

        localStorage.removeItem("token");

        navigate("/login");
    };


    // =====================================================
    // DELETE ACCOUNT
    // =====================================================

    const handleDeleteAccount = async () => {

        const confirmed =
            window.confirm(
                "Are you sure you want to permanently delete your Velora account?"
            );


        if (!confirmed) {
            return;
        }


        const finalConfirm =
            window.confirm(
                "This action cannot be undone. Delete account permanently?"
            );


        if (!finalConfirm) {
            return;
        }


        try {

            const response = await fetch(
                `${API_BASE_URL}/api/users/me`,
                {
                    method: "DELETE",
                    headers: authHeaders()
                }
            );


            if (!response.ok) {

                const errorText =
                    await response.text();

                console.error(
                    "DELETE ACCOUNT API ERROR:",
                    response.status,
                    errorText
                );

                throw new Error(
                    "Account deletion failed"
                );
            }


            localStorage.clear();


            alert(
                "Account deleted successfully."
            );


            navigate("/login");


        } catch (error) {

            console.error(
                "DELETE ACCOUNT ERROR:",
                error
            );


            alert(
                "Unable to delete account."
            );
        }
    };


    // =====================================================
    // AVATAR INITIALS
    // =====================================================

    const getInitials = () => {

        if (!profile.fullName) {
            return "V";
        }


        const words =
            profile.fullName
                .trim()
                .split(/\s+/);


        if (words.length === 1) {

            return words[0]
                .charAt(0)
                .toUpperCase();
        }


        return (
            words[0].charAt(0) +
            words[words.length - 1].charAt(0)
        ).toUpperCase();
    };


    // =====================================================
    // UI
    // =====================================================

    return (

        <div className="settings-layout">

            <Sidebar />


            <main className="settings-main">


                {/* =====================================
                    HEADER
                ====================================== */}

                <header className="settings-header">

                    <div>

                        <p className="settings-eyebrow">
                            ACCOUNT & PREFERENCES
                        </p>

                        <h1>
                            Settings
                        </h1>

                        <p className="settings-subtitle">
                            Manage your profile, finance
                            preferences, notifications and
                            security.
                        </p>

                    </div>

                </header>


                {/* =====================================
                    PROFILE
                ====================================== */}

                <section className="settings-panel">

                    <div className="settings-panel-header">

                        <div>

                            <p className="panel-label">
                                PROFILE
                            </p>

                            <h2>
                                Personal information
                            </h2>

                        </div>

                    </div>


                    {loadingProfile ? (

                        <p>
                            Loading profile...
                        </p>

                    ) : profileError ? (

                        <div>

                            <p className="security-note">
                                {profileError}
                            </p>

                            <button
                                className="secondary-settings-btn"
                                onClick={loadProfile}
                            >
                                Try again
                            </button>

                        </div>

                    ) : (

                        <div className="profile-section">


                            <div className="profile-avatar">
                                {getInitials()}
                            </div>


                            <div className="profile-details">

                                <h3>
                                    Your profile
                                </h3>

                                <p>
                                    Update the information
                                    associated with your
                                    Velora account.
                                </p>


                                <div className="settings-form">


                                    <div className="settings-field">

                                        <label>
                                            Full name
                                        </label>

                                        <input
                                            type="text"
                                            name="fullName"
                                            value={profile.fullName}
                                            onChange={handleProfileChange}
                                            placeholder="Enter your full name"
                                        />

                                    </div>


                                    <div className="settings-field">

                                        <label>
                                            Email address
                                        </label>

                                        <input
                                            type="email"
                                            value={profile.email}
                                            readOnly
                                        />

                                    </div>


                                    <div className="settings-field">

                                        <label>
                                            Phone number
                                        </label>

                                        <input
                                            type="tel"
                                            name="phoneNumber"
                                            value={profile.phoneNumber}
                                            onChange={handleProfileChange}
                                            placeholder="Enter your phone number"
                                        />

                                    </div>

                                </div>


                                <button
                                    className="save-settings-btn"
                                    onClick={saveProfile}
                                    disabled={savingProfile}
                                >

                                    {savingProfile
                                        ? "Saving..."
                                        : "Save profile"}

                                </button>

                            </div>

                        </div>

                    )}

                </section>


                {/* =====================================
                    FINANCE
                ====================================== */}

                <section className="settings-panel">

                    <div className="settings-panel-header">

                        <p className="panel-label">
                            FINANCE
                        </p>

                        <h2>
                            Finance preferences
                        </h2>

                    </div>


                    <div className="preference-item">

                        <div>

                            <h3>
                                Default currency
                            </h3>

                            <p>
                                Currency used when displaying
                                your financial information.
                            </p>

                        </div>


                        <select
                            value={currency}
                            onChange={(e) =>
                                setCurrency(
                                    e.target.value
                                )
                            }
                        >

                            <option value="INR">
                                ₹ INR
                            </option>

                            <option value="USD">
                                $ USD
                            </option>

                            <option value="EUR">
                                € EUR
                            </option>

                            <option value="GBP">
                                £ GBP
                            </option>

                        </select>

                    </div>


                    <div className="preference-item">

                        <div>

                            <h3>
                                Budget period
                            </h3>

                            <p>
                                Choose how your budgets
                                should be organized.
                            </p>

                        </div>


                        <select
                            value={budgetPeriod}
                            onChange={(e) =>
                                setBudgetPeriod(
                                    e.target.value
                                )
                            }
                        >

                            <option value="monthly">
                                Monthly
                            </option>

                            <option value="weekly">
                                Weekly
                            </option>

                            <option value="yearly">
                                Yearly
                            </option>

                        </select>

                    </div>


                    <div className="preference-item">

                        <div>

                            <h3>
                                Financial month starts on
                            </h3>

                            <p>
                                Choose the day when your
                                monthly financial cycle begins.
                            </p>

                        </div>


                        <select
                            value={financialMonthStart}
                            onChange={(e) =>
                                setFinancialMonthStart(
                                    e.target.value
                                )
                            }
                        >

                            <option value="1">
                                1st day
                            </option>

                            <option value="5">
                                5th day
                            </option>

                            <option value="10">
                                10th day
                            </option>

                            <option value="15">
                                15th day
                            </option>

                        </select>

                    </div>

                </section>


                {/* =====================================
                    NOTIFICATIONS
                ====================================== */}

                <section className="settings-panel">

                    <div className="settings-panel-header">

                        <p className="panel-label">
                            NOTIFICATIONS
                        </p>

                        <h2>
                            Notifications & reminders
                        </h2>

                    </div>


                    <div className="preference-item">

                        <div>

                            <h3>
                                Subscription reminders
                            </h3>

                            <p>
                                Get notified before recurring
                                payments are due.
                            </p>

                        </div>


                        <label className="toggle">

                            <input
                                type="checkbox"
                                checked={subscriptionReminders}
                                onChange={(e) =>
                                    setSubscriptionReminders(
                                        e.target.checked
                                    )
                                }
                            />

                            <span></span>

                        </label>

                    </div>


                    <div className="preference-item">

                        <div>

                            <h3>
                                Budget alerts
                            </h3>

                            <p>
                                Get notified when spending
                                approaches your budget limit.
                            </p>

                        </div>


                        <label className="toggle">

                            <input
                                type="checkbox"
                                checked={budgetAlerts}
                                onChange={(e) =>
                                    setBudgetAlerts(
                                        e.target.checked
                                    )
                                }
                            />

                            <span></span>

                        </label>

                    </div>


                    <div className="preference-item">

                        <div>

                            <h3>
                                Weekly spending summary
                            </h3>

                            <p>
                                Receive a summary of your
                                spending every week.
                            </p>

                        </div>


                        <label className="toggle">

                            <input
                                type="checkbox"
                                checked={weeklySummary}
                                onChange={(e) =>
                                    setWeeklySummary(
                                        e.target.checked
                                    )
                                }
                            />

                            <span></span>

                        </label>

                    </div>


                    <div className="preference-item">

                        <div>

                            <h3>
                                Monthly financial report
                            </h3>

                            <p>
                                Receive a summary of your
                                income, expenses and savings.
                            </p>

                        </div>


                        <label className="toggle">

                            <input
                                type="checkbox"
                                checked={monthlyReport}
                                onChange={(e) =>
                                    setMonthlyReport(
                                        e.target.checked
                                    )
                                }
                            />

                            <span></span>

                        </label>

                    </div>

                </section>


                {/* =====================================
                    APPEARANCE
                ====================================== */}

                <section className="settings-panel">

                    <div className="settings-panel-header">

                        <p className="panel-label">
                            APPEARANCE
                        </p>

                        <h2>
                            Appearance
                        </h2>

                    </div>


                    <div className="preference-item">

                        <div>

                            <h3>
                                Theme
                            </h3>

                            <p>
                                Choose how Velora should
                                look on your device.
                            </p>

                        </div>


                        <select
    className="appearance-select"
    value={theme}
                            onChange={(e) =>
                                setTheme(
                                    e.target.value
                                )
                            }
                        >

                            <option value="light">
                                Light
                            </option>

                            <option value="dark">
                                Dark
                            </option>

                            <option value="system">
                                System default
                            </option>

                        </select>

                    </div>


                    <div className="preference-item">

                        <div>

                            <h3>
                                Dashboard layout
                            </h3>

                            <p>
                                Choose how much information
                                is displayed on your dashboard.
                            </p>

                        </div>


                        <select
    className="appearance-select"
    value={dashboardLayout}
                            onChange={(e) =>
                                setDashboardLayout(
                                    e.target.value
                                )
                            }
                        >

                            <option value="comfortable">
                                Comfortable
                            </option>

                            <option value="compact">
                                Compact
                            </option>

                        </select>

                    </div>


                    <button
                        className="save-settings-btn"
                        onClick={savePreferences}
                    >
                        Save preferences
                    </button>

                </section>


                {/* =====================================
                    SECURITY
                ====================================== */}

                <section className="settings-panel">

                    <div className="settings-panel-header">

                        <p className="panel-label">
                            SECURITY
                        </p>

                        <h2>
                            Password & security
                        </h2>

                    </div>


                    {!showPasswordForm ? (

                        <button
                            className="secondary-settings-btn"
                            onClick={() =>
                                setShowPasswordForm(true)
                            }
                        >
                            Change password
                        </button>

                    ) : (

                        <form
                            className="settings-form"
                            onSubmit={handlePasswordChange}
                        >

                            <div className="settings-field">

                                <label>
                                    Current password
                                </label>

                                <input
                                    type="password"
                                    value={currentPassword}
                                    onChange={(e) =>
                                        setCurrentPassword(
                                            e.target.value
                                        )
                                    }
                                    required
                                />

                            </div>


                            <div className="settings-field">

                                <label>
                                    New password
                                </label>

                                <input
                                    type="password"
                                    value={newPassword}
                                    onChange={(e) =>
                                        setNewPassword(
                                            e.target.value
                                        )
                                    }
                                    required
                                />

                            </div>


                            <div className="settings-field">

                                <label>
                                    Confirm new password
                                </label>

                                <input
                                    type="password"
                                    value={confirmPassword}
                                    onChange={(e) =>
                                        setConfirmPassword(
                                            e.target.value
                                        )
                                    }
                                    required
                                />

                            </div>


                            <div className="security-actions">

                                <button
                                    type="button"
                                    className="secondary-settings-btn"
                                    onClick={cancelPasswordChange}
                                >
                                    Cancel
                                </button>


                                <button
                                    type="submit"
                                    className="save-settings-btn"
                                    disabled={changingPassword}
                                >

                                    {changingPassword
                                        ? "Changing..."
                                        : "Update password"}

                                </button>

                            </div>

                        </form>

                    )}


                    <p className="security-note">
                        Keep your account secure by
                        using a strong password.
                    </p>

                </section>


                {/* =====================================
                    VELORA AI
                ====================================== */}

                <section className="settings-panel ai-settings-panel">

                    <div className="settings-panel-header">

                        <p className="panel-label">
                            VELORA AI
                        </p>

                        <h2>
                            AI Assistant
                        </h2>

                    </div>


                    <div className="ai-settings-content">

                        <div className="ai-icon">
                            ✦
                        </div>


                        <div>

                            <h3>
                                Your personal financial assistant
                            </h3>

                            <p>
                                Velora AI helps you understand
                                your spending, budgets, income,
                                subscriptions and savings.
                            </p>


                            <div className="ai-feature-list">

                                <span>
                                    ✓ Spending insights
                                </span>

                                <span>
                                    ✓ Budget suggestions
                                </span>

                                <span>
                                    ✓ Subscription analysis
                                </span>

                                <span>
                                    ✓ Savings insights
                                </span>

                            </div>

                        </div>

                    </div>

                </section>


                {/* =====================================
                    ACCOUNT
                ====================================== */}

                <section className="settings-panel danger-panel">

                    <div className="settings-panel-header">

                        <p className="panel-label danger-label">
                            ACCOUNT
                        </p>

                        <h2>
                            Account actions
                        </h2>

                    </div>


                    <div className="danger-content">

                        <div>

                            <h3>
                                Log out
                            </h3>

                            <p>
                                Sign out of your Velora
                                account on this device.
                            </p>

                        </div>


                        <button
                            className="logout-btn"
                            onClick={handleLogout}
                        >
                            Log out
                        </button>

                    </div>


                    <div className="danger-content delete-account">

                        <div>

                            <h3>
                                Delete account
                            </h3>

                            <p>
                                Permanently delete your
                                Velora account and
                                associated data.
                            </p>

                        </div>


                        <button
                            className="delete-btn"
                            onClick={handleDeleteAccount}
                        >
                            Delete account
                        </button>

                    </div>

                </section>


            </main>

        </div>
    );
}

export default Settings;