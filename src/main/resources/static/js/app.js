/**
 * Table Tennis Tournament Management JavaScript
 * Contains all client-side functionality for the application
 */

// Utility functions
function clearErrors() {
    document.querySelectorAll('.error').forEach(el => el.textContent = '');
    const messageEl = document.getElementById('message');
    if (messageEl) {
        messageEl.style.display = 'none';
    }
}

function showMessage(message, isSuccess = true) {
    const messageEl = document.getElementById('message');
    if (messageEl) {
        messageEl.textContent = message;
        messageEl.className = `message ${isSuccess ? 'success' : 'error'}`;
        messageEl.style.display = 'block';
    }
}

function handleResponse(result, response, successRedirect, defaultErrorMessage) {
    if (response.ok && result.success) {
        showMessage(result.message, true);
        if (successRedirect) {
            setTimeout(() => {
                window.location.href = successRedirect;
            }, 1500);
        }
    } else {
        showMessage(result.message || defaultErrorMessage, false);
    }
}

function handleError(error, errorMessage) {
    console.error('Error:', error);
    showMessage(errorMessage, false);
}

// Registration form handling
function initializeRegistrationForm() {
    const form = document.getElementById('registrationForm');
    if (!form) return;

    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        clearErrors();

        const formData = {
            firstName: document.getElementById('firstName').value,
            surname: document.getElementById('surname').value,
            email: document.getElementById('email').value,
            tournamentId: parseInt(document.getElementById('tournamentId').value)
        };

        try {
            const response = await fetch('/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData)
            });

            const result = await response.json();
            handleResponse(result, response, '/success', 'Registration failed');
        } catch (error) {
            handleError(error, 'An error occurred. Please try again.');
        }
    });
}

// Create tournament form handling
function initializeCreateTournamentForm() {
    const form = document.getElementById('createTournamentForm');
    if (!form) return;

    // Initialize date validation for create tournament form
    initializeDateValidation();

    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        clearErrors();

        // Validate date before submission
        if (!validateTournamentDate()) {
            return;
        }

        const formData = {
            name: document.getElementById('name').value,
            description: document.getElementById('description').value,
            date: document.getElementById('date').value,
            time: document.getElementById('time').value,
            location: document.getElementById('location').value,
            maxEntrants: parseInt(document.getElementById('maxEntrants').value)
        };

        try {
            const response = await fetch('/tournaments/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData)
            });

            const result = await response.json();
            handleResponse(result, response, '/tournaments', 'Failed to create tournament');
        } catch (error) {
            handleError(error, 'An error occurred. Please try again.');
        }
    });
}

// Edit tournament form handling
function initializeEditTournamentForm() {
    const form = document.getElementById('editTournamentForm');
    if (!form) return;

    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        clearErrors();

        const tournamentId = document.getElementById('tournamentId').value;
        const formData = {
            name: document.getElementById('name').value,
            description: document.getElementById('description').value,
            date: document.getElementById('date').value,
            time: document.getElementById('time').value,
            location: document.getElementById('location').value,
            maxEntrants: parseInt(document.getElementById('maxEntrants').value)
        };

        try {
            const response = await fetch(`/tournaments/edit/${tournamentId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData)
            });

            const result = await response.json();
            handleResponse(result, response, '/tournaments', 'Failed to update tournament');
        } catch (error) {
            handleError(error, 'An error occurred. Please try again.');
        }
    });
}

// Delete tournament function
async function deleteTournament(tournamentId) {
    if (!confirm('Are you sure you want to delete this tournament? This action cannot be undone.')) {
        return;
    }

    try {
        const response = await fetch(`/tournaments/delete/${tournamentId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            }
        });

        const result = await response.json();

        if (response.ok && result.success) {
            alert(result.message);
            window.location.reload();
        } else {
            alert(result.message || 'Failed to delete tournament');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('An error occurred. Please try again.');
    }
}

// Tournament Date Validation
function initializeDateValidation() {
    const dateInput = document.getElementById('date');
    const dateError = document.getElementById('dateError');

    if (!dateInput || !dateError) {
        return;
    }

    // Set the minimum date to today
    const today = new Date().toISOString().split('T')[0];
    dateInput.setAttribute('min', today);

    // Add real-time validation for the date field
    dateInput.addEventListener('change', function() {
        validateTournamentDate();
    });

    dateInput.addEventListener('blur', function() {
        validateTournamentDate();
    });
}

function validateTournamentDate() {
    const dateInput = document.getElementById('date');
    const dateError = document.getElementById('dateError');

    if (!dateInput || !dateError) {
        return true;
    }

    if (!dateInput.value) {
        showDateError('Date is required');
        return false;
    }

    // Get today's date in YYYY-MM-DD format
    const today = new Date();
    const todayString = today.toISOString().split('T')[0];

    // Compare date strings directly to avoid timezone issues
    const selectedDateString = dateInput.value;

    if (selectedDateString <= todayString) {
        showDateError('Tournament date must be in the future');
        return false;
    }

    hideDateError();
    return true;
}

function showDateError(message) {
    const dateError = document.getElementById('dateError');
    const dateInput = document.getElementById('date');

    if (dateError && dateInput) {
        dateError.textContent = message;
        dateError.style.display = 'block';
        dateInput.style.borderColor = '#d32f2f';
    }
}

function hideDateError() {
    const dateError = document.getElementById('dateError');
    const dateInput = document.getElementById('date');

    if (dateError && dateInput) {
        dateError.textContent = '';
        dateError.style.display = 'none';
        dateInput.style.borderColor = '#b2dfdb';
    }
}

// Initialize all forms when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    initializeRegistrationForm();
    initializeCreateTournamentForm();
    initializeEditTournamentForm();
});
