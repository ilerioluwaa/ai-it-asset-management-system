
const API_URL = "/api/assets";

const assetForm = document.getElementById("assetForm");
const assetTable = document.getElementById("assetTable");
const totalAssets = document.getElementById("totalAssets");
const message = document.getElementById("message");

const assetName = document.getElementById("assetName");
const assetCategory = document.getElementById("assetCategory");
const serialNumber = document.getElementById("serialNumber");
const assetStatus = document.getElementById("assetStatus");

const submitButton = assetForm.querySelector('button[type="submit"]');
const cancelEditButton = document.getElementById("cancelEditButton");

let editingAssetId = null;

// Display success and error messages
function showMessage(text, isError = false) {
    message.textContent = text;
    message.style.color = isError ? "#dc3545" : "#198754";
}

// Reset the form to Add Asset mode
function resetForm() {
    assetForm.reset();
    editingAssetId = null;

    submitButton.textContent = "Add Asset";

    if (cancelEditButton) {
        cancelEditButton.hidden = true;
    }
}

// Load all assets from the backend
async function loadAssets() {
    try {
        const response = await fetch(API_URL);

        if (!response.ok) {
            throw new Error("Unable to load assets");
        }

        const assets = await response.json();

        assetTable.replaceChildren();
        totalAssets.textContent = assets.length;

        assets.forEach(asset => {
            const row = document.createElement("tr");

            const values = [
                asset.id,
                asset.name,
                asset.category,
                asset.serialNumber,
                asset.status
            ];

            values.forEach(value => {
                const cell = document.createElement("td");
                cell.textContent = value ?? "";
                row.appendChild(cell);
            });

            // Actions column
            const actionCell = document.createElement("td");

            // Edit button
            const editButton = document.createElement("button");
            editButton.textContent = "Edit";
            editButton.className = "btn btn-warning btn-sm me-2";
            editButton.type = "button";

            editButton.addEventListener("click", () => {
                editAsset(asset);
            });

            // Delete button
            const deleteButton = document.createElement("button");
            deleteButton.textContent = "Delete";
            deleteButton.className = "btn btn-danger btn-sm";
            deleteButton.type = "button";

            deleteButton.addEventListener("click", () => {
                deleteAsset(asset.id);
            });

            actionCell.appendChild(editButton);
            actionCell.appendChild(deleteButton);

            row.appendChild(actionCell);
            assetTable.appendChild(row);
        });

    } catch (error) {
        showMessage(error.message, true);
    }
}

// Populate the form with an existing asset
function editAsset(asset) {
    editingAssetId = asset.id;

    assetName.value = asset.name ?? "";
    assetCategory.value = asset.category ?? "";
    serialNumber.value = asset.serialNumber ?? "";
    assetStatus.value = asset.status ?? "Available";

    submitButton.textContent = "Update Asset";

    if (cancelEditButton) {
        cancelEditButton.hidden = false;
    }

    showMessage("Editing asset #" + asset.id);

    assetForm.scrollIntoView({
        behavior: "smooth",
        block: "center"
    });

    assetName.focus();
}

// Handle both asset creation and updates
assetForm.addEventListener("submit", async event => {
    event.preventDefault();

    const asset = {
        name: assetName.value.trim(),
        category: assetCategory.value.trim(),
        serialNumber: serialNumber.value.trim(),
        status: assetStatus.value
    };

    if (!asset.name || !asset.category || !asset.serialNumber) {
        showMessage("Please complete all required fields.", true);
        return;
    }

    const isEditing = editingAssetId !== null;

    const url = isEditing
        ? `${API_URL}/${editingAssetId}`
        : API_URL;

    const method = isEditing ? "PUT" : "POST";

    try {
        submitButton.disabled = true;

        const response = await fetch(url, {
            method: method,
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(asset)
        });

        if (!response.ok) {
            if (response.status === 404) {
                throw new Error("Asset not found");
            }

            throw new Error(
                isEditing
                    ? "Failed to update asset"
                    : "Failed to register asset"
            );
        }

        const successMessage = isEditing
            ? "Asset updated successfully!"
            : "Asset registered successfully!";

        resetForm();
        await loadAssets();
        showMessage(successMessage);

    } catch (error) {
        showMessage(error.message, true);

    } finally {
        submitButton.disabled = false;
    }
});

// Cancel editing without saving changes
if (cancelEditButton) {
    cancelEditButton.addEventListener("click", () => {
        resetForm();
        showMessage("Editing cancelled. No changes were saved.");
    });
}

// Delete an existing asset
async function deleteAsset(id) {
    if (!confirm("Are you sure you want to delete this asset?")) {
        return;
    }

    try {
        const response = await fetch(`${API_URL}/${id}`, {
            method: "DELETE"
        });

        if (!response.ok) {
            if (response.status === 404) {
                throw new Error("Asset not found");
            }

            throw new Error("Failed to delete asset");
        }

        // Return to Add Asset mode if the edited asset was deleted
        if (editingAssetId === id) {
            resetForm();
        }

        await loadAssets();
        showMessage("Asset deleted successfully!");

    } catch (error) {
        showMessage(error.message, true);
    }
}

// Load the inventory when the page opens
loadAssets();
