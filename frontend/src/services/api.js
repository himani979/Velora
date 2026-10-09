const API_BASE_URL = "https://velora-backend-x8cc.onrender.com";

function getToken() {
    return localStorage.getItem("token");
}

function authHeaders() {
    return {
        Authorization: `Bearer ${getToken()}`,
    };
}


// =========================
// LOGIN
// =========================

export async function loginUser(email, password) {
    const response = await fetch(`${API_BASE_URL}/api/users/login`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            email,
            password,
        }),
    });

    const result = await response.text();

    console.log("LOGIN API RESPONSE:", result);

    if (!response.ok) {
        throw new Error("Login failed");
    }

    return result;
}

export async function verifyLoginOtp(email, otp) {
    const response = await fetch(
        `${API_BASE_URL}/api/users/verify-login-otp`, {
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

    console.log("VERIFY LOGIN OTP RESPONSE:", result);

    if (!response.ok) {
        throw new Error("OTP verification failed");
    }

    return result;
}

// =========================
// REGISTER
// =========================

export async function registerUser(user) {
    const response = await fetch(`${API_BASE_URL}/api/users/register`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(user),
    });

    const result = await response.text();

    console.log("REGISTER API RESPONSE:", result);

    if (!response.ok) {
        throw new Error(result || "Registration failed. Please try again.");
    }

    return result;
}


// =========================
// EXPENSES
// =========================

export async function getExpenses() {
    const response = await fetch(`${API_BASE_URL}/api/expenses`, {
        headers: authHeaders(),
    });

    if (!response.ok) {
        throw new Error("Failed to fetch expenses");
    }

    return await response.json();
}


export async function addExpense(expense) {
    const response = await fetch(`${API_BASE_URL}/api/expenses`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            ...authHeaders(),
        },
        body: JSON.stringify(expense),
    });

    if (!response.ok) {
        const errorText = await response.text();

        console.error("ADD EXPENSE ERROR:", response.status, errorText);

        throw new Error(
            `Failed to add expense (${response.status}): ${errorText}`
        );
    }

    return await response.json();
}


export async function updateExpense(id, expense) {
    const response = await fetch(`${API_BASE_URL}/api/expenses/${id}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            ...authHeaders(),
        },
        body: JSON.stringify(expense),
    });

    if (!response.ok) {
        throw new Error("Failed to update expense");
    }

    return await response.json();
}


export async function deleteExpense(id) {
    const response = await fetch(`${API_BASE_URL}/api/expenses/${id}`, {
        method: "DELETE",
        headers: authHeaders(),
    });

    if (!response.ok) {
        throw new Error("Failed to delete expense");
    }

    return await response.text();
}


export async function getTotalExpense() {
    const response = await fetch(`${API_BASE_URL}/api/expenses/total`, {
        headers: authHeaders(),
    });

    if (!response.ok) {
        throw new Error("Failed to fetch total expense");
    }

    return await response.json();
}


export async function getHighestExpense() {
    const response = await fetch(`${API_BASE_URL}/api/expenses/highest`, {
        headers: authHeaders(),
    });

    if (!response.ok) {
        throw new Error("Failed to fetch highest expense");
    }

    return await response.json();
}


export async function getCategorySummary() {
    const response = await fetch(
        `${API_BASE_URL}/api/expenses/category-summary`, {
            headers: authHeaders(),
        }
    );

    if (!response.ok) {
        throw new Error("Failed to fetch category summary");
    }

    return await response.json();
}
export async function getTotalIncome() {
    const response = await fetch(
        `${API_BASE_URL}/api/income/total`, {
            headers: authHeaders(),
        }
    );

    if (!response.ok) {
        throw new Error("Failed to fetch total income");
    }

    return await response.json();
}
// =========================
// GET ALL INCOME
// =========================

export async function getIncomes() {

    const response = await fetch(
        `${API_BASE_URL}/api/income`, {
            headers: authHeaders(),
        }
    );

    if (!response.ok) {
        throw new Error("Failed to fetch incomes");
    }

    return await response.json();
}


export async function searchExpenses(title) {
    const response = await fetch(
        `${API_BASE_URL}/api/expenses/search?title=${encodeURIComponent(title)}`, {
            headers: authHeaders(),
        }
    );

    if (!response.ok) {
        throw new Error("Failed to search expenses");
    }

    return await response.json();
}


export async function getExpensesByCategory(category) {
    const response = await fetch(
        `${API_BASE_URL}/api/expenses/category/${encodeURIComponent(category)}`, {
            headers: authHeaders(),
        }
    );

    if (!response.ok) {
        throw new Error("Failed to fetch category expenses");
    }

    return await response.json();
}
export async function updateIncome(income) {

    const response = await fetch(
        `${API_BASE_URL}/api/users/income?income=${encodeURIComponent(income)}`, {
            method: "PUT",
            headers: {
                ...authHeaders(),
            },
        }
    );

    const result = await response.text();

    console.log(
        "UPDATE INCOME RESPONSE:",
        response.status,
        result
    );

    if (!response.ok) {
        throw new Error(
            `Failed to update income (${response.status}): ${result}`
        );
    }

    return JSON.parse(result);
}
// =========================
// SUBSCRIPTIONS
// =========================

export async function getSubscriptions() {
    const response = await fetch(
        `${API_BASE_URL}/api/subscriptions`, {
            headers: authHeaders(),
        }
    );

    if (!response.ok) {
        const errorText = await response.text();

        console.error(
            "GET SUBSCRIPTIONS ERROR:",
            response.status,
            errorText
        );

        throw new Error(
            `Failed to fetch subscriptions (${response.status}): ${errorText}`
        );
    }

    return await response.json();
}


export async function addSubscription(subscription) {
    const response = await fetch(
        `${API_BASE_URL}/api/subscriptions`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                ...authHeaders(),
            },
            body: JSON.stringify(subscription),
        }
    );

    if (!response.ok) {
        const errorText = await response.text();

        console.error(
            "ADD SUBSCRIPTION ERROR:",
            response.status,
            errorText
        );

        throw new Error(
            `Failed to add subscription (${response.status}): ${errorText}`
        );
    }

    return await response.json();
}


export async function deleteSubscription(id) {
    const response = await fetch(
        `${API_BASE_URL}/api/subscriptions/${id}`, {
            method: "DELETE",
            headers: authHeaders(),
        }
    );

    if (!response.ok) {
        const errorText = await response.text();

        console.error(
            "DELETE SUBSCRIPTION ERROR:",
            response.status,
            errorText
        );

        throw new Error(
            `Failed to delete subscription (${response.status}): ${errorText}`
        );
    }

    return await response.text();
}


export async function toggleSubscriptionAutoPay(id) {
    const response = await fetch(
        `${API_BASE_URL}/api/subscriptions/${id}/autopay`, {
            method: "PUT",
            headers: authHeaders(),
        }
    );

    if (!response.ok) {
        const errorText = await response.text();

        console.error(
            "AUTOPAY ERROR:",
            response.status,
            errorText
        );

        throw new Error(
            `Failed to toggle AutoPay (${response.status}): ${errorText}`
        );
    }

    return await response.json();
}


export async function getUpcomingSubscriptions() {
    const response = await fetch(
        `${API_BASE_URL}/api/subscriptions/upcoming`, {
            headers: authHeaders(),
        }
    );

    if (!response.ok) {
        throw new Error("Failed to fetch upcoming subscriptions");
    }

    return await response.json();
}


export async function getSubscriptionReminders() {
    const response = await fetch(
        `${API_BASE_URL}/api/subscriptions/reminders`, {
            headers: authHeaders(),
        }
    );

    if (!response.ok) {
        throw new Error("Failed to fetch subscription reminders");
    }

    return await response.json();
}


export async function updateSubscription(id, subscription) {
    const response = await fetch(
        `${API_BASE_URL}/api/subscriptions/${id}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                ...authHeaders(),
            },
            body: JSON.stringify(subscription),
        }
    );

    if (!response.ok) {
        const errorText = await response.text();

        console.error(
            "UPDATE SUBSCRIPTION ERROR:",
            response.status,
            errorText
        );

        throw new Error(
            `Failed to update subscription (${response.status}): ${errorText}`
        );
    }

    return await response.json();
}