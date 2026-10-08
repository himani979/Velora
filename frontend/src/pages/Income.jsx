import { useEffect, useState } from "react";
import Sidebar from "../components/Sidebar";
import "./Income.css";


const API_BASE_URL = "https://velora-backend-x8ec.onrender.com";

function getToken() {
    return localStorage.getItem("token");
}

function authHeaders() {
    return {
        Authorization: `Bearer ${getToken()}`,
    };
}

function Income() {

    // =========================
    // STATE
    // =========================

    const [incomes, setIncomes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showModal, setShowModal] = useState(false);
    const [editingIncome, setEditingIncome] = useState(null);

    // Form states
    const [source, setSource] = useState("");
    const [amount, setAmount] = useState("");
    const [incomeDate, setIncomeDate] = useState("");
    const [note, setNote] = useState("");

    // =========================
    // LOAD INCOME
    // =========================

    useEffect(() => {
        loadIncome();
    }, []);

    const loadIncome = async () => {

        try {

            setLoading(true);

            const response = await fetch(
                `${API_BASE_URL}/api/income`,
                {
                    headers: authHeaders(),
                }
            );

            if (!response.ok) {
                throw new Error("Failed to fetch income");
            }

            const data = await response.json();

            setIncomes(data || []);

        } catch (error) {

            console.error("Failed to load income:", error);

        } finally {

            setLoading(false);
        }
    };

    // =========================
    // TOTAL INCOME
    // =========================

    const totalIncome = incomes.reduce(
        (total, income) =>
            total + Number(income.amount || 0),
        0
    );

    // =========================
    // OPEN ADD MODAL
    // =========================

    const openAddModal = () => {

        setEditingIncome(null);

        setSource("");
        setAmount("");
        setIncomeDate("");
        setNote("");

        setShowModal(true);
    };

    // =========================
    // OPEN EDIT MODAL
    // =========================

    const openEditModal = (income) => {

        setEditingIncome(income);

        setSource(income.source || "");
        setAmount(income.amount || "");
        setIncomeDate(income.incomeDate || "");
        setNote(income.note || "");

        setShowModal(true);
    };

    // =========================
    // CLOSE MODAL
    // =========================

    const closeModal = () => {

        setShowModal(false);

        setEditingIncome(null);

        setSource("");
        setAmount("");
        setIncomeDate("");
        setNote("");
    };

    // =========================
    // ADD / UPDATE
    // =========================

    const handleSubmit = async (e) => {

        e.preventDefault();

        try {

            const incomeData = {

                source: source.trim(),

                amount: Number(amount),

                incomeDate: incomeDate,

                note: note.trim(),
            };

            let response;

            const wasEditing = !!editingIncome;

            // UPDATE
            if (editingIncome) {

                response = await fetch(
                    `${API_BASE_URL}/api/income/${editingIncome.id}`,
                    {
                        method: "PUT",

                        headers: {
                            "Content-Type": "application/json",
                            ...authHeaders(),
                        },

                        body: JSON.stringify(incomeData),
                    }
                );

            }

            // ADD
            else {

                response = await fetch(
                    `${API_BASE_URL}/api/income`,
                    {
                        method: "POST",

                        headers: {
                            "Content-Type": "application/json",
                            ...authHeaders(),
                        },

                        body: JSON.stringify(incomeData),
                    }
                );
            }

            if (!response.ok) {

                const errorText =
                    await response.text();

                console.error(
                    "Income API error:",
                    errorText
                );

                throw new Error(
                    `Failed to save income (${response.status})`
                );
            }

            await loadIncome();

            closeModal();

            if (wasEditing) {
                alert("Income updated successfully!");
            } else {
                alert("Income added successfully!");
            }

        } catch (error) {

            console.error(
                "Failed to save income:",
                error
            );

            alert(
                "Unable to save income. Please try again."
            );
        }
    };

    // =========================
    // DELETE
    // =========================

    const handleDelete = async (id) => {

        const confirmed = window.confirm(
            "Are you sure you want to delete this income?"
        );

        if (!confirmed) {
            return;
        }

        try {

            const response = await fetch(
                `${API_BASE_URL}/api/income/${id}`,
                {
                    method: "DELETE",
                    headers: authHeaders(),
                }
            );

            if (!response.ok) {
                throw new Error("Failed to delete income");
            }

            await loadIncome();

            alert("Income deleted successfully!");

        } catch (error) {

            console.error(
                "Failed to delete income:",
                error
            );

            alert(
                "Unable to delete income."
            );
        }
    };

    // =========================
    // FORMAT DATE
    // =========================

    const formatDate = (date) => {

        if (!date) {
            return "";
        }

        return new Date(date).toLocaleDateString(
            "en-IN",
            {
                day: "numeric",
                month: "short",
                year: "numeric",
            }
        );
    };

    // =========================
    // UI
    // =========================

    return (

        <div className="income-layout">

            <Sidebar />

            <main className="income-main">

                {/* HEADER */}

                <header className="income-header">

                    <div>

                        <p className="income-eyebrow">
                            PERSONAL FINANCE
                        </p>

                        <h1>
                            Income
                        </h1>

                        <p className="income-subtitle">
                            Track and manage your sources of income.
                        </p>

                    </div>

                    <button
                        className="add-income-button"
                        onClick={openAddModal}
                    >
                        + Add income
                    </button>

                </header>


                {/* SUMMARY */}

                <section className="income-summary">

                    <div className="income-summary-card">

                        <p>
                            Total income
                        </p>

                        <h2>
                            ₹
                            {totalIncome.toLocaleString(
                                "en-IN"
                            )}
                        </h2>

                        <span>
                            All time
                        </span>

                    </div>


                    <div className="income-summary-card">

                        <p>
                            Transactions
                        </p>

                        <h2>
                            {incomes.length}
                        </h2>

                        <span>
                            Recorded income
                        </span>

                    </div>


                    <div className="income-summary-card">

                        <p>
                            Latest income
                        </p>

                        <h2>

                            {incomes.length > 0
                                ? `₹${Number(
                                    incomes[0].amount
                                ).toLocaleString("en-IN")}`
                                : "₹0"}

                        </h2>

                        <span>
                            Most recent
                        </span>

                    </div>

                </section>


                {/* INCOME PANEL */}

                <section className="income-panel">

                    <div className="income-panel-header">

                        <div>

                            <p className="panel-label">
                                TRANSACTIONS
                            </p>

                            <h3>
                                Income history
                            </h3>

                        </div>

                    </div>


                    {/* LIST */}

                    <div className="income-list">

                        {loading ? (

                            <div className="empty-income">

                                Loading income...

                            </div>

                        ) : incomes.length === 0 ? (

                            <div className="empty-income">

                                <div className="empty-income-icon">
                                    💰
                                </div>

                                <h3>
                                    No income recorded
                                </h3>

                                <p>
                                    Add your first income to start
                                    tracking your earnings.
                                </p>

                                <button
                                    onClick={openAddModal}
                                    className="empty-add-income"
                                >
                                    + Add income
                                </button>

                            </div>

                        ) : (

                            incomes.map((income) => (

                                <div
                                    className="income-row"
                                    key={income.id}
                                >

                                    <div className="income-icon">
                                        💰
                                    </div>


                                    <div className="income-details">

                                        <strong>
                                            {income.source}
                                        </strong>

                                        <span>

                                            {income.note ||
                                                "Income"}
                                            {" • "}
                                            {formatDate(
                                                income.incomeDate
                                            )}

                                        </span>

                                    </div>


                                    <div className="income-amount">

                                        +₹
                                        {Number(
                                            income.amount
                                        ).toLocaleString(
                                            "en-IN"
                                        )}

                                    </div>


                                    <div className="income-row-actions">

                                        <button
                                            className="income-edit-button"
                                            onClick={() =>
                                                openEditModal(
                                                    income
                                                )
                                            }
                                        >
                                            Edit
                                        </button>


                                        <button
                                            className="income-delete-button"
                                            onClick={() =>
                                                handleDelete(
                                                    income.id
                                                )
                                            }
                                        >
                                            Delete
                                        </button>

                                    </div>

                                </div>

                            ))

                        )}

                    </div>

                </section>


                {/* ADD / EDIT MODAL */}

                {showModal && (

                    <div
                        className="income-modal-overlay"
                        onClick={closeModal}
                    >

                        <div
                            className="income-modal"
                            onClick={(e) =>
                                e.stopPropagation()
                            }
                        >

                            {/* MODAL HEADER */}

                            <div className="income-modal-header">

                                <div>

                                    <p className="panel-label">

                                        {editingIncome
                                            ? "UPDATE INCOME"
                                            : "NEW INCOME"}

                                    </p>

                                    <h2>

                                        {editingIncome
                                            ? "Edit income"
                                            : "Add income"}

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

                                {/* SOURCE */}

                                <div className="modal-form-group">

                                    <label>
                                        Source
                                    </label>

                                    <input
                                        type="text"
                                        placeholder="e.g. Salary, Freelance"
                                        value={source}
                                        onChange={(e) =>
                                            setSource(
                                                e.target.value
                                            )
                                        }
                                        required
                                    />

                                </div>


                                {/* AMOUNT */}

                                <div className="modal-form-group">

                                    <label>
                                        Amount
                                    </label>

                                    <div className="amount-input">

                                        <span>
                                            ₹
                                        </span>

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


                                {/* DATE */}

                                <div className="modal-form-group">

                                    <label>
                                        Date
                                    </label>

                                    <input
                                        type="date"
                                        value={incomeDate}
                                        onChange={(e) =>
                                            setIncomeDate(
                                                e.target.value
                                            )
                                        }
                                        required
                                    />

                                </div>


                                {/* NOTE */}

                                <div className="modal-form-group">

                                    <label>
                                        Note
                                    </label>

                                    <textarea
                                        placeholder="Add an optional note..."
                                        value={note}
                                        onChange={(e) =>
                                            setNote(
                                                e.target.value
                                            )
                                        }
                                    />

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
                                        className="save-income-button"
                                    >

                                        {editingIncome
                                            ? "Update income"
                                            : "Add income"}

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

export default Income;