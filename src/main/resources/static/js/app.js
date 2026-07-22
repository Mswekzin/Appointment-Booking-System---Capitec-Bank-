const branchSelect = document.getElementById("branch");
const dateInput = document.getElementById("date");
const slotSelect = document.getElementById("slot");
const form = document.getElementById("booking-form");
const confirmation = document.getElementById("confirmation");

/* ── State ──────────────────────────────────────────────────────── */
let currentStep = 1;
let selectedSlotValue = null;
let allBranches = [];

/* ── Helpers ─────────────────────────────────────────────────────── */
function fmt(dateStr) {
    return new Date(dateStr).toLocaleString(undefined, {
        weekday: 'short', year: 'numeric', month: 'short',
        day: 'numeric', hour: '2-digit', minute: '2-digit'
    });
}
function fmtTime(dateStr) {
    return new Date(dateStr).toLocaleTimeString(undefined, { hour: '2-digit', minute: '2-digit' });
}

function showToast(msg, isError = false) {
    const toast = document.getElementById('toast');
    toast.textContent = msg;
    toast.className = 'toast' + (isError ? ' error' : '');
    setTimeout(() => toast.classList.add('hidden'), 3500);
}

/* ── Tab navigation ───────────────────────────────────────────── */
document.querySelectorAll('.tab-btn').forEach(btn => {
    btn.addEventListener('click', () => {
        document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
        document.querySelectorAll('.tab-content').forEach(t => t.classList.remove('active'));
        btn.classList.add('active');
        document.getElementById('tab-' + btn.dataset.tab).classList.add('active');
    });
});

/* ── Step navigation ──────────────────────────────────────────── */
function gotoStep(n) {
    document.querySelectorAll('.form-step').forEach(s => s.classList.remove('active'));
    document.querySelector(`.form-step[data-step="${n}"]`).classList.add('active');
    document.querySelectorAll('.step').forEach(s => {
        const sn = parseInt(s.dataset.step);
        s.classList.remove('active', 'done');
        if (sn === n) s.classList.add('active');
        if (sn < n)  s.classList.add('done');
    });
    currentStep = n;
}

document.getElementById('step1-next').addEventListener('click', () => {
    if (!document.getElementById('branch').value) { showToast('Please select a branch', true); return; }
    if (!document.getElementById('date').value)   { showToast('Please select a date', true); return; }
    if (!selectedSlotValue)                        { showToast('Please select a time slot', true); return; }
    gotoStep(2);
});

document.getElementById('step2-back').addEventListener('click', () => gotoStep(1));
document.getElementById('step2-next').addEventListener('click', () => {
    if (!validateDetails()) return;
    buildSummary();
    gotoStep(3);
});
document.getElementById('step3-back').addEventListener('click', () => gotoStep(2));

/* ── Branch load ──────────────────────────────────────────────── */
async function loadBranches() {
    const res = await fetch('/api/branches');
    allBranches = await res.json();

    const select = document.getElementById('branch');
    select.innerHTML = '';
    allBranches.forEach(b => {
        const opt = document.createElement('option');
        opt.value = b.id;
        opt.textContent = `${b.name} — ${b.address}`;
        select.appendChild(opt);
    });
    updateBranchInfo();
    await loadSlots();
}

function updateBranchInfo() {
    const branchId = parseInt(document.getElementById('branch').value);
    const branch = allBranches.find(b => b.id === branchId);
    const infoEl = document.getElementById('branch-info');
    if (branch) {
        infoEl.textContent = `📍 ${branch.address}  ·  🕒 Open ${fmtTime('1970-01-01T' + branch.openTime)} – ${fmtTime('1970-01-01T' + branch.closeTime)}  ·  ${branch.slotMinutes}-minute slots`;
        infoEl.classList.remove('hidden');
    } else {
        infoEl.classList.add('hidden');
    }
}

/* ── Slot load ────────────────────────────────────────────────── */
async function loadSlots() {
    const branchId = document.getElementById('branch').value;
    const date = document.getElementById('date').value;
    const grid = document.getElementById('slots-grid');
    selectedSlotValue = null;
    document.getElementById('slot').value = '';

    if (!branchId || !date) {
        grid.innerHTML = '<p class="slots-empty">Select a branch and date to see available slots.</p>';
        return;
    }

    grid.innerHTML = '<p class="slots-empty">Loading slots…</p>';
    const res = await fetch(`/api/branches/${branchId}/slots?date=${date}`);
    const slots = await res.json();

    if (!slots.length) {
        grid.innerHTML = '<p class="slots-empty">No available slots for this date.</p>';
        return;
    }

    grid.innerHTML = '';
    slots.forEach(slot => {
        const chip = document.createElement('button');
        chip.type = 'button';
        chip.className = 'slot-chip';
        chip.textContent = fmtTime(slot.startsAt) + ' – ' + fmtTime(slot.endsAt);
        chip.dataset.value = slot.startsAt;
        chip.addEventListener('click', () => {
            grid.querySelectorAll('.slot-chip').forEach(c => c.classList.remove('selected'));
            chip.classList.add('selected');
            selectedSlotValue = slot.startsAt;
            document.getElementById('slot').value = slot.startsAt;
        });
        grid.appendChild(chip);
    });
}

/* ── Details validation ───────────────────────────────────────── */
function validateDetails() {
    let ok = true;
    const nameEl  = document.getElementById('name');
    const emailEl = document.getElementById('email');
    const phoneEl = document.getElementById('phone');

    [nameEl, emailEl, phoneEl].forEach(el => {
        el.classList.remove('invalid');
        document.getElementById(el.id + '-error').textContent = '';
    });

    if (!nameEl.value.trim()) {
        nameEl.classList.add('invalid');
        document.getElementById('name-error').textContent = 'Full name is required.';
        ok = false;
    }
    if (!emailEl.value.trim() || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(emailEl.value)) {
        emailEl.classList.add('invalid');
        document.getElementById('email-error').textContent = 'Please enter a valid email address.';
        ok = false;
    }
    if (!phoneEl.value.trim() || !/^[0-9+()\-\s]{7,20}$/.test(phoneEl.value)) {
        phoneEl.classList.add('invalid');
        document.getElementById('phone-error').textContent = 'Please enter a valid phone number.';
        ok = false;
    }
    return ok;
}

/* ── Summary ──────────────────────────────────────────────────── */
function buildSummary() {
    const branchId = parseInt(document.getElementById('branch').value);
    const branch = allBranches.find(b => b.id === branchId);
    const rows = [
        ['Branch',    branch ? branch.name : ''],
        ['Address',   branch ? branch.address : ''],
        ['Date & Time', fmt(selectedSlotValue)],
        ['Name',      document.getElementById('name').value],
        ['Email',     document.getElementById('email').value],
        ['Phone',     document.getElementById('phone').value],
    ];
    const grid = document.getElementById('summary');
    grid.innerHTML = rows.map(([l, v]) =>
        `<span class="summary-label">${l}</span><span class="summary-value">${v}</span>`
    ).join('');
}

/* ── Submit ───────────────────────────────────────────────────── */
document.getElementById('booking-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const btn = document.getElementById('submit-btn');
    const label = document.getElementById('submit-label');
    const spinner = document.getElementById('submit-spinner');
    btn.disabled = true;
    label.textContent = 'Booking…';
    spinner.classList.remove('hidden');

    try {
        const payload = {
            branchId: parseInt(document.getElementById('branch').value),
            customerName:  document.getElementById('name').value.trim(),
            customerEmail: document.getElementById('email').value.trim(),
            customerPhone: document.getElementById('phone').value.trim(),
            startsAt: selectedSlotValue
        };

        const res = await fetch('/api/appointments', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!res.ok) {
            const err = await res.json();
            showToast(err.message || 'Booking failed. Please try again.', true);
            return;
        }

        const appt = await res.json();
        showConfirmation(appt);
        document.getElementById('booking-form').reset();
        selectedSlotValue = null;
        gotoStep(1);
        await loadSlots();
    } catch (err) {
        showToast('Network error. Please check your connection.', true);
    } finally {
        btn.disabled = false;
        label.textContent = 'Confirm Booking';
        spinner.classList.add('hidden');
    }
});

/* ── Confirmation display ─────────────────────────────────────── */
function showConfirmation(appt) {
    const details = document.getElementById('confirmation-details');
    const rows = [
        ['Branch',     appt.branchName],
        ['Date & Time', fmt(appt.startsAt)],
        ['Name',       appt.customerName],
        ['Email',      appt.customerEmail],
        ['Booking ID', `#${appt.appointmentId}`],
    ];
    details.innerHTML = rows.map(([l, v]) =>
        `<span class="cd-label">${l}</span><span class="cd-value">${v}</span>`
    ).join('');
    document.getElementById('confirmation-msg').textContent = appt.confirmationMessage;
    document.getElementById('confirmation-banner').classList.remove('hidden');
    document.getElementById('confirmation-banner').scrollIntoView({ behavior: 'smooth' });
}

function dismissConfirmation() {
    document.getElementById('confirmation-banner').classList.add('hidden');
    document.getElementById('booking-section').scrollIntoView({ behavior: 'smooth' });
}

/* ── My Bookings lookup ───────────────────────────────────────── */
document.getElementById('lookup-btn').addEventListener('click', async () => {
    const email = document.getElementById('lookup-email').value.trim();
    const list  = document.getElementById('bookings-list');
    if (!email) { showToast('Please enter your email address', true); return; }

    list.innerHTML = '<p style="color:var(--grey-400);font-size:13px;padding:12px 0">Loading…</p>';

    // find customer by email then fetch appointments
    const branchesRes = await fetch('/api/branches');
    // We search via creating a temp booking to get customer id — instead, we use the email to look up
    // We'll try to find by looking through the appointments response for the customer email
    // The API supports GET /api/customers/{id}/appointments — need customer ID from email
    // As a pragmatic workaround: show message to use booking ID or contact support
    // Actually we expose the customer ID in appointment responses - look it up by booking email
    // Better: hit a dedicated lookup. For now we guide the user.
    // We'll do a lightweight POST attempt with a dummy date to get a 409/404 to extract customerId from existing records
    // Simpler: we added a workaround — call /api/appointments with search (not available)
    // BEST: add a customer lookup endpoint. Let's add it to the controller.
    const res = await fetch(`/api/customers/by-email?email=${encodeURIComponent(email)}`);

    if (res.status === 404) {
        list.innerHTML = '<p style="color:var(--grey-400);font-size:13px;padding:12px 0">No appointments found for this email address.</p>';
        return;
    }
    if (!res.ok) {
        list.innerHTML = '<p style="color:var(--danger);font-size:13px;padding:12px 0">Error looking up appointments.</p>';
        return;
    }

    const appointments = await res.json();
    if (!appointments.length) {
        list.innerHTML = '<p style="color:var(--grey-400);font-size:13px;padding:12px 0">No appointments found for this email address.</p>';
        return;
    }

    list.innerHTML = '';
    appointments.forEach(appt => {
        const card = document.createElement('div');
        card.className = 'booking-card';
        const statusCls = appt.status === 'BOOKED' ? 'status--booked' : 'status--cancelled';
        card.innerHTML = `
          <div class="booking-card__info">
            <div class="booking-card__branch">${appt.branchName}</div>
            <div class="booking-card__meta">${fmt(appt.startsAt)}&nbsp;·&nbsp;Booking #${appt.appointmentId}</div>
          </div>
          <div style="display:flex;align-items:center;gap:12px;flex-shrink:0">
            <span class="booking-card__status ${statusCls}">${appt.status}</span>
            ${appt.status === 'BOOKED'
                ? `<button class="btn btn--danger-ghost" data-id="${appt.appointmentId}">Cancel</button>`
                : ''}
          </div>`;
        if (appt.status === 'BOOKED') {
            card.querySelector('button').addEventListener('click', () => cancelBooking(appt.appointmentId, email));
        }
        list.appendChild(card);
    });
});

async function cancelBooking(id, email) {
    if (!confirm(`Cancel booking #${id}?`)) return;
    const res = await fetch(`/api/appointments/${id}/cancel`, { method: 'PATCH' });
    if (res.ok) {
        showToast('Appointment cancelled.');
        document.getElementById('lookup-btn').click();
    } else {
        showToast('Could not cancel appointment.', true);
    }
}

/* ── Event wiring ─────────────────────────────────────────────── */
document.getElementById('branch').addEventListener('change', () => { updateBranchInfo(); loadSlots(); });
document.getElementById('date').addEventListener('change', loadSlots);

/* ── Init ─────────────────────────────────────────────────────── */
const tomorrow = new Date();
tomorrow.setDate(tomorrow.getDate() + 1);
document.getElementById('date').value = tomorrow.toISOString().slice(0, 10);

loadBranches();
