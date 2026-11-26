
"use strict";


// APPLICATION CONFIGURATION


const API = {
    assets: "/api/assets",
    employees: "/api/employees",
    ai: "/api/ai",
    csrf: "/api/csrf"
};

const PAGE_SIZE = 10;

const state = {
    csrfToken: null,
    csrfHeader: null,

    assetPage: 0,
    assetTotalPages: 0,

    employeePage: 0,
    employeeTotalPages: 0,

    assets: [],
    employees: [],
    employeeOptions: [],

    aiConfigured: false,
    aiGenerating: false,

    notificationTimer: null
};

const $ = (id) => document.getElementById(id);


// ============================================
// APPLICATION INITIALIZATION
// ============================================

document.addEventListener("DOMContentLoaded", initialize);

async function initialize() {

    registerEventListeners();

    try {

        await loadCsrfToken();

        await Promise.all([
            loadDashboard(),
            loadAssets(),
            loadEmployees(),
            loadDepartments(),
            loadEmployeeOptions(),
            loadAiStatus()
        ]);

    } catch (error) {

        showNotification(error.message, "danger");
    }
}


// ============================================
// SECURITY AND API COMMUNICATION
// ============================================

async function loadCsrfToken() {

    const response = await fetch(API.csrf, {
        credentials: "same-origin",
        cache: "no-store"
    });

    if (response.status === 401 || response.redirected) {

        window.location.assign("/login");

        throw new Error("Authentication required.");
    }

    if (!response.ok) {

        throw new Error(
            "Unable to initialize application security."
        );
    }

    const csrf = await response.json();

    state.csrfToken = csrf.token;
    state.csrfHeader = csrf.headerName;
}


async function apiRequest(url, options = {}) {

    const method = (options.method || "GET").toUpperCase();

    const headers = new Headers(options.headers || {});

    headers.set("Accept", "application/json");

    if (options.body) {

        headers.set(
            "Content-Type",
            "application/json"
        );
    }

    const safeMethods = [
        "GET",
        "HEAD",
        "OPTIONS"
    ];

    if (!safeMethods.includes(method)) {

        if (!state.csrfToken || !state.csrfHeader) {

            await loadCsrfToken();
        }

        headers.set(
            state.csrfHeader,
            state.csrfToken
        );
    }

    let response;

    try {

        response = await fetch(url, {
            ...options,
            method,
            headers,
            credentials: "same-origin",
            cache: "no-store"
        });

    } catch (error) {

        throw new Error(
            "Unable to connect to the application server."
        );
    }

    if (response.status === 401 || response.redirected) {

        window.location.assign("/login");

        throw new Error(
            "Your session has expired. Please sign in again."
        );
    }

    if (response.status === 204) {

        return null;
    }

    const contentType =
        response.headers.get("Content-Type") || "";

    let data = null;

    if (contentType.includes("application/json")
        || contentType.includes("application/problem+json")) {

        data = await response.json();
    }

    if (!response.ok) {

        let message =
            data?.message
            || data?.detail
            || `Request failed (${response.status}).`;

        if (data?.validationErrors) {

            const errors = Object.values(
                data.validationErrors
            );

            if (errors.length > 0) {

                message += " " + errors.join(" ");
            }
        }

        if (response.status === 403) {

            message =
                "Access denied or invalid security token. "
                + "Refresh the page and try again.";
        }

        throw new Error(message);
    }

    return data;
}


// ============================================
// NOTIFICATIONS
// ============================================

function showNotification(message, type = "success") {

    const notification = $("notification");

    clearTimeout(state.notificationTimer);

    notification.className = `alert alert-${type}`;

    notification.textContent = message;

    notification.hidden = false;

    state.notificationTimer = setTimeout(() => {

        notification.hidden = true;

    }, 7000);
}


function hideNotification() {

    clearTimeout(state.notificationTimer);

    $("notification").hidden = true;
}


// ============================================
// NAVIGATION
// ============================================

function showSection(sectionId) {

    document.querySelectorAll(".app-section")
        .forEach(section => {

            section.hidden = section.id !== sectionId;
        });

    document.querySelectorAll(".nav-button")
        .forEach(button => {

            const active =
                button.dataset.section === sectionId;

            button.classList.toggle("active", active);

            if (active) {

                button.setAttribute(
                    "aria-current",
                    "page"
                );

            } else {

                button.removeAttribute("aria-current");
            }
        });

    hideNotification();
}


// ============================================
// DASHBOARD STATISTICS
// ============================================

async function loadDashboard() {

    const [assets, employees] = await Promise.all([

        apiRequest(`${API.assets}/statistics`),

        apiRequest(`${API.employees}/statistics`)
    ]);

    $("totalAssets").textContent =
        assets.totalAssets ?? 0;

    $("availableAssets").textContent =
        assets.availableAssets ?? 0;

    $("assignedAssets").textContent =
        assets.assignedAssets ?? 0;

    $("maintenanceAssets").textContent =
        assets.maintenanceAssets ?? 0;

    $("retiredAssets").textContent =
        assets.retiredAssets ?? 0;

    $("totalEmployees").textContent =
        employees.totalEmployees ?? 0;

    updateUtilization(assets);

    renderBreakdown(
        "categoryBreakdown",
        assets.assetsByCategory,
        "No category data available."
    );

    renderBreakdown(
        "departmentBreakdown",
        employees.employeesByDepartment,
        "No department data available."
    );
}


function updateUtilization(assets) {

    const total = Number(assets.totalAssets || 0);

    const retired = Number(assets.retiredAssets || 0);

    const assigned = Number(assets.assignedAssets || 0);

    const active = Math.max(0, total - retired);

    const utilization = active > 0

        ? Math.min(
            100,
            Math.round((assigned / active) * 100)
        )

        : 0;

    $("utilizationPercentage").textContent =
        `${utilization}%`;

    $("utilizationProgress").value =
        utilization;

    $("utilizationDescription").textContent =
        active > 0

            ? `${assigned} of ${active} active assets assigned.`

            : "No active inventory available.";
}


function renderBreakdown(
    containerId,
    data,
    emptyMessage
) {

    const container = $(containerId);

    container.replaceChildren();

    const entries = Object.entries(data || {});

    if (entries.length === 0) {

        const paragraph = document.createElement("p");

        paragraph.className = "text-muted";

        paragraph.textContent = emptyMessage;

        container.appendChild(paragraph);

        return;
    }

    const total = entries.reduce(

        (sum, entry) => sum + Number(entry[1] || 0),

        0
    );

    entries.forEach(([label, count]) => {

        const wrapper = document.createElement("div");

        wrapper.className = "mb-3";

        const heading = document.createElement("div");

        heading.className =
            "d-flex justify-content-between gap-2";

        const name = document.createElement("span");

        name.textContent = label;

        const value = document.createElement("strong");

        value.textContent = count;

        heading.append(name, value);

        const progress = document.createElement("progress");

        progress.className = "w-100";

        progress.max = Math.max(total, 1);

        progress.value = Number(count);

        progress.setAttribute("aria-label", label);

        wrapper.append(heading, progress);

        container.appendChild(wrapper);
    });
}


// ============================================
// ASSET MANAGEMENT
// ============================================

async function loadAssets() {

    const params = new URLSearchParams({

        page: String(state.assetPage),

        size: String(PAGE_SIZE)
    });

    const keyword = $("assetSearch").value.trim();

    const status = $("assetStatusFilter").value;

    if (keyword) {

        params.set("keyword", keyword);
    }

    if (status) {

        params.set("status", status);
    }

    const result = await apiRequest(

        `${API.assets}?${params.toString()}`
    );

    state.assetTotalPages = Number(
        result.totalPages ?? result.page?.totalPages ?? 0
    );

    if (state.assetPage > 0
        && state.assetPage >= state.assetTotalPages) {

        state.assetPage = Math.max(
            0,
            state.assetTotalPages - 1
        );

        return loadAssets();
    }

    state.assets = result.content || [];

    renderAssetTable();

    updateAssetPagination();
}


function renderAssetTable() {

    const tbody = $("assetTableBody");

    tbody.replaceChildren();

    if (state.assets.length === 0) {

        renderEmptyTable(
            tbody,
            7,
            "No assets found."
        );

        return;
    }

    state.assets.forEach(asset => {

        const row = document.createElement("tr");

        appendCell(row, asset.id);

        appendCell(row, asset.name);

        appendCell(row, asset.category);

        appendCell(row, asset.serialNumber);

        const statusCell = document.createElement("td");

        statusCell.appendChild(
            createStatusBadge(asset.status)
        );

        row.appendChild(statusCell);

        appendCell(
            row,
            asset.assignedTo || "Unassigned"
        );

        const actions = document.createElement("td");

        const actionGroup =
            document.createElement("div");

        actionGroup.className =
            "d-flex flex-wrap gap-1";

        actionGroup.appendChild(

            createActionButton(
                "Edit",
                "btn-warning",
                () => editAsset(asset.id)
            )
        );

        if (asset.status === "AVAILABLE") {

            actionGroup.appendChild(

                createActionButton(
                    "Assign",
                    "btn-primary",
                    () => openAssignmentDialog(asset.id)
                )
            );
        }

        if (asset.status === "ASSIGNED") {

            actionGroup.appendChild(

                createActionButton(
                    "Unassign",
                    "btn-secondary",
                    () => unassignAsset(asset.id)
                )
            );
        }

        const deleteButton = createActionButton(

            "Delete",

            "btn-danger",

            () => deleteAsset(asset.id)
        );

        deleteButton.disabled =
            asset.status === "ASSIGNED"
            || asset.employeeId != null;

        if (deleteButton.disabled) {

            deleteButton.title =
                "Unassign this asset before deleting it.";
        }

        actionGroup.appendChild(deleteButton);

        actions.appendChild(actionGroup);

        row.appendChild(actions);

        tbody.appendChild(row);
    });
}


async function saveAsset(event) {

    event.preventDefault();

    const id = $("assetId").value;

    const status = $("assetStatus").value;

    const employeeId = $("assetEmployee").value;

    const request = {

        name: $("assetName").value.trim(),

        category: $("assetCategory").value.trim(),

        serialNumber:
            $("assetSerialNumber").value.trim(),

        status,

        employeeId:
            status === "ASSIGNED" && employeeId

                ? Number(employeeId)

                : null
    };

    if (status === "ASSIGNED"
        && request.employeeId === null) {

        throw new Error(
            "Select an employee for the assigned asset."
        );
    }

    const button = $("saveAssetButton");

    button.disabled = true;

    try {

        await apiRequest(

            id
                ? `${API.assets}/${id}`
                : API.assets,

            {
                method: id ? "PUT" : "POST",

                body: JSON.stringify(request)
            }
        );

        resetAssetForm();

        if (!id) {

            state.assetPage = 0;
        }

        await refreshAssetData();

        showNotification(

            id

                ? "Asset updated successfully."

                : "Asset registered successfully."
        );

    } finally {

        button.disabled = false;
    }
}


async function editAsset(id) {

    const asset = await apiRequest(
        `${API.assets}/${id}`
    );

    await loadEmployeeOptions();

    showSection("dashboardSection");

    $("assetId").value = asset.id;

    $("assetName").value = asset.name;

    $("assetCategory").value = asset.category;

    $("assetSerialNumber").value =
        asset.serialNumber;

    $("assetStatus").value = asset.status;

    toggleAssetEmployeeField();

    $("assetEmployee").value =
        asset.employeeId ?? "";

    $("assetFormTitle").textContent =
        "Edit Asset";

    $("saveAssetButton").textContent =
        "Update Asset";

    $("cancelAssetEditButton").hidden = false;

    $("assetForm").scrollIntoView({

        behavior: "smooth",

        block: "center"
    });

    $("assetName").focus({
        preventScroll: true
    });
}


function resetAssetForm() {

    $("assetForm").reset();

    $("assetId").value = "";

    $("assetStatus").value = "AVAILABLE";

    $("assetEmployee").value = "";

    $("assetFormTitle").textContent =
        "Register New Asset";

    $("saveAssetButton").textContent =
        "Add Asset";

    $("cancelAssetEditButton").hidden = true;

    toggleAssetEmployeeField();
}


function toggleAssetEmployeeField() {

    const assigned =
        $("assetStatus").value === "ASSIGNED";

    $("assetEmployeeGroup").hidden = !assigned;

    $("assetEmployee").required = assigned;

    if (!assigned) {

        $("assetEmployee").value = "";
    }
}


async function deleteAsset(id) {

    const confirmed = window.confirm(

        "Are you sure you want to delete this asset? "
        + "This action cannot be undone."
    );

    if (!confirmed) {

        return;
    }

    await apiRequest(`${API.assets}/${id}`, {

        method: "DELETE"
    });

    if ($("assetId").value === String(id)) {

        resetAssetForm();
    }

    await refreshAssetData();

    showNotification(
        "Asset deleted successfully."
    );
}


async function refreshAssetData() {

    await Promise.all([

        loadAssets(),

        loadDashboard(),

        loadEmployees()
    ]);
}


function updateAssetPagination() {

    const total = Math.max(
        state.assetTotalPages,
        1
    );

    $("assetPageInfo").textContent =

        `Page ${state.assetPage + 1} of ${total}`;

    $("previousAssetPage").disabled =
        state.assetPage <= 0;

    $("nextAssetPage").disabled =
        state.assetPage + 1 >= state.assetTotalPages;
}


// ============================================
// EMPLOYEE MANAGEMENT
// ============================================

async function loadEmployees() {

    const params = new URLSearchParams({

        page: String(state.employeePage),

        size: String(PAGE_SIZE)
    });

    const keyword =
        $("employeeSearch").value.trim();

    const department =
        $("departmentFilter").value;

    if (keyword) {

        params.set("keyword", keyword);
    }

    if (department) {

        params.set("department", department);
    }

    const result = await apiRequest(

        `${API.employees}?${params.toString()}`
    );

    state.employeeTotalPages = Number(

        result.totalPages
        ?? result.page?.totalPages
        ?? 0
    );

    if (state.employeePage > 0
        && state.employeePage >= state.employeeTotalPages) {

        state.employeePage = Math.max(

            0,

            state.employeeTotalPages - 1
        );

        return loadEmployees();
    }

    state.employees = result.content || [];

    renderEmployeeTable();

    updateEmployeePagination();
}


function renderEmployeeTable() {

    const tbody = $("employeeTableBody");

    tbody.replaceChildren();

    if (state.employees.length === 0) {

        renderEmptyTable(
            tbody,
            6,
            "No employees found."
        );

        return;
    }

    state.employees.forEach(employee => {

        const row = document.createElement("tr");

        appendCell(row, employee.id);

        appendCell(row, employee.name);

        appendCell(row, employee.email);

        appendCell(row, employee.department);

        appendCell(
            row,
            employee.assignedAssetCount ?? 0
        );

        const actions = document.createElement("td");

        const actionGroup =
            document.createElement("div");

        actionGroup.className =
            "d-flex flex-wrap gap-1";

        actionGroup.appendChild(

            createActionButton(
                "Edit",
                "btn-warning",
                () => editEmployee(employee.id)
            )
        );

        const deleteButton = createActionButton(

            "Delete",

            "btn-danger",

            () => deleteEmployee(employee.id)
        );

        deleteButton.disabled =
            Number(employee.assignedAssetCount || 0) > 0;

        if (deleteButton.disabled) {

            deleteButton.title =
                "Remove assigned assets before deleting.";
        }

        actionGroup.appendChild(deleteButton);

        actions.appendChild(actionGroup);

        row.appendChild(actions);

        tbody.appendChild(row);
    });
}


async function saveEmployee(event) {

    event.preventDefault();

    const id = $("employeeId").value;

    const request = {

        name: $("employeeName").value.trim(),

        email: $("employeeEmail").value.trim(),

        department:
            $("employeeDepartment").value.trim()
    };

    const button = $("saveEmployeeButton");

    button.disabled = true;

    try {

        await apiRequest(

            id
                ? `${API.employees}/${id}`
                : API.employees,

            {
                method: id ? "PUT" : "POST",

                body: JSON.stringify(request)
            }
        );

        resetEmployeeForm();

        if (!id) {

            state.employeePage = 0;
        }

        await refreshEmployeeData();

        showNotification(

            id

                ? "Employee updated successfully."

                : "Employee registered successfully."
        );

    } finally {

        button.disabled = false;
    }
}


async function editEmployee(id) {

    const employee = await apiRequest(
        `${API.employees}/${id}`
    );

    showSection("employeesSection");

    $("employeeId").value = employee.id;

    $("employeeName").value = employee.name;

    $("employeeEmail").value = employee.email;

    $("employeeDepartment").value =
        employee.department;

    $("employeeFormTitle").textContent =
        "Edit Employee";

    $("saveEmployeeButton").textContent =
        "Update Employee";

    $("cancelEmployeeEditButton").hidden = false;

    $("employeeForm").scrollIntoView({

        behavior: "smooth",

        block: "center"
    });

    $("employeeName").focus({
        preventScroll: true
    });
}


function resetEmployeeForm() {

    $("employeeForm").reset();

    $("employeeId").value = "";

    $("employeeFormTitle").textContent =
        "Register New Employee";

    $("saveEmployeeButton").textContent =
        "Add Employee";

    $("cancelEmployeeEditButton").hidden = true;
}


async function deleteEmployee(id) {

    const confirmed = window.confirm(

        "Are you sure you want to delete this employee? "
        + "This action cannot be undone."
    );

    if (!confirmed) {

        return;
    }

    await apiRequest(`${API.employees}/${id}`, {

        method: "DELETE"
    });

    if ($("employeeId").value === String(id)) {

        resetEmployeeForm();
    }

    await refreshEmployeeData();

    showNotification(
        "Employee deleted successfully."
    );
}


async function refreshEmployeeData() {

    await loadDepartments();

    await Promise.all([

        loadEmployees(),

        loadEmployeeOptions(),

        loadDashboard(),

        loadAssets()
    ]);
}


function updateEmployeePagination() {

    const total = Math.max(
        state.employeeTotalPages,
        1
    );

    $("employeePageInfo").textContent =

        `Page ${state.employeePage + 1} of ${total}`;

    $("previousEmployeePage").disabled =
        state.employeePage <= 0;

    $("nextEmployeePage").disabled =
        state.employeePage + 1
        >= state.employeeTotalPages;
}


async function loadDepartments() {

    const departments = await apiRequest(

        `${API.employees}/departments`
    );

    const select = $("departmentFilter");

    const previousValue = select.value;

    select.replaceChildren();

    select.appendChild(

        new Option("All Departments", "")
    );

    departments.forEach(department => {

        select.appendChild(

            new Option(department, department)
        );
    });

    select.value = departments.includes(previousValue)

        ? previousValue

        : "";
}


// ============================================
// EMPLOYEE ASSIGNMENT OPTIONS
// ============================================

async function loadEmployeeOptions() {

    const employees = [];

    let page = 0;

    let totalPages = 1;

    do {

        const result = await apiRequest(

            `${API.employees}?page=${page}&size=100`
        );

        employees.push(
            ...(result.content || [])
        );

        totalPages = Number(

            result.totalPages
            ?? result.page?.totalPages
            ?? 0
        );

        page++;

    } while (page < totalPages);

    state.employeeOptions = employees;

    populateEmployeeSelect("assetEmployee");

    populateEmployeeSelect("assignmentEmployee");
}


function populateEmployeeSelect(selectId) {

    const select = $(selectId);

    const previousValue = select.value;

    select.replaceChildren();

    select.appendChild(

        new Option("Select an employee", "")
    );

    state.employeeOptions.forEach(employee => {

        const label =
            `${employee.name} — ${employee.department}`;

        select.appendChild(

            new Option(
                label,
                String(employee.id)
            )
        );
    });

    const exists = state.employeeOptions.some(

        employee =>
            String(employee.id) === previousValue
    );

    select.value = exists

        ? previousValue

        : "";
}


// ============================================
// ASSET ALLOCATION
// ============================================

async function openAssignmentDialog(id) {

    const asset = await apiRequest(

        `${API.assets}/${id}`
    );

    await loadEmployeeOptions();

    if (asset.status !== "AVAILABLE") {

        throw new Error(
            "Only available assets can be assigned."
        );
    }

    if (state.employeeOptions.length === 0) {

        throw new Error(
            "Register an employee before assigning an asset."
        );
    }

    $("assignmentAssetId").value = asset.id;

    $("assignmentAssetName").textContent =
        `${asset.name} (${asset.serialNumber})`;

    $("assignmentEmployee").value = "";

    $("assignmentDialog").showModal();
}


async function assignAsset(event) {

    event.preventDefault();

    const assetId = $("assignmentAssetId").value;

    const employeeId = Number(
        $("assignmentEmployee").value
    );

    if (!Number.isSafeInteger(employeeId)
        || employeeId <= 0) {

        throw new Error(
            "Please select a valid employee."
        );
    }

    const submitButton = $("assignmentForm")
        .querySelector('button[type="submit"]');

    submitButton.disabled = true;

    try {

        await apiRequest(

            `${API.assets}/${assetId}/assign`,

            {
                method: "POST",

                body: JSON.stringify({
                    employeeId
                })
            }
        );

        closeAssignmentDialog();

        if ($("assetId").value === String(assetId)) {

            resetAssetForm();
        }

        await refreshAssetData();

        showNotification(
            "Asset assigned successfully."
        );

    } finally {

        submitButton.disabled = false;
    }
}


function closeAssignmentDialog() {

    $("assignmentDialog").close();

    $("assignmentForm").reset();

    $("assignmentAssetId").value = "";
}


async function unassignAsset(id) {

    const confirmed = window.confirm(

        "Return this asset to available inventory?"
    );

    if (!confirmed) {

        return;
    }

    await apiRequest(

        `${API.assets}/${id}/unassign`,

        {
            method: "POST"
        }
    );

    if ($("assetId").value === String(id)) {

        resetAssetForm();
    }

    await refreshAssetData();

    showNotification(
        "Asset returned to available inventory."
    );
}


// ============================================
// AI INVENTORY INTELLIGENCE
// ============================================

async function loadAiStatus() {

    const status = await apiRequest(

        `${API.ai}/status`
    );

    state.aiConfigured =
        Boolean(status.enabled && status.configured);

    let message;

    if (!status.enabled) {

        message = "AI service disabled.";

    } else if (!status.configured) {

        message = "AI provider not configured.";

    } else {

        message = "AI provider configured.";
    }

    $("aiServiceStatus").textContent = message;

    $("dashboardAiStatus").textContent = message;

    $("aiModel").textContent =
        status.model || "Not configured";

    const statusClass = state.aiConfigured

        ? "text-success fw-semibold"

        : "text-secondary";

    $("aiServiceStatus").className =
        statusClass;

    $("dashboardAiStatus").className =
        statusClass;

    $("generateAiButton").disabled =
        !state.aiConfigured || state.aiGenerating;
}


async function generateAiRecommendations() {

    if (state.aiGenerating) {

        return;
    }

    if (!state.aiConfigured) {

        throw new Error(
            "The AI provider is not configured."
        );
    }

    state.aiGenerating = true;

    $("generateAiButton").disabled = true;

    $("aiLoading").hidden = false;

    $("aiResultsPanel").hidden = true;

    try {

        const result = await apiRequest(

            `${API.ai}/recommendations`,

            {
                method: "POST"
            }
        );

        renderAiResults(result);

        showNotification(
            "AI recommendations generated successfully."
        );

    } finally {

        state.aiGenerating = false;

        $("aiLoading").hidden = true;

        $("generateAiButton").disabled =
            !state.aiConfigured;
    }
}


function renderAiResults(result) {

    $("aiResultModel").textContent =
        result.model || "Unknown model";

    const generatedAt = new Date(
        result.generatedAt
    );

    $("aiGeneratedAt").textContent =
        Number.isNaN(generatedAt.getTime())

            ? "Unavailable"

            : generatedAt.toLocaleString();

    const milliseconds = Number(
        result.responseTimeMs
    );

    $("aiResponseTime").textContent =
        Number.isFinite(milliseconds)

            ? `${(milliseconds / 1000).toFixed(2)} seconds`

            : "Unavailable";

    // Render AI output as plain text.
    // Never execute model-generated HTML.

    $("aiRecommendationText").textContent =

        result.recommendation
        || "No recommendations returned.";

    renderAiInventoryData(
        result.inventoryData || {}
    );

    $("aiResultsPanel").hidden = false;

    $("aiResultsPanel").scrollIntoView({

        behavior: "smooth",

        block: "start"
    });
}


function renderAiInventoryData(data) {

    const container = $("aiInventoryData");

    container.replaceChildren();

    const labels = {

        totalAssets: "Total Assets",

        availableAssets: "Available Assets",

        assignedAssets: "Assigned Assets",

        maintenanceAssets: "Under Maintenance",

        retiredAssets: "Retired Assets",

        totalEmployees: "Total Employees",

        totalDepartments: "Total Departments"
    };

    Object.entries(data).forEach(([key, value]) => {

        const row = document.createElement("div");

        row.className =
            "d-flex justify-content-between "
            + "border-bottom py-2 gap-3";

        const label = document.createElement("span");

        label.textContent = labels[key] || key;

        const count = document.createElement("strong");

        count.textContent = value ?? 0;

        row.append(label, count);

        container.appendChild(row);
    });
}


// ============================================
// SAFE TABLE RENDERING
// ============================================

function appendCell(row, value) {

    const cell = document.createElement("td");

    cell.textContent = value ?? "—";

    row.appendChild(cell);
}


function renderEmptyTable(tbody, columns, message) {

    const row = document.createElement("tr");

    const cell = document.createElement("td");

    cell.colSpan = columns;

    cell.className = "text-center text-muted py-4";

    cell.textContent = message;

    row.appendChild(cell);

    tbody.appendChild(row);
}


function createActionButton(
    label,
    buttonClass,
    action
) {

    const button = document.createElement("button");

    button.type = "button";

    button.className =
        `btn ${buttonClass} btn-sm`;

    button.textContent = label;

    button.addEventListener("click", async () => {

        button.disabled = true;

        try {

            await action();

        } catch (error) {

            showNotification(
                error.message,
                "danger"
            );

        } finally {

            button.disabled = false;
        }
    });

    return button;
}


function createStatusBadge(status) {

    const badge = document.createElement("span");

    const styles = {

        AVAILABLE: "bg-success",

        ASSIGNED: "bg-primary",

        MAINTENANCE: "bg-warning text-dark",

        RETIRED: "bg-secondary"
    };

    const labels = {

        AVAILABLE: "Available",

        ASSIGNED: "Assigned",

        MAINTENANCE: "Maintenance",

        RETIRED: "Retired"
    };

    badge.className =

        `badge ${styles[status] || "bg-secondary"}`;

    badge.textContent =

        labels[status] || status || "Unknown";

    return badge;
}


// ============================================
// AUTHENTICATION AND LOGOUT
// ============================================

async function logout() {

    const confirmed = window.confirm(
        "Are you sure you want to sign out?"
    );

    if (!confirmed) {

        return;
    }

    if (!state.csrfToken) {

        await loadCsrfToken();
    }

    // Spring Security requires a POST logout request
    // containing the current CSRF token.

    const form = document.createElement("form");

    form.method = "POST";

    form.action = "/logout";

    form.hidden = true;

    const csrfInput = document.createElement("input");

    csrfInput.type = "hidden";

    csrfInput.name = "_csrf";

    csrfInput.value = state.csrfToken;

    form.appendChild(csrfInput);

    document.body.appendChild(form);

    form.submit();
}


// ============================================
// EVENT HANDLING
// ============================================

function bindAsyncEvent(id, eventName, handler) {

    $(id).addEventListener(
        eventName,
        async event => {

            try {

                await handler(event);

            } catch (error) {

                showNotification(
                    error.message,
                    "danger"
                );
            }
        }
    );
}


function registerEventListeners() {

    // Navigation

    document.querySelectorAll("[data-section]")
        .forEach(button => {

            button.addEventListener("click", () => {

                showSection(button.dataset.section);
            });
        });


    // Dashboard

    bindAsyncEvent(
        "refreshDashboardButton",
        "click",
        async () => {

            await Promise.all([
                loadDashboard(),
                loadAiStatus()
            ]);

            showNotification(
                "Dashboard refreshed."
            );
        }
    );


    // Asset management

    bindAsyncEvent(
        "assetForm",
        "submit",
        saveAsset
    );

    $("assetStatus").addEventListener(
        "change",
        toggleAssetEmployeeField
    );

    $("cancelAssetEditButton").addEventListener(
        "click",
        resetAssetForm
    );

    bindAsyncEvent(
        "searchAssetsButton",
        "click",
        async () => {

            state.assetPage = 0;

            await loadAssets();
        }
    );

    bindAsyncEvent(
        "assetSearch",
        "keydown",
        async event => {

            if (event.key === "Enter") {

                event.preventDefault();

                state.assetPage = 0;

                await loadAssets();
            }
        }
    );

    bindAsyncEvent(
        "assetStatusFilter",
        "change",
        async () => {

            state.assetPage = 0;

            await loadAssets();
        }
    );

    bindAsyncEvent(
        "clearAssetFiltersButton",
        "click",
        async () => {

            $("assetSearch").value = "";

            $("assetStatusFilter").value = "";

            state.assetPage = 0;

            await loadAssets();
        }
    );

    bindAsyncEvent(
        "previousAssetPage",
        "click",
        async () => {

            if (state.assetPage > 0) {

                state.assetPage--;

                await loadAssets();
            }
        }
    );

    bindAsyncEvent(
        "nextAssetPage",
        "click",
        async () => {

            if (state.assetPage + 1
                < state.assetTotalPages) {

                state.assetPage++;

                await loadAssets();
            }
        }
    );


    // Employee management

    bindAsyncEvent(
        "employeeForm",
        "submit",
        saveEmployee
    );

    $("cancelEmployeeEditButton").addEventListener(
        "click",
        resetEmployeeForm
    );

    bindAsyncEvent(
        "searchEmployeesButton",
        "click",
        async () => {

            state.employeePage = 0;

            await loadEmployees();
        }
    );

    bindAsyncEvent(
        "employeeSearch",
        "keydown",
        async event => {

            if (event.key === "Enter") {

                event.preventDefault();

                state.employeePage = 0;

                await loadEmployees();
            }
        }
    );

    bindAsyncEvent(
        "departmentFilter",
        "change",
        async () => {

            state.employeePage = 0;

            await loadEmployees();
        }
    );

    bindAsyncEvent(
        "clearEmployeeFiltersButton",
        "click",
        async () => {

            $("employeeSearch").value = "";

            $("departmentFilter").value = "";

            state.employeePage = 0;

            await loadEmployees();
        }
    );

    bindAsyncEvent(
        "previousEmployeePage",
        "click",
        async () => {

            if (state.employeePage > 0) {

                state.employeePage--;

                await loadEmployees();
            }
        }
    );

    bindAsyncEvent(
        "nextEmployeePage",
        "click",
        async () => {

            if (state.employeePage + 1
                < state.employeeTotalPages) {

                state.employeePage++;

                await loadEmployees();
            }
        }
    );


    // Asset assignment

    bindAsyncEvent(
        "assignmentForm",
        "submit",
        assignAsset
    );

    $("cancelAssignmentButton").addEventListener(
        "click",
        closeAssignmentDialog
    );


    // AI intelligence

    bindAsyncEvent(
        "refreshAiStatusButton",
        "click",
        loadAiStatus
    );

    bindAsyncEvent(
        "generateAiButton",
        "click",
        generateAiRecommendations
    );


    // Authentication

    bindAsyncEvent(
        "logoutButton",
        "click",
        logout
    );
}
