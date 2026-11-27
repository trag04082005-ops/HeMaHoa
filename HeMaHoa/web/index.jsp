
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%!
    private String escapeHtml(String value) {
        if (value == null) return "";

        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }
%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hệ mã hóa</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/style.css">

    <style>
        .permutation-table-wrapper {
            margin-top: 25px;
            padding: 22px;
            background: #ffffff;
            border: 1px solid #dce5f0;
            border-radius: 12px;
        }

        .permutation-table-wrapper h3 {
            color: #173b67;
            margin-bottom: 18px;
        }

        .table-responsive {
            width: 100%;
            overflow-x: auto;
        }

        .permutation-table {
            width: 100%;
            border-collapse: collapse;
            text-align: center;
        }

        .permutation-table th,
        .permutation-table td {
            border: 1px solid #dce5f0;
            padding: 13px;
        }

        .permutation-table thead th {
            background: #eaf2fc;
            color: #173b67;
        }

        .permutation-table tbody th {
            background: #f3f6fa;
        }

        .permutation-table td {
            font-weight: 600;
            color: #263b53;
        }

        .permutation-table-wrapper .column-order {
            margin-top: 18px;
            color: #173b67;
        }
    </style>
</head>

<body>

<header class="header">
    <div class="header-content">
        <a href="#home" class="logo">CRYPTO</a>
        <nav>
            <a href="#home">Trang chủ</a>
            <a href="#algorithms">Thuật toán</a>
        </nav>
    </div>
</header>

<section class="hero" id="home">
    <div class="hero-content">
        <span class="tag">CRYPTOGRAPHY SYSTEM</span>
        <h1>HỆ MÃ HÓA</h1>
        <p>
            Tìm hiểu và thực hiện các thuật toán mã hóa,
            giải mã và bảo mật thông tin.
        </p>
        <a href="#algorithms" class="hero-button">
            Khám phá thuật toán
        </a>
    </div>
</section>

<main class="container">

    <section id="algorithms" class="algorithm-section">
        <div class="section-heading">
            <span>OUR ALGORITHMS</span>
            <h2>Danh sách thuật toán</h2>
            <p>Lựa chọn thuật toán bạn muốn tìm hiểu và sử dụng.</p>
        </div>

        <div class="algorithm-grid">

            <button type="button" class="algorithm-card" data-algorithm="caesar">
                <div class="number">01</div>
                <h3>Caesar</h3>
                <p>Mã hóa thay thế bằng cách dịch chuyển ký tự.</p>
                <span class="card-link">Chọn thuật toán →</span>
            </button>

            <button type="button" class="algorithm-card" data-algorithm="affine">
                <div class="number">02</div>
                <h3>Affine</h3>
                <p>Mã hóa sử dụng phép toán modulo.</p>
                <span class="card-link">Chọn thuật toán →</span>
            </button>

            <button type="button" class="algorithm-card" data-algorithm="playfair">
                <div class="number">03</div>
                <h3>Playfair</h3>
                <p>Mã hóa theo từng cặp ký tự bằng ma trận 5x5.</p>
                <span class="card-link">Chọn thuật toán →</span>
            </button>

            <button type="button" class="algorithm-card" data-algorithm="hill">
                <div class="number">04</div>
                <h3>Hill</h3>
                <p>Mã hóa bằng phép nhân ma trận modulo 26.</p>
                <span class="card-link">Chọn thuật toán →</span>
            </button>

            <button type="button" class="algorithm-card" data-algorithm="hoanvi">
                <div class="number">05</div>
                <h3>Hoán vị hàng</h3>
                <p>Mã hóa bằng cách thay đổi vị trí ký tự.</p>
                <span class="card-link">Chọn thuật toán →</span>
            </button>

            <button type="button" class="algorithm-card" data-algorithm="des">
                <div class="number">06</div>
                <h3>DES</h3>
                <p>Thuật toán mã hóa khối đối xứng.</p>
                <span class="card-link">Chọn thuật toán →</span>
            </button>

            <button type="button" class="algorithm-card" data-algorithm="vigenere">
                <div class="number">07</div>
                <h3>Vigenere</h3>
                <p>Mã hóa sử dụng khóa dạng chuỗi ký tự.</p>
                <span class="card-link">Chọn thuật toán →</span>
            </button>

            <button type="button" class="algorithm-card" data-algorithm="diffiehellman">
                <div class="number">08</div>
                <h3>Diffie-Hellman</h3>
                <p>Trao đổi khóa bí mật giữa hai bên.</p>
                <span class="card-link">Chọn thuật toán →</span>
            </button>

            <button type="button" class="algorithm-card" data-algorithm="rsa">
                <div class="number">09</div>
                <h3>RSA chữ ký số</h3>
                <p>Ký số và xác thực chữ ký điện tử.</p>
                <span class="card-link">Chọn thuật toán →</span>
            </button>

        </div>
    </section>

    <section id="algorithmPanel" class="algorithm-panel" style="display:none;">

        <div class="section-heading">
            <span>ENCRYPTION TOOL</span>
            <h2 id="algorithmTitle">Thuật toán</h2>
            <p id="algorithmDescription"></p>
        </div>

        <form method="post"
              action="${pageContext.request.contextPath}/crypto"
              id="cryptoForm">

            <input type="hidden" name="algorithm" id="algorithm">
            <input type="hidden" name="originalLength" id="originalLength">

            <div id="inputFields"></div>
            <div id="operationButtons" class="operation-buttons"></div>
        </form>

        <% if (request.getAttribute("error") != null) { %>
            <div class="result-box error-box">
                <h3>Thông báo lỗi</h3>
                <pre><%= escapeHtml(String.valueOf(request.getAttribute("error"))) %></pre>
            </div>
        <% } %>

        <% if (request.getAttribute("result") != null) { %>
            <div class="result-box">
                <h3>Kết quả</h3>
                <pre><%= escapeHtml(String.valueOf(request.getAttribute("result"))) %></pre>
            </div>
        <% } %>

        <!-- Khu vuc hien thi bang hoan vi hang -->
        <div id="permutationTable"></div>

    </section>

</main>

<footer>
    <h3>HỆ MÃ HÓA</h3>
    <p>Bài tập lớn môn An toàn và bảo mật thông tin</p>
    <p>© 2026 - Crypto System</p>
</footer>

<div id="savedFormData" hidden>

    <span id="saved_algorithm"><%= escapeHtml(request.getParameter("algorithm")) %></span>

    <span id="saved_operation"><%= escapeHtml(request.getParameter("operation")) %></span>

    <span id="saved_originalLength"><%= escapeHtml(
        request.getAttribute("originalLength") != null
            ? String.valueOf(request.getAttribute("originalLength"))
            : request.getParameter("originalLength")
    ) %></span>

    <span id="saved_text"><%= escapeHtml(request.getParameter("text")) %></span>
    <span id="saved_key"><%= escapeHtml(request.getParameter("key")) %></span>

    <span id="saved_a"><%= escapeHtml(request.getParameter("a")) %></span>
    <span id="saved_b"><%= escapeHtml(request.getParameter("b")) %></span>

    <span id="saved_dimension"><%= escapeHtml(request.getParameter("dimension")) %></span>
    <span id="saved_keyMatrix"><%= escapeHtml(request.getParameter("keyMatrix")) %></span>

    <span id="saved_p"><%= escapeHtml(request.getParameter("p")) %></span>
    <span id="saved_g"><%= escapeHtml(request.getParameter("g")) %></span>
    <span id="saved_privateA"><%= escapeHtml(request.getParameter("privateA")) %></span>
    <span id="saved_privateB"><%= escapeHtml(request.getParameter("privateB")) %></span>

    <span id="saved_keySize"><%= escapeHtml(request.getParameter("keySize")) %></span>
    <span id="saved_privateKey"><%= escapeHtml(request.getParameter("privateKey")) %></span>
    <span id="saved_publicKey"><%= escapeHtml(request.getParameter("publicKey")) %></span>
    <span id="saved_signature"><%= escapeHtml(request.getParameter("signature")) %></span>

</div>

<script>

const algorithms = {

    caesar: {
        title: "Caesar",
        description: "Mã hóa và giải mã bằng cách dịch chuyển ký tự.",
        fields: `
            <div class="form-group">
                <label>Bản rõ hoặc bản mã</label>
                <textarea name="text" rows="4" placeholder="Nhập nội dung cần xử lý..." required></textarea>
            </div>
            <div class="form-group">
                <label>Khóa dịch chuyển</label>
                <input type="number" name="key" placeholder="Ví dụ: 3" required>
            </div>
        `,
        buttons: `
            <button type="submit" name="operation" value="encrypt">Mã hóa</button>
            <button type="submit" name="operation" value="decrypt">Giải mã</button>
        `
    },

    affine: {
        title: "Affine",
        description: "Mã hóa bằng công thức toán học modulo 26.",
        fields: `
            <div class="form-group">
                <label>Bản rõ hoặc bản mã</label>
                <textarea name="text" rows="4" placeholder="Nhập nội dung cần xử lý..." required></textarea>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label>Khóa a</label>
                    <input type="number" name="a" required>
                </div>
                <div class="form-group">
                    <label>Khóa b</label>
                    <input type="number" name="b" required>
                </div>
            </div>
        `,
        buttons: `
            <button type="submit" name="operation" value="encrypt">Mã hóa</button>
            <button type="submit" name="operation" value="decrypt">Giải mã</button>
        `
    },

    playfair: {
        title: "Playfair",
        description: "Mã hóa theo từng cặp ký tự bằng ma trận 5x5.",
        fields: `
            <div class="form-group">
                <label>Bản rõ hoặc bản mã</label>
                <textarea name="text" rows="4" placeholder="Nhập nội dung cần xử lý..." required></textarea>
            </div>
            <div class="form-group">
                <label>Khóa</label>
                <input type="text" name="key" placeholder="Nhập khóa Playfair..." required>
            </div>
        `,
        buttons: `
            <button type="submit" name="operation" value="encrypt">Mã hóa</button>
            <button type="submit" name="operation" value="decrypt">Giải mã</button>
        `
    },

    hill: {
        title: "Hill",
        description: "Mã hóa bằng phép nhân ma trận modulo 26.",
        fields: `
    <div class="form-group">
        <label>Bản rõ hoặc bản mã</label>
        <textarea name="text" rows="4"
            placeholder="Nhập nội dung cần xử lý..."
            required></textarea>
    </div>

    <div class="form-group">
        <label>Kích thước ma trận khóa</label>
        <select name="dimension" id="hillDimension"
                required onchange="updateHillExample()">
            <option value="2">Ma trận 2 x 2</option>
            <option value="3">Ma trận 3 x 3</option>
            <option value="4">Ma trận 4 x 4</option>
        </select>
    </div>

    <div class="form-group">
        <label>Ma trận khóa</label>

        <textarea name="keyMatrix"
            id="hillKeyMatrix"
            rows="5"
            placeholder="Nhập ma trận khóa..."
            required></textarea>

        <small id="hillGuide"></small>

        <small>
            Định thức ma trận phải nguyên tố cùng nhau với 26
            để có thể giải mã.
        </small>

        <button type="button"
            onclick="generateHillKey()"
            style="margin-top:12px;">
            Gợi ý khóa
        </button>
    </div>

        `,
        buttons: `
            <button type="submit" name="operation" value="encrypt">Mã hóa</button>
            <button type="submit" name="operation" value="decrypt">Giải mã</button>
        `
    },

    hoanvi: {
        title: "Hoán vị hàng",
        description: "Mã hóa bằng cách thay đổi vị trí ký tự theo thứ tự các cột của khóa.",
        fields: `
            <div class="form-group">
                <label>Bản rõ hoặc bản mã</label>
                <textarea name="text" rows="4" placeholder="Nhập nội dung cần xử lý..." required></textarea>
            </div>
            <div class="form-group">
                <label>Khóa hoán vị</label>
                <input type="text" name="key" placeholder="Ví dụ: CAB" required>
                <small>Khóa chỉ được chứa chữ cái và chữ số.</small>
            </div>
        `,
        buttons: `
            <button type="submit" name="operation" value="encrypt">Mã hóa</button>
            <button type="submit" name="operation" value="decrypt">Giải mã</button>
        `
    },

    des: {
    title: "DES",
    description: "Mã hóa và giải mã dữ liệu bằng thuật toán DES.",
    fields: `
        <div class="form-group">
            <label>Bản rõ hoặc bản mã</label>
            <textarea name="text" rows="4"
                placeholder="Nhập nội dung cần mã hóa hoặc giải mã..."
                required></textarea>
        </div>

        <div class="form-group">
            <label>Khóa DES</label>
            <input type="text" name="key"
                placeholder="Nhập khóa DES..."
                required>
            <small>
                Khóa phải đúng 8 byte UTF-8 hoặc 16 ký tự Hex.
                Không chấp nhận khóa yếu hoặc bán yếu.
            </small>
        </div>

        <div class="form-group">
            <label>Chế độ mã hóa</label>
            <select name="mode">
                <option value="ECB" selected>ECB</option>
                <option value="CBC">CBC</option>
            </select>
        </div>

        <div class="form-group">
            <label>Định dạng dữ liệu</label>
            <select name="format">
                <option value="Văn bản" selected>Văn bản (UTF-8/Base64)</option>
                <option value="Hex">Hex</option>
            </select>
        </div>

        <div class="form-group">
            <label>Kiểu đệm</label>
            <select name="padding">
                <option value="PKCS5" selected>PKCS5Padding</option>
                <option value="Không đệm">NoPadding</option>
            </select>
            <small>
                NoPadding yêu cầu dữ liệu có độ dài là bội số của 8 byte.
            </small>
        </div>
    `,
    buttons: `
        <button type="submit" name="operation" value="encrypt">
            Mã hóa
        </button>
        <button type="submit" name="operation" value="decrypt">
            Giải mã
        </button>
    `
},

    vigenere: {
        title: "Vigenere",
        description: "Mã hóa bằng khóa dạng chuỗi ký tự.",
        fields: `
            <div class="form-group">
                <label>Bản rõ hoặc bản mã</label>
                <textarea name="text" rows="4" placeholder="Nhập nội dung cần xử lý..." required></textarea>
            </div>
            <div class="form-group">
                <label>Khóa</label>
                <input type="text" name="key" placeholder="Chỉ sử dụng chữ cái A-Z..." required>
            </div>
        `,
        buttons: `
            <button type="submit" name="operation" value="encrypt">Mã hóa</button>
            <button type="submit" name="operation" value="decrypt">Giải mã</button>
        `
    },

    diffiehellman: {
        title: "Diffie-Hellman",
        description: "Trao đổi khóa bí mật giữa hai bên thông qua số nguyên lớn.",
        fields: `
            <div class="form-group">
                <label>Số nguyên tố p</label>
                <input type="text" name="p" placeholder="Nhập p..." required>
            </div>
            <div class="form-group">
                <label>Cơ số g</label>
                <input type="text" name="g" placeholder="Nhập g..." required>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label>Khóa bí mật A</label>
                    <input type="text" name="privateA" placeholder="Nhập khóa A..." required>
                </div>
                <div class="form-group">
                    <label>Khóa bí mật B</label>
                    <input type="text" name="privateB" placeholder="Nhập khóa B..." required>
                </div>
            </div>
        `,
        buttons: `
            <button type="submit" name="operation" value="exchange">Thực hiện trao đổi khóa</button>
        `
    },

    rsa: {
        title: "RSA chữ ký số",
        description: "Tạo khóa, ký số và xác thực chữ ký điện tử.",
        fields: `
            <div class="form-group">
                <label>Nội dung cần ký hoặc xác thực</label>
                <textarea name="text" rows="4" placeholder="Nhập nội dung..."></textarea>
            </div>
            <div class="form-group">
                <label>Kích thước khóa RSA</label>
                <select name="keySize">
                    <option value="2048">2048 bit</option>
                    <option value="3072">3072 bit</option>
                    <option value="4096">4096 bit</option>
                </select>
            </div>
            <div class="form-group">
                <label>Khóa riêng (Private Key - Base64)</label>
                <textarea name="privateKey" rows="3" placeholder="Nhập khóa riêng khi ký số..."></textarea>
            </div>
            <div class="form-group">
                <label>Khóa công khai (Public Key - Base64)</label>
                <textarea name="publicKey" rows="3" placeholder="Nhập khóa công khai khi xác thực..."></textarea>
            </div>
            <div class="form-group">
                <label>Chữ ký số (Base64)</label>
                <textarea name="signature" rows="3" placeholder="Nhập chữ ký cần xác thực..."></textarea>
            </div>
        `,
        buttons: `
            <button type="submit" name="operation" value="generate">Tạo cặp khóa</button>
            <button type="submit" name="operation" value="sign">Ký số</button>
            <button type="submit" name="operation" value="verify">Xác thực chữ ký</button>
        `
    }
};


// ==================== HILL ====================

function updateHillExample() {

    const dimension = document.getElementById("hillDimension");
    const matrix = document.getElementById("hillKeyMatrix");
    const guide = document.getElementById("hillGuide");

    if (!dimension || !matrix || !guide) return;

    const n = parseInt(dimension.value, 10);

    const examples = {
        2: "3 3\n2 5",
        3: "1 2 3\n0 1 4\n5 6 0",
        4: "1 0 0 0\n0 1 0 0\n0 0 1 0\n0 0 0 1"
    };

    matrix.placeholder = examples[n];

    guide.textContent =
        `Nhập ${n} hàng, mỗi hàng gồm ${n} số nguyên từ 0 đến 25. Các số cách nhau bằng dấu cách.`;
}


// Chuan hoa giong Hill.java

// Tinh dinh thuc ma tran
function hillDeterminant(matrix) {

    const n = matrix.length;

    if (n === 1) {
        return matrix[0][0];
    }

    if (n === 2) {
        return matrix[0][0] * matrix[1][1]
             - matrix[0][1] * matrix[1][0];
    }

    let det = 0;

    for (let col = 0; col < n; col++) {

        const minor = [];

        for (let i = 1; i < n; i++) {

            const row = [];

            for (let j = 0; j < n; j++) {

                if (j !== col) {
                    row.push(matrix[i][j]);
                }
            }

            minor.push(row);
        }

        const sign = col % 2 === 0 ? 1 : -1;

        det += sign * matrix[0][col]
                   * hillDeterminant(minor);
    }

    return det;
}


// Tim UCLN
function hillGCD(a, b) {

    a = Math.abs(a);
    b = Math.abs(b);

    while (b !== 0) {

        const temp = b;
        b = a % b;
        a = temp;
    }

    return a;
}


// Sinh ma tran khoa hop le
function generateHillKey() {

    const dimension = document.getElementById("hillDimension");
    const matrixInput = document.getElementById("hillKeyMatrix");
    const guide = document.getElementById("hillGuide");

    if (!dimension || !matrixInput || !guide) return;

    const n = parseInt(dimension.value, 10);

    let key;
    let det;
    let attempts = 0;

    do {

        key = [];

        // Sinh ma tran ngau nhien
        for (let i = 0; i < n; i++) {

            const row = [];

            for (let j = 0; j < n; j++) {

                row.push(Math.floor(Math.random() * 26));
            }

            key.push(row);
        }

        // Tinh dinh thuc
        det = hillDeterminant(key);

        attempts++;

        if (attempts > 10000) {
            alert("Không thể sinh khóa hợp lệ!");
            return;
        }

    } while (hillGCD(det, 26) !== 1);

    // Hien thi ma tran
    matrixInput.value = key
        .map(row => row.join(" "))
        .join("\n");

    guide.textContent =
        "Đã sinh khóa hợp lệ! Định thức = "
        + det
        + ", gcd("
        + Math.abs(det)
        + ", 26) = 1. Có thể mã hóa và giải mã.";
}


// ==================== HIEN THI BANG HOAN VI HANG ====================

function renderHoanViTable(text, key) {

    const container = document.getElementById("permutationTable");

    if (!container || !text || !key) return;

    container.innerHTML = "";

    const chars = Array.from(text);
    const keyChars = Array.from(key.toUpperCase());

    const cols = keyChars.length;
    const rows = Math.ceil(chars.length / cols);

    // Sap xep thu tu cot theo khoa
    const order = Array.from(
        { length: cols },
        (_, i) => i
    );

    order.sort((a, b) => {

        const c1 = keyChars[a].charCodeAt(0);
        const c2 = keyChars[b].charCodeAt(0);

        if (c1 !== c2) {
            return c1 - c2;
        }

        return a - b;
    });

    // Tao bang
    const table = document.createElement("table");
    table.className = "permutation-table";

    // Tieu de
    const thead = document.createElement("thead");
    const headerRow = document.createElement("tr");

    const firstHeader = document.createElement("th");
    firstHeader.textContent = "Hàng";
    headerRow.appendChild(firstHeader);

    for (let col = 0; col < cols; col++) {

        const th = document.createElement("th");

        th.textContent =
            "Cột " + (col + 1) + " (" + keyChars[col] + ")";

        headerRow.appendChild(th);
    }

    thead.appendChild(headerRow);
    table.appendChild(thead);

    // Noi dung bang
    const tbody = document.createElement("tbody");

    for (let row = 0; row < rows; row++) {

        const tr = document.createElement("tr");

        const rowHeader = document.createElement("th");
        rowHeader.textContent = row + 1;
        tr.appendChild(rowHeader);

        for (let col = 0; col < cols; col++) {

            const td = document.createElement("td");
            const index = row * cols + col;

            td.textContent =
                index < chars.length ? chars[index] : "-";

            tr.appendChild(td);
        }

        tbody.appendChild(tr);
    }

    table.appendChild(tbody);

    // Khung bang
    const wrapper = document.createElement("div");
    wrapper.className = "permutation-table-wrapper";

    const heading = document.createElement("h3");
    heading.textContent = "Bảng sắp xếp ký tự";

    const responsive = document.createElement("div");
    responsive.className = "table-responsive";
    responsive.appendChild(table);

    // Thu tu doc cot
    const orderText = document.createElement("div");
    orderText.className = "column-order";

    const orderLabel = document.createElement("strong");
    orderLabel.textContent = "Thứ tự đọc cột: ";

    orderText.appendChild(orderLabel);
    orderText.append(
        order.map(col => "Cột " + (col + 1)).join(" → ")
    );

    wrapper.appendChild(heading);
    wrapper.appendChild(responsive);
    wrapper.appendChild(orderText);

    container.appendChild(wrapper);
}


// ==================== KHOI PHUC DU LIEU ====================

function restoreFormData() {

    const savedFields = [
        "text", "key", "a", "b",
        "dimension", "keyMatrix",
        "p", "g", "privateA", "privateB",
        "keySize", "privateKey", "publicKey", "signature"
    ];

    savedFields.forEach(name => {

        const savedElement =
            document.getElementById("saved_" + name);

        const inputElement =
            fields.querySelector('[name="' + name + '"]');

        if (savedElement && inputElement) {
            inputElement.value = savedElement.textContent;
        }
    });

    const savedLength =
        document.getElementById("saved_originalLength");

    const lengthInput =
        document.getElementById("originalLength");

    if (savedLength && lengthInput) {

        const length = savedLength.textContent.trim();

        if (length !== "") {
            lengthInput.value = length;
            sessionStorage.setItem("hillOriginalLength", length);
        }
    }
}


// ==================== CHON THUAT TOAN ====================

const cards = document.querySelectorAll(".algorithm-card");
const panel = document.getElementById("algorithmPanel");
const title = document.getElementById("algorithmTitle");
const description = document.getElementById("algorithmDescription");
const fields = document.getElementById("inputFields");
const buttons = document.getElementById("operationButtons");
const algorithmInput = document.getElementById("algorithm");
const cryptoForm = document.getElementById("cryptoForm");
const originalLengthInput = document.getElementById("originalLength");

function selectAlgorithm(name, restoreData = false) {

    const item = algorithms[name];

    if (!item) return;

    algorithmInput.value = name;
    title.textContent = item.title;
    description.textContent = item.description;

    fields.innerHTML = item.fields;
    buttons.innerHTML = item.buttons;

    panel.style.display = "block";

    // An bang cu khi chon thuat toan khac
    document.getElementById("permutationTable").innerHTML = "";

    cards.forEach(card => {
        card.classList.toggle(
            "active",
            card.dataset.algorithm === name
        );
    });

    if (name === "hill") {
        updateHillExample();
    } else {
        originalLengthInput.value = "";
    }

    if (restoreData) {
        restoreFormData();
    }

    panel.scrollIntoView({
        behavior: "smooth",
        block: "start"
    });
}


// Gan su kien chon thuat toan

cards.forEach(card => {

    card.addEventListener("click", function() {
        selectAlgorithm(this.dataset.algorithm);
    });
});


// ==================== XU LY DO DAI HILL ====================

cryptoForm.addEventListener("submit", function(event) {

    const selectedAlgorithm = algorithmInput.value;
    const operationButton = event.submitter;

    if (selectedAlgorithm !== "hill" || !operationButton) return;

    const operation = operationButton.value;
    const textInput = fields.querySelector('[name="text"]');

    if (!textInput) return;

    if (operation === "encrypt") {

        const length = getHillOriginalLength(textInput.value);

        if (length === 0) {
            event.preventDefault();
            alert("Bản rõ không có ký tự hợp lệ để mã hóa!");
            return;
        }

        originalLengthInput.value = String(length);

        sessionStorage.setItem(
            "hillOriginalLength",
            String(length)
        );
    }

    if (operation === "decrypt") {

        const savedLength =
            sessionStorage.getItem("hillOriginalLength");

        if (savedLength !== null) {
            originalLengthInput.value = savedLength;
        }
    }
});


// ==================== KHOI PHUC SAU POST ====================

const previousAlgorithm =
    document.getElementById("saved_algorithm").textContent.trim();

const previousOperation =
    document.getElementById("saved_operation").textContent.trim();

if (algorithms[previousAlgorithm]) {

    selectAlgorithm(previousAlgorithm, true);

    // Hien thi bang neu vua ma hoa Hoan vi hang
    if (
        previousAlgorithm === "hoanvi" &&
        previousOperation === "encrypt"
    ) {

        const savedText =
            document.getElementById("saved_text").textContent;

        const savedKey =
            document.getElementById("saved_key").textContent;

        renderHoanViTable(savedText, savedKey);
    }

    // Khoi phuc do dai ban ro Hill
    if (previousAlgorithm === "hill") {

        const savedLength =
            document.getElementById("saved_originalLength").textContent.trim();

        if (savedLength !== "") {
            sessionStorage.setItem("hillOriginalLength", savedLength);
        }
    }
}

</script>

</body>
</html>
