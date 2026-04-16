/**
 * Kaasu Creator - Frontend JavaScript
 */

// Helper function to show temporary alerts
function showAlert(message, type = 'success') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type}`;
    alertDiv.textContent = message;

    const container = document.querySelector('.container');
    container.insertBefore(alertDiv, container.firstChild);

    setTimeout(() => alertDiv.remove(), 4000);
}

// ===== BUDGET PAGE =====
// Simple bar chart for expense categories
function renderCategoryChart(expenses) {
    // Group expenses by category
    const categoryTotals = {};
    expenses.forEach(exp => {
        categoryTotals[exp.category] = (categoryTotals[exp.category] || 0) + exp.amount;
    });

    const chartContainer = document.getElementById('category-chart');
    if (!chartContainer) return;

    // Find max for scaling
    const max = Math.max(...Object.values(categoryTotals));

    let chartHTML = '<div style="display:flex;gap:10px;align-items:end;height:150px;margin-top:20px;">';
    const colors = ['#e94560', '#7b2cbf', '#00d9ff', '#ff9100', '#00c853', '#ff3d00'];

    let i = 0;
    for (const [category, amount] of Object.entries(categoryTotals)) {
        const heightPercent = (amount / max) * 100;
        const color = colors[i % colors.length];
        chartHTML += `
            <div style="flex:1;text-align:center;">
                <div style="background:${color};height:${heightPercent}%;border-radius:4px 4px 0 0;min-height:10px;"
                     title="${category}: $${amount.toFixed(2)}"></div>
                <div style="font-size:0.75rem;margin-top:5px;color:#b0b0b0;">${category}</div>
                <div style="font-size:0.85rem;color:white;">$${amount.toFixed(0)}</div>
            </div>
        `;
        i++;
    }
    chartHTML += '</div>';
    chartContainer.innerHTML = chartHTML;
}

// ===== CALCULATOR PAGE =====
// Handle calculator type switching
function switchCalculator(type) {
    const sections = document.querySelectorAll('.calc-section');
    sections.forEach(section => {
        section.style.display = section.id === `calc-${type}` ? 'block' : 'none';
    });
}

// ===== GOAL PAGE =====
// Calculate and display progress for goals
function updateGoalProgress(goalId, targetAmount, currentAmount) {
    const progress = Math.min((currentAmount / targetAmount) * 100, 100);
    const progressBar = document.querySelector(`#goal-${goalId} .progress-fill`);
    const progressText = document.querySelector(`#goal-${goalId} .progress-text`);

    if (progressBar) {
        progressBar.style.width = `${progress}%`;
    }
    if (progressText) {
        progressText.textContent = `$${currentAmount.toFixed(2)} / $${targetAmount.toFixed(2)} (${progress.toFixed(1)}%)`;
    }
}

// ===== KAASU-MASCOT INTERACTION =====
// Toggle speech bubble
document.addEventListener('DOMContentLoaded', () => {
    const mascot = document.querySelector('.kaasu-mascot');
    if (mascot) {
        mascot.addEventListener('click', () => {
            const speech = mascot.querySelector('.kaasu-speech');
            speech.style.display = speech.style.display === 'block' ? 'none' : 'block';
        });
    }

    // For forms, add confirmation before submit
    const forms = document.querySelectorAll('form[data-confirm]');
    forms.forEach(form => {
        form.addEventListener('submit', (e) => {
            if (!confirm(form.dataset.confirm)) {
                e.preventDefault();
            }
        });
    });
});

// ===== EXPENSE DELETION =====
// Confirm before deleting
function confirmDelete(expenseId) {
    if (confirm('Delete this expense? This cannot be undone.')) {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '/budget/delete';

        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = 'expenseId';
        input.value = expenseId;

        form.appendChild(input);
        document.body.appendChild(form);
        form.submit();
    }
}

// ===== ROADMAP CHECKBOX =====
function markWeekComplete(roadmapId, checked) {
    // This could be an AJAX call to update status
    console.log(`Week ${roadmapId} marked as ${checked ? 'complete' : 'pending'}`);
}