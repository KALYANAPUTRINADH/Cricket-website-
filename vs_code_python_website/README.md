# Cosmic Mind Space Python Website (VS Code Setup Guide)

This is a beautiful, modern, high-performance local website built in **Python** using the **Flask** microframework, specifically tailored for setup and running inside **VS Code**.

---

## 🛠️ VS Code Quick Setup Instructions

Follow these five simple steps to run this web application locally on your computer inside VS Code:

### 1. Download & Extract Website Directory
Extract or move the `vs_code_python_website` directory to any folder of your choice on your local machine.

### 2. Open Directory in VS Code
1. Open **VS Code**.
2. Go to **File > Open Folder...** (or press `Cmd+O` on macOS / `Ctrl+O` on Windows).
3. Choose the `vs_code_python_website` folder containing `app.py`.

### 3. Install Python Extension (If not already installed)
1. Head to the **Extensions** tab on the left sidebar in VS Code (or press `Cmd+Shift+X` / `Ctrl+Shift+X`).
2. Search for `Python` (created by Microsoft) and click **Install**.

### 4. Set Up Python Flask Dependency
1. Open the built-in terminal in VS Code: **Terminal > New Terminal** (or press ``Cmd+``` / ``Ctrl+```).
2. Install **Flask** via raw pip command:
   ```bash
   pip install flask
   ```

### 5. Launch the Web Server
Run the Flask server by typing this command in your VS Code terminal:
```bash
python app.py
```

### 6. Access your Website
Once run successfully, the terminal will display:
```
* Running on http://127.0.0.1:5000/ (Press CTRL+C to quit)
```
Simply hold `Cmd` or `Ctrl` and click the link in your terminal, or copy and paste `http://127.0.0.1:5000/` into any browser to view and experience your brand new Cosmic Mind Space Python site!

---

## 🚀 Features Enabled
- **Direct Flask REST API Intersections (`/api/entries`)**: Create and delete real thoughts that persist dynamically inside local Flask server memory context.
- **Premium Cosmic Dark UI Style**: Utilizing beautiful indigo/teal space colors, perfect spacing, hover borders, and intuitive layout flow.
- **Responsive Web Layout**: Flowing correctly to split grid on large laptop screens and canonical columns on smaller devices.
