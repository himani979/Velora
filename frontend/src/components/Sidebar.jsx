import { Link, useLocation, useNavigate } from "react-router-dom";
import {
    Bot,
    LayoutDashboard,
    Receipt,
    WalletCards,
    RefreshCw,
    BarChart3,
    Settings,
    LogOut,
    Banknote,
} from "lucide-react";

import "./Sidebar.css";

function Sidebar() {

    const location = useLocation();
    const navigate = useNavigate();
    const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/login");
};

    return (
        <aside className="sidebar">

            {/* LOGO */}
            <div className="sidebar-logo">

                <h2>Velora</h2>

                <span>
                    Personal Finance
                </span>

            </div>


            {/* NAVIGATION */}
            <nav className="sidebar-nav">

                {/* DASHBOARD */}
                <Link
                    to="/dashboard"
                    className={`sidebar-link ${
                        location.pathname === "/dashboard"
                            ? "active"
                            : ""
                    }`}
                >
                    <LayoutDashboard
                        size={18}
                        strokeWidth={1.8}
                    />

                    <span>
                        Dashboard
                    </span>

                </Link>


                {/* EXPENSES */}
                <Link
                    to="/expenses"
                    className={`sidebar-link ${
                        location.pathname === "/expenses"
                            ? "active"
                            : ""
                    }`}
                >
                    <Receipt
                        size={18}
                        strokeWidth={1.8}
                    />

                    <span>
                        Expenses
                    </span>

                </Link>


                {/* INCOME */}
                <Link
                    to="/income"
                    className={`sidebar-link ${
                        location.pathname === "/income"
                            ? "active"
                            : ""
                    }`}
                >
                    <Banknote
                        size={18}
                        strokeWidth={1.8}
                    />

                    <span>
                        Income
                    </span>

                </Link>


                {/* BUDGETS */}
                <Link
                    to="/budgets"
                    className={`sidebar-link ${
                        location.pathname === "/budgets"
                            ? "active"
                            : ""
                    }`}
                >
                    <WalletCards
                        size={18}
                        strokeWidth={1.8}
                    />

                    <span>
                        Budgets
                    </span>

                </Link>


                {/* SUBSCRIPTIONS */}
                <Link
                    to="/subscriptions"
                    className={`sidebar-link ${
                        location.pathname === "/subscriptions"
                            ? "active"
                            : ""
                    }`}
                >
                    <RefreshCw
                        size={18}
                        strokeWidth={1.8}
                    />

                    <span>
                        Subscriptions
                    </span>

                </Link>


                {/* REPORTS */}
                <Link
                    to="/reports"
                    className={`sidebar-link ${
                        location.pathname === "/reports"
                            ? "active"
                            : ""
                    }`}
                >
                    <BarChart3
                        size={18}
                        strokeWidth={1.8}
                    />

                    <span>
                        Reports
                    </span>

                </Link>

            </nav>


            {/* AI ASSISTANT */}
            <Link
                to="/ai-assistant"
                className={`sidebar-link ${
                    location.pathname === "/ai-assistant"
                        ? "active"
                        : ""
                }`}
            >
                <Bot size={19} />

                <span>
                    AI Assistant
                </span>

            </Link>


            {/* BOTTOM */}
            <div className="sidebar-bottom">

                {/* SETTINGS */}
                <Link
                    to="/settings"
                    className={`sidebar-link ${
                        location.pathname === "/settings"
                            ? "active"
                            : ""
                    }`}
                >
                    <Settings
                        size={18}
                        strokeWidth={1.8}
                    />

                    <span>
                        Settings
                    </span>

                </Link>


                {/* LOGOUT */}
                <button
    onClick={handleLogout}
    className="sidebar-link logout-link"
>
                    <LogOut
                        size={18}
                        strokeWidth={1.8}
                    />

                    <span>
                        Logout
                    </span>

               </button>

            </div>

        </aside>
    );
}

export default Sidebar;