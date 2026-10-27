// Campus Event Management System - Interactive Behaviors

document.addEventListener('DOMContentLoaded', () => {
    // 1. Mobile Menu Toggle
    const mobileMenuBtn = document.getElementById('mobileMenuToggle');
    const navLinks = document.querySelector('.nav-links');

    if (mobileMenuBtn && navLinks) {
        mobileMenuBtn.addEventListener('click', () => {
            navLinks.classList.toggle('show');
        });
    }

    // 2. Demo Credential Autofill (Convenient for quick evaluations)
    const demoAdminBtn = document.getElementById('fillDemoAdmin');
    const demoStudentBtn = document.getElementById('fillDemoStudent');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');

    if (demoAdminBtn && emailInput && passwordInput) {
        demoAdminBtn.addEventListener('click', (e) => {
            e.preventDefault();
            emailInput.value = 'admin@campus.edu';
            passwordInput.value = 'Admin@123';
        });
    }

    if (demoStudentBtn && emailInput && passwordInput) {
        demoStudentBtn.addEventListener('click', (e) => {
            e.preventDefault();
            emailInput.value = 'student@campus.edu';
            passwordInput.value = 'Student@123';
        });
    }

    // 3. Auto-dismiss alerts after 6 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.4s ease, transform 0.4s ease';
            alert.style.opacity = '0';
            alert.style.transform = 'translateY(-10px)';
            setTimeout(() => alert.remove(), 400);
        }, 6000);
    });

    // 4. Client-side date and time validation hints
    const eventForm = document.getElementById('eventForm');
    if (eventForm) {
        const startTime = document.getElementById('startTime');
        const endTime = document.getElementById('endTime');
        const eventDate = document.getElementById('eventDate');
        const deadline = document.getElementById('registrationDeadline');

        if (startTime && endTime) {
            endTime.addEventListener('change', () => {
                if (startTime.value && endTime.value && endTime.value <= startTime.value) {
                    endTime.setCustomValidity('End time must be after start time');
                } else {
                    endTime.setCustomValidity('');
                }
            });
        }

        if (eventDate && deadline) {
            deadline.addEventListener('change', () => {
                if (eventDate.value && deadline.value && deadline.value > eventDate.value) {
                    deadline.setCustomValidity('Registration deadline cannot be after the event date');
                } else {
                    deadline.setCustomValidity('');
                }
            });
        }
    }
});
