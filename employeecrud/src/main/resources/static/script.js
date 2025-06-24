const apiUrl = 'http://localhost:8080/employees';
const form = document.getElementById('employeeForm');
const empId = document.getElementById('empId');
const empName = document.getElementById('empName');
const empAge = document.getElementById('empAge');
const empDept = document.getElementById('empDept');
const tbody = document.querySelector('#employeeTable tbody');

// Initial display of employees
function loadEmployees() {
  fetch(apiUrl)
    .then(res => res.json())
    .then(data => {
      tbody.innerHTML = '';
      data.forEach(emp => {
        const row = document.createElement('tr');
        row.innerHTML = `
          <td>${emp.id}</td>
          <td>${emp.name}</td>
          <td>${emp.age}</td>
          <td>${emp.department}</td>
          <td>
            <button onclick="editEmployee(${emp.id}, '${emp.name}', ${emp.age}, '${emp.department}')">Edit</button>
            <button onclick="deleteEmployee(${emp.id})">Delete</button>
          </td>
        `;
        tbody.appendChild(row);
      });
    });
}

// Add/Update employee
form.onsubmit = e => {
  e.preventDefault();
  const employee = {
    name: empName.value,
    age: parseInt(empAge.value),
    department: empDept.value
  };

  const method = empId.value ? 'PUT' : 'POST';
  const url = empId.value ? `${apiUrl}/${empId.value}` : apiUrl;

  fetch(url, {
    method,
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(employee)
  }).then(() => {
    form.reset();
    empId.value = '';
    loadEmployees();
  });
};

// Fill during Update
function editEmployee(id, name, age, dept) {
  empId.value = id;
  empName.value = name;
  empAge.value = age;
  empDept.value = dept;
}

// Delete employee
function deleteEmployee(id) {
  if (confirm('Are you sure you want to delete this employee?')) {
    fetch(`${apiUrl}/${id}`, { method: 'DELETE' })
      .then(() => loadEmployees());
  }
}

loadEmployees();
