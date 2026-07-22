const branchSelect = document.getElementById("branch");
const dateInput = document.getElementById("date");
const slotSelect = document.getElementById("slot");
const form = document.getElementById("booking-form");
const confirmation = document.getElementById("confirmation");

async function loadBranches() {
    const response = await fetch("/api/branches");
    const branches = await response.json();

    branchSelect.innerHTML = "";
    for (const branch of branches) {
        const option = document.createElement("option");
        option.value = branch.id;
        option.textContent = `${branch.name} (${branch.openTime} - ${branch.closeTime})`;
        branchSelect.appendChild(option);
    }

    if (branches.length > 0) {
        await loadSlots();
    }
}

async function loadSlots() {
    const branchId = branchSelect.value;
    const date = dateInput.value;

    if (!branchId || !date) {
        slotSelect.innerHTML = "<option value=''>Select branch and date</option>";
        return;
    }

    const response = await fetch(`/api/branches/${branchId}/slots?date=${date}`);
    const slots = await response.json();

    slotSelect.innerHTML = "";
    if (slots.length === 0) {
        slotSelect.innerHTML = "<option value=''>No slots available</option>";
        return;
    }

    for (const slot of slots) {
        const option = document.createElement("option");
        option.value = slot.startsAt;
        option.textContent = `${new Date(slot.startsAt).toLocaleString()} - ${new Date(slot.endsAt).toLocaleTimeString()}`;
        slotSelect.appendChild(option);
    }
}

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const payload = {
        branchId: Number(branchSelect.value),
        customerName: document.getElementById("name").value,
        customerEmail: document.getElementById("email").value,
        customerPhone: document.getElementById("phone").value,
        startsAt: slotSelect.value
    };

    const response = await fetch("/api/appointments", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    });

    if (!response.ok) {
        const error = await response.json();
        confirmation.textContent = `Booking failed: ${error.message}`;
        return;
    }

    const data = await response.json();
    confirmation.textContent = JSON.stringify(data, null, 2);
    await loadSlots();
});

branchSelect.addEventListener("change", loadSlots);
dateInput.addEventListener("change", loadSlots);

const tomorrow = new Date();
tomorrow.setDate(tomorrow.getDate() + 1);
dateInput.value = tomorrow.toISOString().slice(0, 10);

loadBranches();

