from flask import Flask, render_template, jsonify, request
import datetime

app = Flask(__name__)

# Mock database of logs or ideas for a creative workspace
mind_space_database = [
    {
        "id": 1,
        "title": "Quantum Mechanics and Creativity",
        "category": "Science & Philosophy",
        "reflection": "Could consciousness interact with quantum coherence in brain microtubules? Some theories suggest a connection, but more empirical evidence is needed.",
        "timestamp": "2026-05-27 10:00"
    },
    {
        "id": 2,
        "title": "Aesthetic of Dark Ambient Workspaces",
        "category": "Design Systems",
        "reflection": "Warm twilight glows paired with deep space obisidan hues reduce cortical strain and enhance coding focus.",
        "timestamp": "2026-05-27 12:30"
    }
]

@app.route("/")
def home():
    current_time = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    return render_template("index.html", time=current_time, entries=mind_space_database)

@app.route("/api/entries", methods=["GET", "POST"])
def manage_entries():
    if request.method == "POST":
        data = request.json
        if not data or "title" not in data or "reflection" not in data:
            return jsonify({"error": "Missing required fields"}), 400
        
        new_entry = {
            "id": len(mind_space_database) + 1,
            "title": data["title"],
            "category": data.get("category", "General"),
            "reflection": data["reflection"],
            "timestamp": datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        }
        mind_space_database.append(new_entry)
        return jsonify(new_entry), 201
        
    return jsonify(mind_space_database)

@app.route("/api/entries/<int:entry_id>", methods=["DELETE"])
def delete_entry(entry_id):
    global mind_space_database
    initial_length = len(mind_space_database)
    mind_space_database = [e for e in mind_space_database if e["id"] != entry_id]
    if len(mind_space_database) < initial_length:
        return jsonify({"message": f"Successfully deleted item {entry_id}"}), 200
    return jsonify({"error": "Entry not found"}), 404

if __name__ == "__main__":
    # Runs the server locally on port 5000
    app.run(debug=True, host="127.0.0.1", port=5000)
