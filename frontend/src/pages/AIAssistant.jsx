import Sidebar from "../components/Sidebar";
import "./AIAssistant.css";
import { useState } from "react";


// ==========================================
// FORMAT AI RESPONSE
// ==========================================

// ==========================================
// FORMAT AI RESPONSE
// ==========================================

function renderInlineText(text) {

    const parts = text.split(/(\*\*.*?\*\*)/g);

    return parts.map((part, index) => {

        if (
            part.startsWith("**") &&
            part.endsWith("**")
        ) {
            return (
                <strong key={index}>
                    {part.slice(2, -2)}
                </strong>
            );
        }

        return part;
    });
}


function formatAIText(text) {

    if (!text) {
        return null;
    }

    return text
        .split("\n")
        .map((line, index) => {

            const trimmedLine = line.trim();

            // Empty line
            if (!trimmedLine) {
                return (
                    <div
                        key={index}
                        className="ai-line-space"
                    />
                );
            }


            // Bullet point
            if (
                trimmedLine.startsWith("* ") ||
                trimmedLine.startsWith("- ")
            ) {

                const bulletText =
                    trimmedLine.substring(2);

                return (
                    <div
                        key={index}
                        className="ai-response-bullet"
                    >
                        <span className="ai-bullet-dot">
                            •
                        </span>

                        <span>
                            {renderInlineText(
                                bulletText
                            )}
                        </span>
                    </div>
                );
            }


            // Normal paragraph
            return (
                <p key={index}>
                    {renderInlineText(
                        trimmedLine
                    )}
                </p>
            );
        });
}
function AIAssistant() {

    const [messages, setMessages] = useState([
        {
            type: "bot",
            text: "Hi! I'm your Velora AI assistant. I can help you understand your spending, budgets and subscriptions.\n\nWhat would you like to know about your finances?"
        }
    ]);

    const [question, setQuestion] = useState("");
    const [loading, setLoading] = useState(false);


    // ==========================================
    // ASK GEMINI
    // ==========================================

    const askAI = async (text) => {

        const userQuestion = text.trim();

        if (!userQuestion || loading) {
            return;
        }

        // Add user's message
        setMessages(prev => [
            ...prev,
            {
                type: "user",
                text: userQuestion
            }
        ]);

        setQuestion("");
        setLoading(true);

        try {

            const token = localStorage.getItem("token");

            const response = await fetch(
                "http://localhost:8081/api/ai/ask",
                {
                    method: "POST",

                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${token}`
                    },

                    body: JSON.stringify({
                        question: userQuestion
                    })
                }
            );


            if (!response.ok) {
                throw new Error(
                    `AI request failed: ${response.status}`
                );
            }


            const data = await response.json();


            setMessages(prev => [
                ...prev,
                {
                    type: "bot",
                    text: data.answer
                }
            ]);

        } catch (error) {

            console.error(
                "AI request failed:",
                error
            );

            setMessages(prev => [
                ...prev,
                {
                    type: "bot",
                    text: "Sorry, I couldn't connect to Velora AI. Please try again."
                }
            ]);

        } finally {

            setLoading(false);
        }
    };


    // ==========================================
    // FORM SUBMIT
    // ==========================================

    const handleSubmit = (e) => {

        e.preventDefault();

        askAI(question);
    };


    // ==========================================
    // SUGGESTION
    // ==========================================

    const handleSuggestion = (text) => {

        askAI(text);
    };


    return (

        <div className="ai-layout">

            <Sidebar />


            <main className="ai-main">

                {/* HEADER */}

                <header className="ai-header">

                    <div>

                        <p className="ai-eyebrow">
                            VELORA AI
                        </p>

                        <h1>
                            AI Assistant
                        </h1>

                        <p className="ai-subtitle">
                            Understand your money with personalized insights.
                        </p>

                    </div>


                    <div className="ai-status">

                        <span className="ai-status-dot"></span>

                        AI Assistant

                    </div>

                </header>


                {/* CONTENT */}

                <section className="ai-content">


                    {/* CHAT */}

                    <div className="ai-chat-panel">


                        {/* CHAT HEADER */}

                        <div className="ai-chat-header">

                            <div className="ai-avatar">
                                ✦
                            </div>

                            <div>

                                <h2>
                                    Velora AI
                                </h2>

                                <p>
                                    Your personal finance assistant
                                </p>

                            </div>

                        </div>


                        {/* MESSAGES */}

                        <div className="ai-messages">

                            {messages.map(
                                (message, index) => (

                                    <div
                                        key={index}
                                        className={`ai-message ${
                                            message.type === "user"
                                                ? "ai-message-user"
                                                : "ai-message-bot"
                                        }`}
                                    >

                                        {message.type === "bot" && (

                                            <div className="message-avatar">
                                                ✦
                                            </div>

                                        )}


                                      <div className="message-bubble">
             {formatAIText(message.text)}
                     </div>

                                    </div>

                                )
                            )}


                            {/* LOADING */}

                            {loading && (

                                <div className="ai-message ai-message-bot">

                                    <div className="message-avatar">
                                        ✦
                                    </div>

                                    <div className="message-bubble">

                                        <p>
                                            Velora AI is thinking...
                                        </p>

                                    </div>

                                </div>

                            )}


                            {/* SUGGESTIONS */}

                            {messages.length === 1 && !loading && (

                                <div className="ai-suggestions">

                                    <button
                                        onClick={() =>
                                            handleSuggestion(
                                                "Where am I spending the most?"
                                            )
                                        }
                                    >
                                        Where am I spending the most?
                                    </button>


                                    <button
                                        onClick={() =>
                                            handleSuggestion(
                                                "How much did I spend this month?"
                                            )
                                        }
                                    >
                                        How much did I spend this month?
                                    </button>


                                    <button
                                        onClick={() =>
                                            handleSuggestion(
                                                "Am I over my budget?"
                                            )
                                        }
                                    >
                                        Am I over my budget?
                                    </button>


                                    <button
                                        onClick={() =>
                                            handleSuggestion(
                                                "Give me saving suggestions"
                                            )
                                        }
                                    >
                                        Give me saving suggestions
                                    </button>

                                </div>

                            )}

                        </div>


                        {/* INPUT */}

                        <form
                            className="ai-input-area"
                            onSubmit={handleSubmit}
                        >

                            <input
                                type="text"
                                placeholder="Ask Velora AI about your finances..."
                                value={question}
                                onChange={(e) =>
                                    setQuestion(e.target.value)
                                }
                                disabled={loading}
                            />


                            <button
                                type="submit"
                                className="ai-send-btn"
                                disabled={
                                    loading ||
                                    !question.trim()
                                }
                            >
                                →
                            </button>

                        </form>


                        <p className="ai-disclaimer">

                            Velora AI provides financial insights based on your
                            account data and should not be considered professional
                            financial advice.

                        </p>

                    </div>


                    {/* QUICK INSIGHTS */}

                    <aside className="ai-insights-panel">

                        <div className="insights-header">

                            <p className="ai-panel-label">
                                QUICK INSIGHTS
                            </p>

                            <h2>
                                What I can help with
                            </h2>

                        </div>


                        <div
    className="insight-card"
    onClick={() =>
        handleSuggestion("Where am I spending the most?")
    }
>

                            <div className="insight-icon">
                                ₹
                            </div>

                            <div>

                                <h3>
                                    Spending analysis
                                </h3>

                                <p>
                                    Find your biggest spending categories
                                    and patterns.
                                </p>

                            </div>

                        </div>


                        <div
    className="insight-card"
    onClick={() =>
        handleSuggestion("Am I over my budget?")
    }
>

                            <div className="insight-icon">
                                ◷
                            </div>

                            <div>

                                <h3>
                                    Budget guidance
                                </h3>

                                <p>
                                    Understand whether your spending is
                                    staying within your budgets.
                                </p>

                            </div>

                        </div>


                        <div
    className="insight-card"
    onClick={() =>
        handleSuggestion("How much am I spending on subscriptions?")
    }
>

                            <div className="insight-icon">
                                ↻
                            </div>

                            <div>

                                <h3>
                                    Subscription insights
                                </h3>

                                <p>
                                    Identify recurring payments and
                                    subscription costs.
                                </p>

                            </div>

                        </div>


                        <div
    className="insight-card"
    onClick={() =>
        handleSuggestion("Give me saving suggestions")
    }
>

                            <div className="insight-icon">
                                ↗
                            </div>

                            <div>

                                <h3>
                                    Savings insights
                                </h3>

                                <p>
                                    Get suggestions based on your income
                                    and expenses.
                                </p>

                            </div>

                        </div>

                    </aside>

                </section>

            </main>

        </div>
    );
}

export default AIAssistant;