import Sidebar from "../components/Sidebar";
import "./Reports.css";

import { useEffect, useState } from "react";

import {
    getExpenses,
    getTotalExpense,
    getTotalIncome,
    getCategorySummary,
    getIncomes
} from "../services/api";

function Reports() {

    const [expenses, setExpenses] = useState([]);
    const [incomes, setIncomes] = useState([]);

    const [totalIncome, setTotalIncome] = useState(0);
    const [totalExpense, setTotalExpense] = useState(0);

    const [categorySummary, setCategorySummary] = useState([]);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");


    // ==========================================
    // LOAD REPORT DATA
    // ==========================================

    useEffect(() => {

        const loadReports = async () => {

            try {

                setLoading(true);
                setError("");

                const [
                    expenseData,
                    expenseTotal,
                    incomeTotal,
                    categoryData,
                    incomeData
                ] = await Promise.all([

                    getExpenses(),

                    getTotalExpense(),

                    getTotalIncome(),

                    getCategorySummary(),

                    getIncomes()

                ]);


                setExpenses(
                    Array.isArray(expenseData)
                        ? expenseData
                        : []
                );


                setIncomes(
                    Array.isArray(incomeData)
                        ? incomeData
                        : []
                );


                setTotalExpense(
                    Number(expenseTotal) || 0
                );


                setTotalIncome(
                    Number(incomeTotal) || 0
                );


                setCategorySummary(
                    Array.isArray(categoryData)
                        ? categoryData
                        : []
                );


            } catch (err) {

                console.error(
                    "REPORT ERROR:",
                    err
                );

                setError(
                    "Unable to load report data."
                );

            } finally {

                setLoading(false);

            }

        };


        loadReports();

    }, []);


    // ==========================================
    // SAVINGS
    // ==========================================

    const savings =
        totalIncome - totalExpense;


    // ==========================================
    // EXPENSE PERCENTAGE
    // ==========================================

    const expensePercentage =
        totalIncome > 0
            ? (totalExpense / totalIncome) * 100
            : 0;


    // ==========================================
    // SAVINGS PERCENTAGE
    // ==========================================

    let savingsPercentage =
        totalIncome > 0
            ? (savings / totalIncome) * 100
            : 0;


    if (savingsPercentage < 0) {
        savingsPercentage = 0;
    }


    if (savingsPercentage > 100) {
        savingsPercentage = 100;
    }


    // ==========================================
    // CATEGORY DATA
    // ==========================================

    const maxCategoryAmount =
        categorySummary.length > 0
            ? Math.max(
                ...categorySummary.map(
                    item =>
                        Number(item.amount) || 0
                )
            )
            : 0;


    // ==========================================
    // LAST 6 MONTHS
    // ==========================================

    const getLastSixMonths = () => {

        const result = [];

        const today = new Date();


        for (let i = 5; i >= 0; i--) {

            const date = new Date(
                today.getFullYear(),
                today.getMonth() - i,
                1
            );


            result.push({

                month:
                    date.getMonth(),

                year:
                    date.getFullYear(),

                label:
                    date.toLocaleString(
                        "en-US",
                        {
                            month: "short"
                        }
                    )

            });

        }


        return result;
    };


    const months =
        getLastSixMonths();


    // ==========================================
    // MONTHLY EXPENSE
    // ==========================================

    const getMonthlyExpense = (
        month,
        year
    ) => {

        return expenses

            .filter(expense => {

                if (!expense.expenseDate) {
                    return false;
                }


                const date =
                    new Date(
                        expense.expenseDate
                    );


                return (
                    date.getMonth() === month
                    &&
                    date.getFullYear() === year
                );

            })

            .reduce(
                (total, expense) =>
                    total +
                    (
                        Number(
                            expense.amount
                        ) || 0
                    ),
                0
            );

    };


    // ==========================================
    // MONTHLY INCOME
    // ==========================================

    const getMonthlyIncome = (
        month,
        year
    ) => {

        return incomes

            .filter(income => {

                if (!income.incomeDate) {
                    return false;
                }


                const date =
                    new Date(
                        income.incomeDate
                    );


                return (
                    date.getMonth() === month
                    &&
                    date.getFullYear() === year
                );

            })

            .reduce(
                (total, income) =>
                    total +
                    (
                        Number(
                            income.amount
                        ) || 0
                    ),
                0
            );

    };


    // ==========================================
    // MONTHLY CHART DATA
    // ==========================================

    const monthlyData =
        months.map(month => ({

            ...month,

            income:
                getMonthlyIncome(
                    month.month,
                    month.year
                ),

            expense:
                getMonthlyExpense(
                    month.month,
                    month.year
                )

        }));


    // ==========================================
    // MAX CHART VALUE
    // ==========================================

    const maxChartValue =
        Math.max(
            1,

            ...monthlyData.map(
                item =>
                    Math.max(
                        item.income,
                        item.expense
                    )
            )
        );


    // ==========================================
    // CURRENT MONTH LABEL
    // ==========================================

    const currentMonth =
        new Date().toLocaleString(
            "en-US",
            {
                month: "long",
                year: "numeric"
            }
        );


    // ==========================================
    // LOADING
    // ==========================================

    if (loading) {

        return (

            <div className="reports-layout">

                <Sidebar />

                <main className="reports-main">

                    <div className="reports-loading">
                        Loading reports...
                    </div>

                </main>

            </div>

        );

    }


    // ==========================================
    // RENDER
    // ==========================================

    return (

        <div className="reports-layout">

            <Sidebar />


            <main className="reports-main">


                {/* HEADER */}

                <header className="reports-header">

                    <div>

                        <p className="reports-eyebrow">
                            FINANCIAL INSIGHTS
                        </p>


                        <h1>
                            Reports
                        </h1>


                        <p className="reports-subtitle">

                            Understand your spending
                            patterns and financial progress.

                        </p>

                    </div>


                    <button
                        className="report-period-btn"
                        type="button"
                    >

                        {currentMonth}

                    </button>

                </header>


                {/* ERROR */}

                {error && (

                    <div className="reports-error">
                        {error}
                    </div>

                )}


                {/* SUMMARY */}

                <section className="report-summary">


                    <div className="report-card">

                        <span>
                            Total income
                        </span>


                        <strong>

                            ₹
                            {totalIncome.toLocaleString(
                                "en-IN",
                                {
                                    maximumFractionDigits: 0
                                }
                            )}

                        </strong>


                        <small className="report-positive">

                            Total recorded income

                        </small>

                    </div>


                    <div className="report-card">

                        <span>
                            Total expenses
                        </span>


                        <strong>

                            ₹
                            {totalExpense.toLocaleString(
                                "en-IN",
                                {
                                    maximumFractionDigits: 0
                                }
                            )}

                        </strong>


                        <small>

                            {expensePercentage.toFixed(1)}
                            % of income

                        </small>

                    </div>


                    <div className="report-card">

                        <span>
                            Total savings
                        </span>


                        <strong>

                            ₹
                            {savings.toLocaleString(
                                "en-IN",
                                {
                                    maximumFractionDigits: 0
                                }
                            )}

                        </strong>


                        <small
                            className={
                                savings >= 0
                                    ? "report-positive"
                                    : ""
                            }
                        >

                            {savingsPercentage.toFixed(1)}
                            % of income

                        </small>

                    </div>

                </section>


                {/* CATEGORY + SAVINGS */}

                <section className="reports-grid">


                    {/* CATEGORY REPORT */}

                    <div className="reports-panel">


                        <div className="reports-panel-header">

                            <div>

                                <p className="panel-label">
                                    SPENDING
                                </p>


                                <h2>
                                    Expense by category
                                </h2>

                            </div>

                        </div>


                        <div className="category-report">


                            {categorySummary.length === 0 ? (

                                <p className="report-empty">
                                    No expense data available.
                                </p>

                            ) : (

                                categorySummary.map(
                                    (item) => {


                                        const category =
                                            item.category;


                                        const numericAmount =
                                            Number(item.amount) || 0;


                                        const width =
                                            maxCategoryAmount > 0
                                                ? (
                                                    numericAmount /
                                                    maxCategoryAmount
                                                ) * 100
                                                : 0;


                                        return (

                                            <div
                                                key={category}
                                                className="category-group"
                                            >


                                                <div className="category-row">


                                                    <div className="category-name">

                                                        <span className="category-dot">
                                                        </span>


                                                        {category}

                                                    </div>


                                                    <strong>

                                                        ₹
                                                        {numericAmount
                                                            .toLocaleString(
                                                                "en-IN"
                                                            )}

                                                    </strong>


                                                </div>



                                                <div className="category-bar">

                                                    <div
                                                        style={{
                                                            width:
                                                                `${width}%`
                                                        }}
                                                    >
                                                    </div>

                                                </div>


                                            </div>

                                        );

                                    }
                                )

                            )}


                        </div>

                    </div>


                    {/* SAVINGS */}

                    <div className="reports-panel savings-panel">


                        <div className="reports-panel-header">

                            <div>

                                <p className="panel-label">
                                    SAVINGS
                                </p>


                                <h2>
                                    Savings progress
                                </h2>

                            </div>

                        </div>



                        <div
                            className="savings-circle"

                            style={{

                                background:
                                    `conic-gradient(
                                        var(--velora-green)
                                        ${savingsPercentage}%,
                                        #e8e5dc
                                        ${savingsPercentage}%
                                    )`

                            }}
                        >

                            <div>

                                <strong>

                                    {savingsPercentage.toFixed(0)}
                                    %

                                </strong>


                                <span>
                                    saved
                                </span>

                            </div>

                        </div>



                        <div className="savings-info">


                            <div>

                                <span>
                                    Income
                                </span>


                                <strong>

                                    ₹
                                    {totalIncome
                                        .toLocaleString(
                                            "en-IN"
                                        )}

                                </strong>

                            </div>



                            <div>

                                <span>
                                    Expenses
                                </span>


                                <strong>

                                    ₹
                                    {totalExpense
                                        .toLocaleString(
                                            "en-IN"
                                        )}

                                </strong>

                            </div>



                            <div>

                                <span>
                                    Savings
                                </span>


                                <strong>

                                    ₹
                                    {savings
                                        .toLocaleString(
                                            "en-IN"
                                        )}

                                </strong>

                            </div>


                        </div>

                    </div>

                </section>


                {/* MONTHLY CHART */}

                <section className="reports-panel monthly-panel">


                    <div className="reports-panel-header">

                        <div>

                            <p className="panel-label">
                                MONTHLY TREND
                            </p>


                            <h2>
                                Income vs expenses
                            </h2>

                        </div>

                    </div>



                    <div className="monthly-chart">


                        <div className="monthly-bars">


                            {monthlyData.map(
                                item => {


                                    const incomeHeight =
                                        (
                                            item.income /
                                            maxChartValue
                                        ) * 100;


                                    const expenseHeight =
                                        (
                                            item.expense /
                                            maxChartValue
                                        ) * 100;


                                    return (

                                        <div
                                            className="month-column"
                                            key={
                                                `${item.month}-${item.year}`
                                            }
                                        >


                                            <div
                                                className="income-bar"

                                                style={{
                                                    height:
                                                        `${incomeHeight}%`
                                                }}

                                                title={
                                                    `Income ₹${item.income}`
                                                }
                                            >
                                            </div>


                                            <div
                                                className="expense-bar"

                                                style={{
                                                    height:
                                                        `${expenseHeight}%`
                                                }}

                                                title={
                                                    `Expense ₹${item.expense}`
                                                }
                                            >
                                            </div>


                                            <span>
                                                {item.label}
                                            </span>


                                        </div>

                                    );

                                }
                            )}


                        </div>



                        <div className="chart-legend">


                            <span>

                                <i className="income-dot">
                                </i>

                                Income

                            </span>


                            <span>

                                <i className="expense-dot">
                                </i>

                                Expenses

                            </span>


                        </div>

                    </div>

                </section>


            </main>

        </div>

    );

}

export default Reports;