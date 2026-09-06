import { useEffect, useState } from "react";
import Sidebar from "../components/Sidebar";

import {
    getExpenses,
    getTotalExpense,
    getCategorySummary,
    getTotalIncome,
} from "../services/api";

import "./Dashboard.css";

function Dashboard() {
    const dashboardLayout =
    localStorage.getItem("velora_dashboard_layout") || "comfortable";

    // =========================
    // STATE
    // =========================

    const [expenses, setExpenses] = useState([]);

    const [totalExpense, setTotalExpense] = useState(0);

    const [totalIncome, setTotalIncome] = useState(0);

    const [highestCategory, setHighestCategory] =
        useState("No expenses");

    const [highestCategoryAmount, setHighestCategoryAmount] =
        useState(0);

    const [loading, setLoading] = useState(true);


    // =========================
    // LOAD DASHBOARD DATA
    // =========================

    useEffect(() => {
        loadDashboard();
    }, []);


    const loadDashboard = async () => {

        try {

            setLoading(true);


            // =========================
            // EXPENSES
            // =========================

            const expenseData = await getExpenses();

            setExpenses(expenseData || []);


            // =========================
            // TOTAL EXPENSE
            // =========================

            const total = await getTotalExpense();

            setTotalExpense(
                Number(total) || 0
            );


            // =========================
            // TOTAL INCOME
            // =========================

            const income = await getTotalIncome();

            setTotalIncome(
                Number(income) || 0
            );


            // =========================
            // CATEGORY SUMMARY
            // =========================

            const summary =
                await getCategorySummary();


            if (
                summary &&
                summary.length > 0
            ) {

                const highest =
                    summary.reduce(
                        (max, current) =>
                            Number(current.amount) >
                            Number(max.amount)
                                ? current
                                : max
                    );


                setHighestCategory(
                    highest.category
                );


                setHighestCategoryAmount(
                    Number(highest.amount)
                );

            } else {

                setHighestCategory(
                    "No expenses"
                );

                setHighestCategoryAmount(0);

            }

        } catch (error) {

            console.error(
                "Failed to load dashboard:",
                error
            );

        } finally {

            setLoading(false);

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
    // RECENT EXPENSES
    // =========================

    const recentExpenses =
        [...expenses]
            .sort(
                (a, b) =>
                    new Date(b.expenseDate) -
                    new Date(a.expenseDate)
            )
            .slice(0, 4);


    // =========================
    // MONTHLY EXPENSE DATA
    // =========================

    const monthlyExpenses = {};


    expenses.forEach((expense) => {

        if (!expense.expenseDate) {
            return;
        }


        const date =
            new Date(expense.expenseDate);


        const month =
            date.toLocaleDateString(
                "en-IN",
                {
                    month: "short",
                }
            );


        monthlyExpenses[month] =
            (monthlyExpenses[month] || 0) +
            Number(expense.amount || 0);

    });


    const chartMonths = [
    "Jan",
    "Feb",
    "Mar",
    "Apr",
    "May",
    "Jun",
    "Jul",
    "Aug",
    "Sep",
    "Oct",
    "Nov",
    "Dec",
];


    const chartValues =
        chartMonths.map(
            (month) =>
                monthlyExpenses[month] || 0
        );


    const maxChartValue =
        Math.max(...chartValues, 1);


    // =========================
    // SAVINGS
    // =========================

    const savings =
        totalIncome - totalExpense;


    // =========================
    // UI
    // =========================

    return (

        <div className={`dashboard-layout ${dashboardLayout}`}>

            <Sidebar />


            <main className="dashboard-main">


                {/* =========================
                    HEADER
                ========================= */}

                <header className="dashboard-header">

                    <div>

                        <p className="dashboard-eyebrow">
                            PERSONAL FINANCE
                        </p>


                        <h1>
                            Good morning 👋
                        </h1>


                        <p className="dashboard-subtitle">
                            Here's what's happening with your money.
                        </p>

                    </div>


                    <div className="dashboard-date">
                        August 2026
                    </div>

                </header>



                {/* =========================
                    SUMMARY CARDS
                ========================= */}

                <section className="summary-grid">


                    {/* INCOME */}

                    <div className="summary-card">

                        <p>
                            Income
                        </p>


                        <h2>

                            ₹
                            {totalIncome.toLocaleString(
                                "en-IN"
                            )}

                        </h2>


                        <span>
                            Total recorded income
                        </span>

                    </div>



                    {/* HIGHEST CATEGORY */}

                    <div className="summary-card">

                        <p>
                            Highest category
                        </p>


                        <h2>

                            {loading
                                ? "..."
                                : highestCategory}

                        </h2>


                        <span>

                            ₹
                            {highestCategoryAmount.toLocaleString(
                                "en-IN"
                            )}

                        </span>

                    </div>



                    {/* EXPENSES */}

                    <div className="summary-card">

                        <p>
                            Expenses
                        </p>


                        <h2>

                            ₹
                            {totalExpense.toLocaleString(
                                "en-IN"
                            )}

                        </h2>


                        <span>
                            All time
                        </span>

                    </div>



                    {/* SAVINGS */}

                    <div className="summary-card">

                        <p>
                            Savings
                        </p>


                        <h2>

                            ₹
                            {savings.toLocaleString(
                                "en-IN"
                            )}

                        </h2>


                        <span>
                            Income − expenses
                        </span>

                    </div>


                </section>



                {/* =========================
                    DASHBOARD CONTENT
                ========================= */}

                <section className="dashboard-content">


                    {/* =========================
                        EXPENSE OVERVIEW
                    ========================= */}

                    <div className="dashboard-panel expense-panel">

                        <div className="panel-header">

                            <div>

                                <p className="panel-label">
                                    OVERVIEW
                                </p>


                                <h3>
                                    Expense overview
                                </h3>

                            </div>


                            <button>
                                Monthly ▾
                            </button>

                        </div>



                        {/* EXPENSE CHART */}

                        <div className="expense-chart">


                            <div className="chart-values">

                                <span>

                                    ₹
                                    {Math.round(
                                        maxChartValue
                                    ).toLocaleString(
                                        "en-IN"
                                    )}

                                </span>


                                <span>

                                    ₹
                                    {Math.round(
                                        maxChartValue * 0.66
                                    ).toLocaleString(
                                        "en-IN"
                                    )}

                                </span>


                                <span>

                                    ₹
                                    {Math.round(
                                        maxChartValue * 0.33
                                    ).toLocaleString(
                                        "en-IN"
                                    )}

                                </span>


                                <span>
                                    ₹0
                                </span>

                            </div>



                            <div className="chart-area">


                                <div className="chart-grid">

                                    <span></span>
                                    <span></span>
                                    <span></span>
                                    <span></span>

                                </div>



                                <div className="chart-bars">

                                    {chartValues.map(
                                        (value, index) => (

                                            <div
                                                key={index}
                                                className="chart-bar"
                                                style={{
                                                    height:
                                                        `${(value / maxChartValue) * 100}%`
                                                }}
                                            >
                                            </div>

                                        )
                                    )}

                                </div>



                                <div className="chart-labels">

                                    {chartMonths.map(
                                        (month) => (

                                            <span
                                                key={month}
                                            >
                                                {month}
                                            </span>

                                        )
                                    )}

                                </div>


                            </div>

                        </div>

                    </div>



                    {/* =========================
                        RECENT EXPENSES
                    ========================= */}

                    <div className="dashboard-panel">

                        <div className="panel-header">

                            <div>

                                <p className="panel-label">
                                    BREAKDOWN
                                </p>


                                <h3>
                                    Recent expenses
                                </h3>

                            </div>

                        </div>



                        {loading ? (

                            <p>
                                Loading expenses...
                            </p>

                        ) : recentExpenses.length === 0 ? (

                            <p>
                                No expenses yet.
                            </p>

                        ) : (

                            recentExpenses.map(
                                (expense) => (

                                    <div
                                        className="expense-item"
                                        key={expense.id}
                                    >

                                        <div>

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


                                        <b>

                                            ₹
                                            {Number(
                                                expense.amount
                                            ).toLocaleString(
                                                "en-IN"
                                            )}

                                        </b>

                                    </div>

                                )
                            )

                        )}

                    </div>


                </section>


            </main>

        </div>

    );

}


export default Dashboard;