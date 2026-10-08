import { useEffect, useState } from "react";
import Sidebar from "../components/Sidebar";
import "./Budgets.css";

const API_BASE_URL = "https://velora-backend-x8ec.onrender.com";

// =====================================================
// TOKEN
// =====================================================

function getToken() {
    return localStorage.getItem("token");
}

// =====================================================
// AUTH HEADERS
// =====================================================

function authHeaders() {
    const token = getToken();

    return {
        Authorization: `Bearer ${token}`,
    };
}

// =====================================================
// COMPONENT
// =====================================================

function Budgets() {

    // =================================================
    // STATE
    // =================================================

    const [budgets, setBudgets] = useState([]);
    const [loading, setLoading] = useState(true);

    const [showModal, setShowModal] = useState(false);
    const [editingBudget, setEditingBudget] = useState(null);

    const [category, setCategory] = useState("");
    const [amount, setAmount] = useState("");

    const [selectedMonth, setSelectedMonth] = useState(8);
    const [selectedYear, setSelectedYear] = useState(2026);

    // =================================================
    // LOAD BUDGETS WHEN MONTH/YEAR CHANGES
    // =================================================

    useEffect(() => {
        loadBudgets();
    }, [selectedMonth, selectedYear]);

    // =================================================
    // LOAD BUDGETS
    // =================================================

    const loadBudgets = async () => {
        try {
            setLoading(true);

            const response = await fetch(
                `${API_BASE_URL}/api/budgets?month=${selectedMonth}&year=${selectedYear}`,
                {
                    method: "GET",
                    headers: authHeaders(),
                }
            );

            if (!response.ok) {
                throw new Error(
                    `Failed to fetch budgets (${response.status})`
                );
            }

            const data = await response.json();

            console.log("Budgets received:", data);

            setBudgets(Array.isArray(data) ? data : []);

        } catch (error) {
            console.error("Failed to load budgets:", error);
            setBudgets([]);
        } finally {
            setLoading(false);
        }
    };

    // =================================================
    // SUMMARY
    // =================================================

    const totalBudget = budgets.reduce(
        (total, budget) => {
            return total + Number(budget.amount || 0);
        },
        0
    );

    const totalSpent = budgets.reduce(
        (total, budget) => {
            return total + Number(budget.spent || 0);
        },
        0
    );

    const totalRemaining = totalBudget - totalSpent;

    // =================================================
    // OPEN ADD MODAL
    // =================================================

    const openAddModal = () => {
        setEditingBudget(null);
        setCategory("");
        setAmount("");
        setShowModal(true);
    };

    // =================================================
    // OPEN EDIT MODAL
    // =================================================

    const openEditModal = (budget) => {
        setEditingBudget(budget);
        setCategory(budget.category || "");
        setAmount(budget.amount || "");
        setShowModal(true);
    };

    // =================================================
    // CLOSE MODAL
    // =================================================

    const closeModal = () => {
        setShowModal(false);
        setEditingBudget(null);
        setCategory("");
        setAmount("");
    };

    // =================================================
    // ADD / UPDATE BUDGET
    // =================================================

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!category.trim()) {
            alert("Please enter a category.");
            return;
        }

        if (!amount || Number(amount) <= 0) {
            alert("Please enter a valid amount.");
            return;
        }

        try {

            const budgetData = {
    category: category.trim(),
    amount: Number(amount),
    month: selectedMonth,
    year: selectedYear,
};

            let response;

            // UPDATE
            if (editingBudget) {

                response = await fetch(
                    `${API_BASE_URL}/api/budgets/${editingBudget.id}`,
                    {
                        method: "PUT",
                        headers: {
                            "Content-Type": "application/json",
                            ...authHeaders(),
                        },
                        body: JSON.stringify(budgetData),
                    }
                );

            } else {

                // ADD
                response = await fetch(
                    `${API_BASE_URL}/api/budgets`,
                    {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json",
                            ...authHeaders(),
                        },
                        body: JSON.stringify(budgetData),
                    }
                );
            }

            if (!response.ok) {

                const errorText = await response.text();

                console.error(
                    "Budget API error:",
                    errorText
                );

                throw new Error(
                    `Failed to save budget (${response.status})`
                );
            }

            const wasEditing = !!editingBudget;

await loadBudgets();
closeModal();

if (wasEditing) {
    alert("Budget updated successfully!");
} else {
    alert("Budget added successfully!");
}

        } catch (error) {

            console.error(
                "Failed to save budget:",
                error
            );

            alert(
                "Unable to save budget. Please try again."
            );
        }
    };

    // =================================================
    // DELETE BUDGET
    // =================================================

    const handleDelete = async (id) => {

        const confirmed = window.confirm(
            "Are you sure you want to delete this budget?"
        );

        if (!confirmed) {
            return;
        }

        try {

            const response = await fetch(
                `${API_BASE_URL}/api/budgets/${id}`,
                {
                    method: "DELETE",
                    headers: authHeaders(),
                }
            );

            if (!response.ok) {
                throw new Error(
                    `Failed to delete budget (${response.status})`
                );
            }

            await loadBudgets();
            alert("Budget deleted successfully!");

        } catch (error) {

            console.error(
                "Failed to delete budget:",
                error
            );

            alert("Unable to delete budget.");
        }
    };

    // =================================================
    // FORMAT MONEY
    // =================================================

    const formatMoney = (value) => {
        return Number(value || 0).toLocaleString("en-IN");
    };

    // =================================================
    // UI
    // =================================================

    return (
        <div className="budgets-layout">

            <Sidebar />

            <main className="budgets-main">

                {/* HEADER */}

                <header className="budgets-header">

                    <div>

                        <p className="budgets-eyebrow">
                            FINANCIAL PLANNING
                        </p>

                        <h1>Budgets</h1>

                        <p className="budgets-subtitle">
                            Plan your spending and stay within your limits.
                        </p>

                    </div>

                    <button
                        className="add-budget-btn"
                        onClick={openAddModal}
                    >
                        + Add Budget
                    </button>

                </header>

                {/* SUMMARY */}

                <section className="budget-summary">

                    <div className="budget-summary-card">

                        <span>Total budget</span>

                        <strong>
                            ₹{formatMoney(totalBudget)}
                        </strong>

                    </div>

                    <div className="budget-summary-card">

                        <span>Spent</span>

                        <strong>
                            ₹{formatMoney(totalSpent)}
                        </strong>

                    </div>

                    <div className="budget-summary-card">

                        <span>Remaining</span>

                        <strong>
                            ₹{formatMoney(totalRemaining)}
                        </strong>

                    </div>

                </section>

                {/* BUDGET PANEL */}

                <section className="budgets-panel">

                    <div className="budgets-panel-header">

                        <div>

                            <p className="panel-label">
                                MONTHLY BUDGETS
                            </p>

                            <h2>
                                Your spending limits
                            </h2>

                        </div>

                        <select
                            className="month-button"
                            value={`${selectedMonth}-${selectedYear}`}
                            onChange={(e) => {

                                const [month, year] =
                                    e.target.value.split("-");

                                setSelectedMonth(Number(month));
                                setSelectedYear(Number(year));

                            }}
                        >

                            <option value="1-2026">
                                January 2026
                            </option>

                            <option value="2-2026">
                                February 2026
                            </option>

                            <option value="3-2026">
                                March 2026
                            </option>

                            <option value="4-2026">
                                April 2026
                            </option>

                            <option value="5-2026">
                                May 2026
                            </option>

                            <option value="6-2026">
                                June 2026
                            </option>

                            <option value="7-2026">
                                July 2026
                            </option>

                            <option value="8-2026">
                                August 2026
                            </option>

                            <option value="9-2026">
                                September 2026
                            </option>

                            <option value="10-2026">
                                October 2026
                            </option>

                            <option value="11-2026">
                                November 2026
                            </option>

                            <option value="12-2026">
                                December 2026
                            </option>

                        </select>

                    </div>

                    {/* BUDGET LIST */}

                    <div className="budget-list">

                        {loading ? (

                            <div className="empty-budget">
                                Loading budgets...
                            </div>

                        ) : budgets.length === 0 ? (

                            <div className="empty-budget">

                                <div className="empty-budget-icon">
                                    💰
                                </div>

                                <h3>
                                    No budgets yet
                                </h3>

                                <p>
                                    Create your first budget to start
                                    tracking your spending limits.
                                </p>

                                <button
                                    onClick={openAddModal}
                                    className="empty-add-budget"
                                >
                                    + Add Budget
                                </button>

                            </div>

                        ) : (

                            budgets.map((budget) => {

                                const spent =
                                    Number(budget.spent || 0);

                                const budgetAmount =
                                    Number(budget.amount || 0);

                                const remaining =
                                    budgetAmount - spent;

                                let progress =
                                    Number(budget.progress || 0);

                                if (progress < 0) {
                                    progress = 0;
                                }

                                if (progress > 100) {
                                    progress = 100;
                                }

                                return (

                                    <div
                                        className="budget-item"
                                        key={budget.id}
                                    >

                                        {/* TOP */}

                                        <div className="budget-top">

                                            <div>

                                                <h3>
                                                    {budget.category}
                                                </h3>

                                                <p>
                                                    ₹{formatMoney(spent)}
                                                    {" of "}
                                                    ₹{formatMoney(
                                                        budgetAmount
                                                    )}
                                                </p>

                                            </div>

                                            <span>
                                                {Math.round(progress)}%
                                            </span>

                                        </div>

                                        {/* PROGRESS */}

                                        <div className="budget-progress">

                                            <div
                                                className="budget-progress-fill"
                                                style={{
                                                    width: `${progress}%`,
                                                }}
                                            />

                                        </div>

                                        {/* BOTTOM */}

                                        <div className="budget-bottom">

                                           <span className="budget-remaining">
    Remaining: ₹
    {formatMoney(
        remaining
    )}
</span>

                                            <div className="budget-actions">

                                                <button
                                                    className="budget-edit-btn"
                                                    onClick={() =>
                                                        openEditModal(
                                                            budget
                                                        )
                                                    }
                                                >
                                                    Edit
                                                </button>

                                                <button
                                                    className="budget-delete-btn"
                                                    onClick={() =>
                                                        handleDelete(
                                                            budget.id
                                                        )
                                                    }
                                                >
                                                    Delete
                                                </button>

                                            </div>

                                        </div>

                                    </div>
                                );
                            })

                        )}

                    </div>

                </section>

                {/* ADD / EDIT MODAL */}

                {showModal && (

                    <div
                        className="budget-modal-overlay"
                        onClick={closeModal}
                    >

                        <div
                            className="budget-modal"
                            onClick={(e) =>
                                e.stopPropagation()
                            }
                        >

                            {/* MODAL HEADER */}

                            <div className="budget-modal-header">

                                <div>

                                    <p className="panel-label">
                                        {editingBudget
                                            ? "UPDATE BUDGET"
                                            : "NEW BUDGET"}
                                    </p>

                                    <h2>
                                        {editingBudget
                                            ? "Edit budget"
                                            : "Add budget"}
                                    </h2>

                                </div>

                                <button
                                    className="modal-close"
                                    onClick={closeModal}
                                >
                                    ×
                                </button>

                            </div>

                            {/* FORM */}

                            <form onSubmit={handleSubmit}>

                                {/* CATEGORY */}

                                <div className="modal-form-group">

                                    <label>
                                        Category
                                    </label>

                                    <input
                                        type="text"
                                        placeholder="e.g. Food & Dining"
                                        value={category}
                                        onChange={(e) =>
                                            setCategory(
                                                e.target.value
                                            )
                                        }
                                        required
                                    />

                                </div>

                                {/* AMOUNT */}

                                <div className="modal-form-group">

                                    <label>
                                        Budget amount
                                    </label>

                                    <div className="amount-input">

                                        <span>₹</span>

                                        <input
                                            type="number"
                                            min="1"
                                            step="0.01"
                                            placeholder="0.00"
                                            value={amount}
                                            onChange={(e) =>
                                                setAmount(
                                                    e.target.value
                                                )
                                            }
                                            required
                                        />

                                    </div>

                                </div>

                                {/* ACTIONS */}

                                <div className="modal-actions">

                                    <button
                                        type="button"
                                        className="cancel-button"
                                        onClick={closeModal}
                                    >
                                        Cancel
                                    </button>

                                    <button
                                        type="submit"
                                        className="save-budget-button"
                                    >
                                        {editingBudget
                                            ? "Update budget"
                                            : "Add budget"}
                                    </button>

                                </div>

                            </form>

                        </div>

                    </div>

                )}

            </main>

        </div>
    );
}

export default Budgets;