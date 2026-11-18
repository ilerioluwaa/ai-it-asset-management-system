
const API_URL = "/api/assets";

const assetForm = document.getElementById("assetForm");
const assetTable = document.getElementById("assetTable");
const totalAssets = document.getElementById("totalAssets");
const message = document.getElementById("message");

function showMessage(text, isError = false) {
    message.textContent = text;
    message.style.color = isError ? "#dc3545" : "#198754";
}

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

            const actionCell = document.createElement("td");
            const deleteButton = document.createElement("button");

            deleteButton.textContent = "Delete";
            deleteButton.className = "btn btn-danger btn-sm";
            deleteButton.addEventListener("click", () => {
                deleteAsset(asset.id);
            });

            actionCell.appendChild(deleteButton);
            row.appendChild(actionCell);
            assetTable.appendChild(row);
        });
    } catch (error) {
        showMessage(error.message, true);
    }
}

assetForm.addEventListener("submit", async event => {
    event.preventDefault();

    const asset = {
        name: document.getElementById("assetName").value,
        category: document.getElementById("assetCategory").value,
        serialNumber: document.getElementById("serialNumber").value,
        status: document.getElementById("assetStatus").value
    };

    try {
        const response = await fetch(API_URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(asset)
        });

        if (!response.ok) {
            throw new Error("Failed to register asset");
        }

        assetForm.reset();
        showMessage("Asset registered successfully!");
        await loadAssets();
    } catch (error) {
        showMessage(error.message, true);
    }
});

async function deleteAsset(id) {
    if (!confirm("Are you sure you want to delete this asset?")) {
        return;
    }

    try {
        const response = await fetch(`${API_URL}/${id}`, {
            method: "DELETE"
        });

        if (!response.ok) {
            throw new Error("Failed to delete asset");
        }

        showMessage("Asset deleted successfully!");
        await loadAssets();
    } catch (error) {
        showMessage(error.message, true);
    }
}

loadAssets();
