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
// Pie chart for expense categories
const PIE_COLORS = [
    '#e879a0', '#d45f88', '#f4a0c0',
    '#9b59b6', '#c0392b', '#e67e22',
    '#27ae60', '#3498db', '#f39c12'
];

function renderPieChart(expenses) {
    const canvas = document.getElementById('pieChart');
    if (!canvas) return;

    const totals = {};
    expenses.forEach(e => {
        totals[e.category] = (totals[e.category] || 0) + e.amount;
    });

    const entries = Object.entries(totals);
    const grand   = entries.reduce((s, [, v]) => s + v, 0);
    if (grand === 0) return;

    const ctx = canvas.getContext('2d');
    const cx  = canvas.width  / 2;
    const cy  = canvas.height / 2;
    const r   = Math.min(cx, cy) - 16;

    ctx.clearRect(0, 0, canvas.width, canvas.height);

    let startAngle = -Math.PI / 2;
    entries.forEach(([, amt], i) => {
        const slice = (amt / grand) * 2 * Math.PI;
        ctx.beginPath();
        ctx.moveTo(cx, cy);
        ctx.arc(cx, cy, r, startAngle, startAngle + slice);
        ctx.closePath();
        ctx.fillStyle   = PIE_COLORS[i % PIE_COLORS.length];
        ctx.strokeStyle = '#080808';
        ctx.lineWidth   = 2;
        ctx.fill();
        ctx.stroke();
        startAngle += slice;
    });

    // Center hole for donut effect
    ctx.beginPath();
    ctx.arc(cx, cy, r * 0.48, 0, 2 * Math.PI);
    ctx.fillStyle = '#111111';
    ctx.fill();

    // Center label
    ctx.fillStyle    = '#f0f0f0';
    ctx.font         = 'bold 13px Segoe UI, sans-serif';
    ctx.textAlign    = 'center';
    ctx.textBaseline = 'middle';
    ctx.fillText(entries.length + ' categories', cx, cy);

    // Build legend
    const legend = document.getElementById('pie-legend');
    if (!legend) return;
    legend.innerHTML = entries.map(([cat, amt], i) => `
        <div class="b-legend-row">
            <span class="b-legend-dot" style="background:${PIE_COLORS[i % PIE_COLORS.length]}"></span>
            <span class="b-legend-cat">${cat}</span>
            <span class="b-legend-pct">${((amt / grand) * 100).toFixed(0)}%</span>
            <span class="b-legend-amt">$${amt.toFixed(2)}</span>
        </div>
    `).join('');
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