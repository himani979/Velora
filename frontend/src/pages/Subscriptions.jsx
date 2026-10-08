import { useEffect, useState } from "react";
import Sidebar from "../components/Sidebar";
import "./Subscriptions.css";

const API_BASE_URL = "https://velora-backend-x8ec.onrender.com";

const subscriptionPlans = {
    Netflix: [
        "Basic",
        "Standard",
        "Premium"
    ],

    Spotify: [
        "Individual",
        "Duo",
        "Family",
        "Student"
    ],

    "Amazon Prime": [
        "Monthly",
        "Annual"
    ],

    "Canva Pro": [
        "Free",
        "Pro",
        "Teams"
    ],

    "Google One": [
        "100 GB",
        "200 GB",
        "2 TB"
    ],

    "YouTube Premium": [
        "Individual",
        "Family",
        "Student"
    ],

    "Disney+ Hotstar": [
        "Mobile",
        "Super",
        "Premium"
    ],

    "Microsoft 365": [
        "Personal",
        "Family"
    ],

    Adobe: [
        "Photography",
        "Individual",
        "All Apps"
    ]
};

function Subscriptions() {
    const [subscriptions, setSubscriptions] = useState([]);
    const [reminders, setReminders] = useState([]);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [editingSubscription, setEditingSubscription] = useState(null);

    const [formData, setFormData] = useState({
        serviceName: "",
        plan: "",
        amount: "",
        billingCycle: "Monthly",
        nextPaymentDate: "",
        autoPay: false,
        status: "ACTIVE",
        reminderDaysBefore: 3
    });

    // =========================
    // TOKEN
    // =========================

    const getToken = () => {
        return localStorage.getItem("token");
    };

    // =========================
    // FETCH SUBSCRIPTIONS
    // =========================

    const fetchSubscriptions = async () => {
        try {
            setLoading(true);
            setError("");

            const response = await fetch(
                `${API_BASE_URL}/api/subscriptions`,
                {
                    method: "GET",
                    headers: {
                        Authorization: `Bearer ${getToken()}`
                    }
                }
            );

            if (!response.ok) {
                throw new Error("Failed to load subscriptions");
            }

            const data = await response.json();
            setSubscriptions(data);

        } catch (err) {
            console.error(err);
            setError("Unable to load subscriptions.");
        } finally {
            setLoading(false);
        }
    };

    // =========================
    // FETCH REMINDERS
    // =========================

    const fetchReminders = async () => {
        try {
            const response = await fetch(
                `${API_BASE_URL}/api/subscriptions/reminders`,
                {
                    method: "GET",
                    headers: {
                        Authorization: `Bearer ${getToken()}`
                    }
                }
            );

            if (!response.ok) {
                throw new Error("Failed to load reminders");
            }

            const data = await response.json();
            setReminders(data);

        } catch (err) {
            console.error("Reminder loading failed:", err);
        }
    };

    // =========================
    // INITIAL LOAD
    // =========================

    useEffect(() => {
        fetchSubscriptions();
        fetchReminders();
    }, []);

    // =========================
    // FORM CHANGE
    // =========================

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;

        setFormData((prev) => ({
            ...prev,
            [name]: type === "checkbox" ? checked : value
        }));
    };

    // =========================
    // RESET FORM
    // =========================

    const resetForm = () => {
        setEditingSubscription(null);

        setFormData({
            serviceName: "",
            plan: "",
            amount: "",
            billingCycle: "Monthly",
            nextPaymentDate: "",
            autoPay: false,
            status: "ACTIVE",
            reminderDaysBefore: 3
        });
    };

    // =========================
    // ADD SUBSCRIPTION
    // =========================

    const openAddForm = () => {
        resetForm();

        setTimeout(() => {
            document
                .getElementById("subscription-editor")
                ?.scrollIntoView({
                    behavior: "smooth",
                    block: "start"
                });
        }, 100);
    };

    // =========================
    // EDIT SUBSCRIPTION
    // =========================

    const handleEdit = (subscription) => {
        setEditingSubscription(subscription);

        setFormData({
            serviceName: subscription.serviceName || "",
            plan: subscription.plan || "",
            amount: subscription.amount || "",
            billingCycle: subscription.billingCycle || "Monthly",
            nextPaymentDate: subscription.nextPaymentDate || "",
            autoPay: Boolean(subscription.autoPay),
            status: subscription.status || "ACTIVE",
            reminderDaysBefore:
                subscription.reminderDaysBefore ?? 3
        });

        setTimeout(() => {
            document
                .getElementById("subscription-editor")
                ?.scrollIntoView({
                    behavior: "smooth",
                    block: "start"
                });
        }, 100);
    };

    // =========================
    // SUBMIT
    // =========================

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const isEditing = editingSubscription !== null;

            const url = isEditing
                ? `${API_BASE_URL}/api/subscriptions/${editingSubscription.id}`
                : `${API_BASE_URL}/api/subscriptions`;

            const response = await fetch(url, {
                method: isEditing ? "PUT" : "POST",

                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${getToken()}`
                },

                body: JSON.stringify({
                    serviceName: formData.serviceName,
                    plan: formData.plan,
                    amount: Number(formData.amount),
                    billingCycle: formData.billingCycle,
                    nextPaymentDate: formData.nextPaymentDate,
                    autoPay: formData.autoPay,
                    status: formData.status,
                    reminderDaysBefore:
                        Number(formData.reminderDaysBefore)
                })
            });

            if (!response.ok) {
                const message = await response.text();
                throw new Error(
                    message || "Failed to save subscription"
                );
            }

            const wasEditing = editingSubscription !== null;

resetForm();

await fetchSubscriptions();
await fetchReminders();

if (wasEditing) {
    alert("Subscription updated successfully!");
} else {
    alert("Subscription added successfully!");
}
        } catch (err) {
            console.error(err);
            alert("Unable to save subscription.");
        }
    };

    // =========================
    // DELETE
    // =========================

    const handleDelete = async (id) => {
        const confirmed = window.confirm(
            "Are you sure you want to delete this subscription?"
        );

        if (!confirmed) return;

        try {
            const response = await fetch(
                `${API_BASE_URL}/api/subscriptions/${id}`,
                {
                    method: "DELETE",
                    headers: {
                        Authorization: `Bearer ${getToken()}`
                    }
                }
            );

            if (!response.ok) {
                throw new Error(
                    "Failed to delete subscription"
                );
            }

            if (editingSubscription?.id === id) {
                resetForm();
            }

            await fetchSubscriptions();
            await fetchReminders();
            alert("Subscription deleted successfully!");

        } catch (err) {
            console.error(err);
            alert("Unable to delete subscription.");
        }
    };

    // =========================
    // TOGGLE AUTOPAY
    // =========================

    const handleAutoPay = async (id) => {
        try {
            const response = await fetch(
                `${API_BASE_URL}/api/subscriptions/${id}/autopay`,
                {
                    method: "PUT",
                    headers: {
                        Authorization: `Bearer ${getToken()}`
                    }
                }
            );

            if (!response.ok) {
                throw new Error(
                    "Failed to update AutoPay"
                );
            }

            await fetchSubscriptions();
            await fetchReminders();
            alert("AutoPay updated successfully!");

        } catch (err) {
            console.error(err);
            alert("Unable to update AutoPay.");
        }
    };

    // =========================
    // MONTHLY COST
    // =========================

    const calculateMonthlyCost = () => {
        return subscriptions.reduce(
            (total, subscription) => {
                const amount =
                    Number(subscription.amount) || 0;

                const cycle =
                    subscription.billingCycle?.toLowerCase();

                if (cycle === "yearly") {
                    return total + amount / 12;
                }

                if (cycle === "quarterly") {
                    return total + amount / 3;
                }

                return total + amount;
            },
            0
        );
    };

    // =========================
    // ACTIVE
    // =========================

    const activeSubscriptions =
        subscriptions.filter(
            (subscription) =>
                subscription.status === "ACTIVE"
        );

    // =========================
    // UPCOMING THIS MONTH
    // =========================

    const upcomingThisMonth =
        subscriptions.filter((subscription) => {
            if (!subscription.nextPaymentDate) {
                return false;
            }

            const paymentDate = new Date(
                subscription.nextPaymentDate
            );

            const today = new Date();

            return (
                paymentDate.getMonth() === today.getMonth() &&
                paymentDate.getFullYear() ===
                    today.getFullYear()
            );
        });

    const upcomingAmount =
        upcomingThisMonth.reduce(
            (total, subscription) =>
                total +
                (Number(subscription.amount) || 0),
            0
        );

    // =========================
    // DATE FORMAT
    // =========================

    const formatDate = (date) => {
        if (!date) return "";

        return new Date(date).toLocaleDateString(
            "en-IN",
            {
                day: "numeric",
                month: "short"
            }
        );
    };

    // =========================
    // RENDER
    // =========================

    return (
        <div className="subscriptions-layout">

            <Sidebar />

            <main className="subscriptions-main">

                {/* HEADER */}

                <header className="subscriptions-header">

                    <div>
                        <p className="subscriptions-eyebrow">
                            RECURRING PAYMENTS
                        </p>

                        <h1>Subscriptions</h1>

                        <p className="subscriptions-subtitle">
                            Keep track of your recurring
                            payments and upcoming renewals.
                        </p>
                    </div>

                    <button
                        className="add-subscription-btn"
                        onClick={openAddForm}
                    >
                        + Add Subscription
                    </button>

                </header>


                {/* SUMMARY */}

                <section className="subscription-summary">

                    <div className="subscription-summary-card">

                        <div className="summary-icon">
                            💳
                        </div>

                        <div>
                            <span>Monthly cost</span>

                            <strong>
                                ₹
                                {calculateMonthlyCost().toFixed(0)}
                            </strong>
                        </div>

                    </div>


                    <div className="subscription-summary-card">

                        <div className="summary-icon">
                            ▣
                        </div>

                        <div>
                            <span>
                                Active subscriptions
                            </span>

                            <strong>
                                {activeSubscriptions.length}
                            </strong>
                        </div>

                    </div>


                    <div className="subscription-summary-card">

                        <div className="summary-icon">
                            ◫
                        </div>

                        <div>
                            <span>
                                Upcoming this month
                            </span>

                            <strong>
                                ₹
                                {upcomingAmount.toFixed(0)}
                            </strong>
                        </div>

                    </div>

                </section>


                {/* ERROR */}

                {error && (
                    <div className="subscription-error">
                        {error}
                    </div>
                )}


                {/* REMINDERS */}

                {reminders.length > 0 && (

                    <section className="subscription-reminders">

                        <div className="subscriptions-panel-header">

                            <div>
                                <p className="panel-label">
                                    PAYMENT REMINDERS
                                </p>

                                <h2>
                                    Upcoming payments
                                </h2>
                            </div>

                        </div>

                        <div className="reminder-list">

                            {reminders.map(
                                (reminder, index) => (

                                    <div
                                        className="reminder-card"
                                        key={index}
                                    >

                                        <div className="reminder-icon">
                                            !
                                        </div>

                                        <div className="reminder-info">

                                            <h3>
                                                {reminder.serviceName}
                                            </h3>

                                            <p>
                                                {reminder.message}
                                            </p>

                                        </div>

                                        <strong>
                                            ₹
                                            {Number(
                                                reminder.amount
                                            ).toFixed(0)}
                                        </strong>

                                    </div>

                                )
                            )}

                        </div>

                    </section>

                )}


                {/* SUBSCRIPTIONS */}

                <section className="subscriptions-panel">

                    <div className="subscriptions-panel-header">

                        <div>
                            <p className="panel-label">
                                YOUR SUBSCRIPTIONS
                            </p>

                            <h2>
                                Active subscriptions
                            </h2>
                        </div>

                    </div>


                    {loading ? (

                        <div className="subscription-empty">
                            Loading subscriptions...
                        </div>

                    ) : subscriptions.length === 0 ? (

                        <div className="subscription-empty">

                            <h3>
                                No subscriptions yet
                            </h3>

                            <p>
                                Add your first recurring
                                payment to start tracking it.
                            </p>

                        </div>

                    ) : (

                        <div className="subscription-list">

                            {subscriptions.map(
                                (subscription) => (

                                    <div
                                        className="subscription-item"
                                        key={subscription.id}
                                    >

                                        <div className="subscription-icon">
                                            {subscription.serviceName
                                                ?.charAt(0)
                                                ?.toUpperCase()}
                                        </div>


                                        <div className="subscription-info">

                                            <h3>
                                                {
                                                    subscription.serviceName
                                                }
                                            </h3>

                                            <p>
                                                {subscription.plan ||
                                                    "Standard"}

                                                {" · "}

                                                Renews{" "}

                                                {formatDate(
                                                    subscription.nextPaymentDate
                                                )}
                                            </p>

                                            <small>
                                                <span
                                                    className={
                                                        subscription.status ===
                                                        "ACTIVE"
                                                            ? "status-active"
                                                            : "status-other"
                                                    }
                                                >
                                                    {subscription.status}
                                                </span>

                                                <span className="dot">
                                                    ·
                                                </span>

                                                AutoPay:{" "}

                                                {subscription.autoPay
                                                    ? "On"
                                                    : "Off"}
                                            </small>

                                        </div>


                                        <div className="subscription-price">

                                            <strong>
                                                ₹
                                                {Number(
                                                    subscription.amount
                                                ).toFixed(0)}
                                            </strong>

                                            <span>
                                                {
                                                    subscription.billingCycle
                                                }
                                            </span>

                                        </div>


                                        <div className="subscription-actions">

                                            <button
                                                className="edit-btn"
                                                type="button"
                                                onClick={() =>
                                                    handleEdit(
                                                        subscription
                                                    )
                                                }
                                            >
                                                Edit
                                            </button>

                                            <button
                                                className="autopay-btn"
                                                type="button"
                                                onClick={() =>
                                                    handleAutoPay(
                                                        subscription.id
                                                    )
                                                }
                                            >
                                                {subscription.autoPay
                                                    ? "AutoPay Off"
                                                    : "AutoPay On"}
                                            </button>

                                            <button
                                                className="subscription-delete-btn"
                                                type="button"
                                                onClick={() =>
                                                    handleDelete(
                                                        subscription.id
                                                    )
                                                }
                                            >
                                                Delete
                                            </button>

                                        </div>

                                    </div>

                                )
                            )}

                        </div>

                    )}

                </section>


                {/* EDIT / ADD FORM */}

                <section
                    className="subscription-editor"
                    id="subscription-editor"
                >

                    <div className="editor-header">

                        <div>
                            <p className="panel-label">
                                {editingSubscription
                                    ? "UPDATE PAYMENT"
                                    : "NEW PAYMENT"}
                            </p>

                            <h2>
                                {editingSubscription
                                    ? "Edit Subscription"
                                    : "Add Subscription"}
                            </h2>
                        </div>

                    </div>


                    <form
                        className="subscription-form"
                        onSubmit={handleSubmit}
                    >

                        {/* SERVICE */}

                        <div className="form-field">
                            <label>
                                Service Name
                            </label>

                            <select
    name="serviceName"
    value={formData.serviceName}
    onChange={(e) => {
        handleChange(e);

        setFormData(prev => ({
            ...prev,
            serviceName: e.target.value,
            plan: ""
        }));
    }}
    required
>
    <option value="">Select service</option>

    {Object.keys(subscriptionPlans).map(service => (
        <option key={service} value={service}>
            {service}
        </option>
    ))}
</select>
                        </div>


                        {/* PLAN */}

                        <div className="form-field">
                            <label>
                                Plan
                            </label>

                            <select
    name="plan"
    value={formData.plan}
    onChange={handleChange}
    disabled={!formData.serviceName}
>
    <option value="">
        {formData.serviceName
            ? "Select plan"
            : "Select service first"}
    </option>

    {(subscriptionPlans[formData.serviceName] || []).map(
        plan => (
            <option
                key={plan}
                value={plan}
            >
                {plan}
            </option>
        )
    )}
</select>
                        </div>


                        {/* AMOUNT */}

                        <div className="form-field">
                            <label>
                                Amount (₹)
                            </label>

                            <input
                                type="number"
                                name="amount"
                                value={formData.amount}
                                onChange={handleChange}
                                placeholder="649"
                                min="0"
                                step="0.01"
                                required
                            />
                        </div>


                        {/* BILLING */}

                        <div className="form-field">
                            <label>
                                Billing Cycle
                            </label>

                            <select
                                name="billingCycle"
                                value={formData.billingCycle}
                                onChange={handleChange}
                            >
                                <option value="Monthly">
                                    Monthly
                                </option>

                                <option value="Quarterly">
                                    Quarterly
                                </option>

                                <option value="Yearly">
                                    Yearly
                                </option>
                            </select>
                        </div>


                        {/* DATE */}

                        <div className="form-field">
                            <label>
                                Next Payment Date
                            </label>

                            <input
                                type="date"
                                name="nextPaymentDate"
                                value={formData.nextPaymentDate}
                                onChange={handleChange}
                                required
                            />
                        </div>


                        {/* STATUS */}

                        <div className="form-field">
                            <label>
                                Status
                            </label>

                            <select
                                name="status"
                                value={formData.status}
                                onChange={handleChange}
                            >
                                <option value="ACTIVE">
                                    ACTIVE
                                </option>

                                <option value="PAUSED">
                                    PAUSED
                                </option>

                                <option value="CANCELLED">
                                    CANCELLED
                                </option>
                            </select>
                        </div>


                        {/* REMINDER */}

                        <div className="form-field">
                            <label>
                                Reminder Days Before
                            </label>

                            <input
                                type="number"
                                name="reminderDaysBefore"
                                value={
                                    formData.reminderDaysBefore
                                }
                                onChange={handleChange}
                                min="0"
                            />
                        </div>


                        {/* AUTOPAY */}

                        <div className="autopay-field">

                            <label className="autopay-check">

                                <input
                                    type="checkbox"
                                    name="autoPay"
                                    checked={formData.autoPay}
                                    onChange={handleChange}
                                />

                                <span>
                                    <strong>
                                        AutoPay enabled
                                    </strong>

                                    <small>
                                        Payment will be processed
                                        automatically.
                                    </small>
                                </span>

                            </label>

                        </div>


                        {/* FORM BUTTONS */}

                        <div className="subscription-form-actions">

                            <button
                                type="button"
                                className="cancel-editor-btn"
                                onClick={resetForm}
                            >
                                Cancel
                            </button>

                            <button
                                type="submit"
                                className="save-subscription-btn"
                            >
                                {editingSubscription
                                    ? "Update Subscription"
                                    : "Save Subscription"}
                            </button>

                        </div>

                    </form>

                </section>

            </main>

        </div>
    );
}

export default Subscriptions;