const API_URL = "http://localhost:8080/api";
let token = localStorage.getItem("jwt");

function setAuthHeader(headers = {}) {
  if (token) headers["Authorization"] = "Bearer " + token;
  return headers;
}


async function login() {
  const username = document.getElementById("login-username").value;
  const password = document.getElementById("login-password").value;

  const res = await fetch(`${API_URL}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password })
  });
  const data = await res.json();
  token = data.data.token;
  localStorage.setItem("jwt", token);
  document.getElementById("auth").style.display = "none";
  document.getElementById("notes").style.display = "block";
  loadNotes();
}

async function register() {
  const username = document.getElementById("register-username").value;
  const email = document.getElementById("register-email").value;
  const password = document.getElementById("register-password").value;

  const res = await fetch(`${API_URL}/auth/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, email, password })
  });
  const data = await res.json();
  alert("Registered! Now login.");
}

async function loadNotes() {
  const res = await fetch(`${API_URL}/notes`, {
    headers: setAuthHeader()
  });
  if (!res.ok) {
    console.error("Failed to load notes:", res.status);
    return;
  }
  const data = await res.json();
  renderNotes(data.data.content);
}

async function createNote() {
  const title = document.getElementById("note-title").value;
  const content = document.getElementById("note-content").value;

  console.log("Sending note:", { title, content });
  console.log("Token:", token);

  try {
    const res = await fetch(`${API_URL}/notes`, {
      method: "POST",
      headers: setAuthHeader({ "Content-Type": "application/json" }),
      body: JSON.stringify({ title, content })
    });

    console.log("Response status:", res.status);
    console.log("Response headers:", [...res.headers.entries()]);
    const text = await res.text();
    console.log("Response body:", text);

    if (!res.ok) {
      alert("Error creating note: " + res.status);
      return;
    }

    loadNotes();
  } catch (e) {
    console.error("Network error:", e);
    alert("Network error while creating note");
  }
}

// Render notes as cards
function renderNotes(notes) {
  const list = document.getElementById("notes-list");
  list.innerHTML = "";

  notes.forEach(note => {
    const card = document.createElement("div");
    card.className = "note-card";

    const title = document.createElement("h3");
    title.textContent = note.title;

    const content = document.createElement("p");
    content.textContent = note.content;

    const actions = document.createElement("div");
    actions.className = "note-actions";

    const delBtn = document.createElement("button");
    delBtn.textContent = "🗑 Delete";
    delBtn.onclick = () => deleteNote(note.id);

    actions.appendChild(delBtn);

    card.appendChild(title);
    card.appendChild(content);
    card.appendChild(actions);

    list.appendChild(card);
  });
}

async function deleteNote(id) {
  await fetch(`${API_URL}/notes/${id}`, {
    method: "DELETE",
    headers: setAuthHeader()
  });
  loadNotes();
}
