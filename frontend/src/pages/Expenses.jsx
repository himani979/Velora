import { useEffect, useState } from "react";
import Sidebar from "../components/Sidebar";
import {
    getExpenses,
    addExpense,
    updateExpense,
    deleteExpense,
    getTotalExpense,
    getCategorySummary,
    searchExpenses,
    getExpensesByCategory,
} from "../services/api";
import "./Expenses.css";

function Expenses() {

    // =========================
    // STATE
    // =========================

    const [expenses, setExpenses] = useState([]);

    const [totalExpense, setTotalExpense] = useState(0);

    const [highestCategory, setHighestCategory] =
        useState("No expenses");

    const [highestCategoryAmount, setHighestCategoryAmount] =
        useState(0);

    const [showModal, setShowModal] = useState(false);

    const [editingExpense, setEditingExpense] = useState(null);

    const [search, setSearch] = useState("");

    const [selectedCategory, setSelectedCategory] =
        useState("All categories");

    const [loading, setLoading] = useState(true);


    // Form states

    const [amount, setAmount] = useState("");

    const [category, setCategory] =
        useState("Food & Dining");

    const [description, setDescription] =
        useState("");

    const [date, setDate] = useState("");

    const [note, setNote] = useState("");


    // =========================
    // CATEGORY DATA
    // =========================

    const getCategoryData = (category) => {

        const data = {

            "Food & Dining": {
                icon: "🍴",
                type: "food",
            },

            Transportation: {
                icon: "🚗",
                type: "transport",
            },

            Shopping: {
                icon: "🛍️",
                type: "shopping",
            },

            Subscriptions: {
                icon: "🔄",
                type: "subscription",
            },

            Entertainment: {
                icon: "🎬",
                type: "entertainment",
            },

            Health: {
                icon: "💊",
                type: "health",
            },

            Other: {
                icon: "💳",
                type: "other",
            },
        };

        return data[category] || data.Other;
    };


    // =========================
    // LOAD EXPENSES
    // =========================

    useEffect(() => {
        loadExpenses();
    }, []);


    const loadExpenses = async () => {

        try {

            setLoading(true);

            const expenseData = await getExpenses();

            setExpenses(expenseData || []);


            const total = await getTotalExpense();

            setTotalExpense(total || 0);


            const summary = await getCategorySummary();


            if (summary && summary.length > 0) {

                const highest = summary.reduce(
                    (max, current) =>
                        current.amount > max.amount
                            ? current
                            : max
                );

                setHighestCategory(highest.category);

                setHighestCategoryAmount(highest.amount);

            } else {

                setHighestCategory("No expenses");

                setHighestCategoryAmount(0);
            }

        } catch (error) {

            console.error(
                "Failed to load expenses:",
                error
            );

        } finally {

            setLoading(false);
        }
    };


    // =========================
    // OPEN ADD MODAL
    // =========================

    const openAddModal = () => {

        setEditingExpense(null);

        setAmount("");
        setCategory("Food & Dining");
        setDescription("");
        setDate("");
        setNote("");

        setShowModal(true);
    };


    // =========================
    // OPEN EDIT MODAL
    // =========================

    const openEditModal = (expense) => {

        setEditingExpense(expense);

        setAmount(expense.amount);

        setCategory(expense.category);

        setDescription(expense.title);

        setDate(expense.expenseDate);

        setNote(expense.note || "");

        setShowModal(true);
    };


    // =========================
    // ADD / UPDATE EXPENSE
    // =========================

    const handleSubmit = async (e) => {

        e.preventDefault();

        try {

            const expenseData = {

                title:
                    description.trim() ||
                    "Expense",

                amount: Number(amount),

                category: category,

                expenseDate: date,

                note: note,
            };


           if (editingExpense) {

    await updateExpense(
        editingExpense.id,
        expenseData
    );

    alert("Expense updated successfully!");

} else {

    await addExpense(expenseData);

    alert("Expense added successfully!");
}


            await loadExpenses();

            closeModal();

        } catch (error) {

            console.error(
                "Failed to save expense:",
                error
            );

            alert(
                "Unable to save expense. Please try again."
            );
        }
    };


    // =========================
    // DELETE EXPENSE
    // =========================

    const handleDelete = async (id) => {

        const confirmed =
            window.confirm(
                "Are you sure you want to delete this expense?"
            );

        if (!confirmed) {
            return;
        }


        try {

            await deleteExpense(id);

            await loadExpenses();
            alert("Expense deleted successfully!");

        } catch (error) {

            console.error(
                "Failed to delete expense:",
                error
            );

            alert(
                "Unable to delete expense."
            );
        }
    };


    // =========================
    // CLOSE MODAL
    // =========================

    const closeModal = () => {

        setShowModal(false);

        setEditingExpense(null);

        setAmount("");
        setCategory("Food & Dining");
        setDescription("");
        setDate("");
        setNote("");
    };


    // =========================
    // SEARCH
    // =========================

    const handleSearch = async (value) => {

        setSearch(value);

        if (!value.trim()) {

            loadExpenses();

            return;
        }


        try {

            const result =
                await searchExpenses(value);

            setExpenses(result || []);

        } catch (error) {

            console.error(
                "Search failed:",
                error
            );
        }
    };


    // =========================
    // CATEGORY FILTER
    // =========================

    const handleCategoryFilter = async (value) => {

        setSelectedCategory(value);

        if (value === "All categories") {

            loadExpenses();

            return;
        }


        try {

            const result =
                await getExpensesByCategory(value);

            setExpenses(result || []);

        } catch (error) {

            console.error(
                "Category filter failed:",
                error
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

        <div className="expenses-layout">

            <Sidebar />


            <main className="expenses-main">

                {/* HEADER */}

                <header className="expenses-header">

                    <div>

                        <p className="expenses-eyebrow">
                            PERSONAL FINANCE
                        </p>

                        <h1>
                            Expenses
                        </h1>

                        <p className="expenses-subtitle">
                            Track and manage where your money goes.
                        </p>

                    </div>


                    <button
                        className="add-expense-button"
                        onClick={openAddModal}
                    >
                        + Add expense
                    </button>

                </header>


                {/* SUMMARY */}

                <section className="expense-summary">

                    <div className="expense-summary-card">

                        <p>
                            Total expenses
                        </p>

                        <h2>
                            ₹{totalExpense.toLocaleString("en-IN")}
                        </h2>

                        <span>
                            All time
                        </span>

                    </div>


                    <div className="expense-summary-card">

                        <p>
                            Highest category
                        </p>

                        <h2>
                            {highestCategory}
                        </h2>

                        <span>
                            ₹
                            {highestCategoryAmount.toLocaleString(
                                "en-IN"
                            )}
                        </span>

                    </div>


                    <div className="expense-summary-card">

                        <p>
                            Transactions
                        </p>

                        <h2>
                            {expenses.length}
                        </h2>

                        <span>
                            Showing results
                        </span>

                    </div>

                </section>


                {/* EXPENSE PANEL */}

                <section className="expenses-panel">

                    <div className="expenses-panel-header">

                        <div>

                            <p className="panel-label">
                                TRANSACTIONS
                            </p>

                            <h3>
                                Recent expenses
                            </h3>

                        </div>


                        <div className="expense-actions">

                            <input
                                className="expense-search"
                                type="text"
                                placeholder="Search expenses..."
                                value={search}
                                onChange={(e) =>
                                    handleSearch(e.target.value)
                                }
                            />


                            <select
                                className="expense-filter"
                                value={selectedCategory}
                                onChange={(e) =>
                                    handleCategoryFilter(
                                        e.target.value
                                    )
                                }
                            >

                                <option>
                                    All categories
                                </option>

                                <option>
                                    Food & Dining
                                </option>

                                <option>
                                    Transportation
                                </option>

                                <option>
                                    Shopping
                                </option>

                                <option>
                                    Subscriptions
                                </option>

                                <option>
                                    Entertainment
                                </option>

                                <option>
                                    Health
                                </option>

                                <option>
                                    Other
                                </option>

                            </select>

                        </div>

                    </div>


                    {/* LIST */}

                    <div className="expense-list">

                        {loading ? (

                            <div className="empty-expenses">
                                Loading expenses...
                            </div>

                        ) : expenses.length === 0 ? (

                            <div className="empty-expenses">

                                <div>
                                    💸
                                </div>

                                <h3>
                                    No expenses found
                                </h3>

                                <p>
                                    Add your first expense to start
                                    tracking your spending.
                                </p>

                               

                            </div>

                        ) : (

                            expenses.map((expense) => {

                                const categoryData =
                                    getCategoryData(
                                        expense.category
                                    );

                                return (

                                    <div
                                        className="expense-row"
                                        key={expense.id}
                                    >

                                        <div
                                            className={`expense-icon ${categoryData.type}`}
                                        >
                                            {categoryData.icon}
                                        </div>


                                        <div className="expense-details">

                                            <strong>
                                                {expense.category}
                                            </strong>

                                            <span>
                                                {expense.title}
                                                {" • "}
                                                {formatDate(
                                                    expense.expenseDate
                                                )}
                                            </span>

                                        </div>


                                        <div className="expense-amount">

                                            -₹
                                            {Number(
                                                expense.amount
                                            ).toLocaleString(
                                                "en-IN"
                                            )}

                                        </div>


                                        <div className="expense-row-actions">

                                            <button
                                                className="expense-edit-button"
                                                onClick={() =>
                                                    openEditModal(
                                                        expense
                                                    )
                                                }
                                            >
                                                Edit
                                            </button>


                                            <button
                       className="expense-delete-button"
                onClick={() =>
                  handleDelete(
                              expense.id
                                     )
                                }
                 >
                          Delete
                                            </button>

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
                        className="expense-modal-overlay"
                        onClick={closeModal}
                    >

                        <div
                            className="expense-modal"
                            onClick={(e) =>
                                e.stopPropagation()
                            }
                        >

                            <div className="expense-modal-header">

                                <div>

                                    <p className="panel-label">
                                        {editingExpense
                                            ? "UPDATE TRANSACTION"
                                            : "NEW TRANSACTION"}
                                    </p>

                                    <h2>
                                        {editingExpense
                                            ? "Edit expense"
                                            : "Add expense"}
                                    </h2>

                                </div>


                                <button
                                    className="modal-close"
                                    onClick={closeModal}
                                >
                                    ×
                                </button>

                            </div>


                            <form onSubmit={handleSubmit}>

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


                                {/* CATEGORY */}

                                <div className="modal-form-group">

                                    <label>
                                        Category
                                    </label>

                                    <select
                                        value={category}
                                        onChange={(e) =>
                                            setCategory(
                                                e.target.value
                                            )
                                        }
                                    >

                                        <option>
                                            Food & Dining
                                        </option>

                                        <option>
                                            Transportation
                                        </option>

                                        <option>
                                            Shopping
                                        </option>

                                        <option>
                                            Subscriptions
                                        </option>

                                        <option>
                                            Entertainment
                                        </option>

                                        <option>
                                            Health
                                        </option>

                                        <option>
                                            Other
                                        </option>

                                    </select>

                                </div>


                                {/* DESCRIPTION */}

                                <div className="modal-form-group">

                                    <label>
                                        Description
                                    </label>

                                    <input
                                        type="text"
                                        placeholder="What did you spend on?"
                                        value={description}
                                        onChange={(e) =>
                                            setDescription(
                                                e.target.value
                                            )
                                        }
                                        required
                                    />

                                </div>


                                {/* DATE */}

                                <div className="modal-form-group">

                                    <label>
                                        Date
                                    </label>

                                    <input
                                        type="date"
                                        value={date}
                                        onChange={(e) =>
                                            setDate(
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
                                        className="save-expense-button"
                                    >
                                        {editingExpense
                                            ? "Update expense"
                                            : "Add expense"}
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

export default Expenses;