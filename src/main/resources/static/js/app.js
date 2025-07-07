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

    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        clearErrors();

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

// Initialize all forms when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    initializeRegistrationForm();
    initializeCreateTournamentForm();
    initializeEditTournamentForm();
});
