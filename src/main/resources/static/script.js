const API_BASE = "http://localhost:8080/api";

// ---------- TAB SWITCHING ----------
const tabButtons = document.querySelectorAll(".tab-btn");
const tabSections = document.querySelectorAll(".tab-section");

tabButtons.forEach(btn => {
    btn.addEventListener("click", () => {
        tabButtons.forEach(b => b.classList.remove("active"));
        tabSections.forEach(s => s.classList.remove("active"));

        btn.classList.add("active");
        document.getElementById(btn.dataset.tab).classList.add("active");

        document.getElementById("bookSearchInput").value = "";
        document.getElementById("studentSearchInput").value = "";

        if (btn.dataset.tab === "books") loadBooks();
        if (btn.dataset.tab === "students") loadStudents();
        if (btn.dataset.tab === "records") loadRecords();
    });
});

// ---------- LOAD BOOKS ----------
// ---------- LOAD BOOKS ----------
let allBooks = [];

async function loadBooks() {
    const res = await fetch(`${API_BASE}/books`);
    allBooks = await res.json();
    renderBooks(allBooks);
}

function renderBooks(books) {
    const tbody = document.getElementById("booksTableBody");
    tbody.innerHTML = "";

    if (books.length === 0) {
        tbody.innerHTML = `<tr><td colspan="4" class="empty-state">No books found.</td></tr>`;
        return;
    }

    books.forEach(book => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${book.title}</td>
            <td>${book.author}</td>
            <td>${book.isbn}</td>
            <td>${book.copiesAvailable}</td>
        `;
        tbody.appendChild(row);
    });
}

document.getElementById("bookSearchInput").addEventListener("input", (e) => {
    const query = e.target.value.toLowerCase();
    const filtered = allBooks.filter(book =>
        book.title.toLowerCase().includes(query) ||
        book.author.toLowerCase().includes(query) ||
        book.isbn.toLowerCase().includes(query)
    );
    renderBooks(filtered);
});

// ---------- LOAD STUDENTS ----------
// ---------- LOAD STUDENTS ----------
let allStudents = [];

async function loadStudents() {
    const res = await fetch(`${API_BASE}/students`);
    allStudents = await res.json();
    renderStudents(allStudents);
}

function renderStudents(students) {
    const tbody = document.getElementById("studentsTableBody");
    tbody.innerHTML = "";

    if (students.length === 0) {
        tbody.innerHTML = `<tr><td colspan="2" class="empty-state">No students found.</td></tr>`;
        return;
    }

    students.forEach(student => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${student.studentId}</td>
            <td>${student.studentName}</td>
        `;
        tbody.appendChild(row);
    });
}

document.getElementById("studentSearchInput").addEventListener("input", (e) => {
    const query = e.target.value.toLowerCase();
    const filtered = allStudents.filter(student =>
        student.studentId.toLowerCase().includes(query) ||
        student.studentName.toLowerCase().includes(query)
    );
    renderStudents(filtered);
});

// ---------- LOAD ISSUE RECORDS ----------
async function loadRecords() {
    const res = await fetch(`${API_BASE}/issues`);
    const records = await res.json();

    const tbody = document.getElementById("recordsTableBody");
    tbody.innerHTML = "";

    records.forEach(record => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${record.book.title}</td>
            <td>${record.book.isbn}</td>
            <td>${record.student.studentName} (${record.student.studentId})</td>
            <td>${record.issueDate}</td>
            <td>${record.dueDate}</td>
        `;
        tbody.appendChild(row);
    });
}

// ---------- ADD BOOK FORM TOGGLE ----------
document.getElementById("showAddBookForm").addEventListener("click", () => {
    document.getElementById("addBookForm").classList.toggle("hidden");
});

document.getElementById("cancelAddBook").addEventListener("click", () => {
    document.getElementById("addBookForm").classList.add("hidden");
});

// ---------- SUBMIT ADD BOOK ----------
document.getElementById("submitAddBook").addEventListener("click", async () => {
    const title = document.getElementById("bookTitle").value.trim();
    const author = document.getElementById("bookAuthor").value.trim();
    const isbn = document.getElementById("bookIsbn").value.trim();
    const copiesAvailable = parseInt(document.getElementById("bookCopies").value);

    const messageEl = document.getElementById("addBookMessage");

    if (!title || !author || !isbn || isNaN(copiesAvailable) || copiesAvailable < 0) {
        messageEl.textContent = "Please fill in all fields with valid values.";
        messageEl.className = "form-message error";
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/books`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ title, author, isbn, copiesAvailable })
        });

        const data = await res.json().catch(() => null);

        if (res.ok) {
            messageEl.textContent = "Book added successfully!";
            messageEl.className = "form-message success";
            document.getElementById("bookTitle").value = "";
            document.getElementById("bookAuthor").value = "";
            document.getElementById("bookIsbn").value = "";
            document.getElementById("bookCopies").value = "";
            loadBooks();
        } else {
            messageEl.textContent = data?.message || "Failed to add book.";
            messageEl.className = "form-message error";
        }
    } catch (err) {
        messageEl.textContent = "Error connecting to server.";
        messageEl.className = "form-message error";
    }
});

// ---------- ADD STUDENT FORM TOGGLE ----------
document.getElementById("showAddStudentForm").addEventListener("click", () => {
    document.getElementById("addStudentForm").classList.toggle("hidden");
});

document.getElementById("cancelAddStudent").addEventListener("click", () => {
    document.getElementById("addStudentForm").classList.add("hidden");
});

// ---------- SUBMIT ADD STUDENT ----------
document.getElementById("submitAddStudent").addEventListener("click", async () => {
    const studentId = document.getElementById("studentIdInput").value.trim();
    const studentName = document.getElementById("studentNameInput").value.trim();

    const messageEl = document.getElementById("addStudentMessage");

    if (!studentId || !studentName) {
        messageEl.textContent = "Please fill in both fields.";
        messageEl.className = "form-message error";
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/students`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ studentId, studentName })
        });

        const data = await res.json().catch(() => null);

        if (res.ok) {
            messageEl.textContent = "Student added successfully!";
            messageEl.className = "form-message success";
            document.getElementById("studentIdInput").value = "";
            document.getElementById("studentNameInput").value = "";
            loadStudents();
        } else {
            messageEl.textContent = data?.message || "Failed to add student.";
            messageEl.className = "form-message error";
        }
    } catch (err) {
        messageEl.textContent = "Error connecting to server.";
        messageEl.className = "form-message error";
    }
});

// ---------- ISSUE BOOK ----------
document.getElementById("submitIssue").addEventListener("click", async () => {
    const isbn = document.getElementById("issueIsbn").value;
    const studentId = document.getElementById("issueStudentId").value;

    const messageEl = document.getElementById("issueMessage");

    try {
        const res = await fetch(`${API_BASE}/issue?isbn=${isbn}&studentId=${studentId}`, {
            method: "POST"
        });

        const text = await res.text();

        if (res.ok) {
            messageEl.textContent = text;
            messageEl.className = "form-message success";
            document.getElementById("issueIsbn").value = "";
            document.getElementById("issueStudentId").value = "";
        } else {
            let msg = "Failed to issue book.";
            try {
                const data = JSON.parse(text);
                msg = data.message || msg;
            } catch (e) {}
            messageEl.textContent = msg;
            messageEl.className = "form-message error";
        }
    } catch (err) {
        messageEl.textContent = "Error connecting to server.";
        messageEl.className = "form-message error";
    }
});

// ---------- RETURN BOOK ----------
document.getElementById("submitReturn").addEventListener("click", async () => {
    const isbn = document.getElementById("returnIsbn").value;
    const studentId = document.getElementById("returnStudentId").value;

    const messageEl = document.getElementById("returnMessage");

    try {
        const res = await fetch(`${API_BASE}/return?isbn=${isbn}&studentId=${studentId}`, {
            method: "POST"
        });

        const text = await res.text();

        if (res.ok) {
            messageEl.textContent = text;
            messageEl.className = "form-message success";
            document.getElementById("returnIsbn").value = "";
            document.getElementById("returnStudentId").value = "";
        } else {
            let msg = "Failed to return book.";
            try {
                const data = JSON.parse(text);
                msg = data.message || msg;
            } catch (e) {}
            messageEl.textContent = msg;
            messageEl.className = "form-message error";
        }
    } catch (err) {
        messageEl.textContent = "Error connecting to server.";
        messageEl.className = "form-message error";
    }
});

// ---------- INITIAL LOAD ----------
loadBooks();