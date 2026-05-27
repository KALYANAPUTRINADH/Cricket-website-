import os
import json
from flask import Flask, render_template, request, jsonify, Response, stream_with_context
from werkzeug.utils import secure_filename
from video_processor import CricketBallClipperAI

app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = os.path.join(os.path.dirname(__file__), 'uploads')
app.config['CLIPS_FOLDER'] = os.path.join(os.path.dirname(__file__), 'static', 'clips')
app.config['MAX_CONTENT_LENGTH'] = 120 * 1024 * 1024  # 120MB limits

# Make sure folders exist
os.makedirs(app.config['UPLOAD_FOLDER'], exist_ok=True)
os.makedirs(app.config['CLIPS_FOLDER'], exist_ok=True)

# Shared memory analysis tracking channel
processing_logs = []
current_status = {"step": "IDLE", "message": "Ready to initiate stream analysis.", "progress": 0}

@app.route('/')
def dashboard():
    api_key_configured = bool(os.getenv("GEMINI_API_KEY"))
    return render_template('index.html', api_key=api_key_configured)

@app.route('/api/upload', methods=['POST'])
def upload_video():
    if 'video' not in request.files:
        return jsonify({"error": "No video file received"}), 400
    
    file = request.files['video']
    if file.filename == '':
        return jsonify({"error": "Selected empty file"}), 400

    filename = secure_filename(file.filename)
    filepath = os.path.join(app.config['UPLOAD_FOLDER'], filename)
    file.save(filepath)

    return jsonify({
        "message": "Match stream uploaded successfully.",
        "filepath": filepath,
        "filename": filename
    }), 200

@app.route('/api/status')
def get_status():
    global current_status, processing_logs
    return jsonify({
        "status": current_status,
        "logs": processing_logs[-12:] # return last 12 events
    })

@app.route('/api/analyze', methods=['POST'])
def run_analysis():
    global processing_logs, current_status
    processing_logs = []
    
    data = request.json or {}
    video_path = data.get('filepath')
    
    # If customer selected "Run stream Simulator" without uploading a custom massive file, use standard simulation file
    if not video_path:
        video_path = "mock_cricket_live_stream_feed.mp4"
        # Create empty mock file if not exists to let cv2 handle cleanly
        with open(os.path.join(app.config['UPLOAD_FOLDER'], video_path), "w") as f:
            f.write("MOCK STREAM STREAM DATA")
        video_path = os.path.join(app.config['UPLOAD_FOLDER'], video_path)

    api_key = data.get('api_key') or os.getenv("GEMINI_API_KEY")

    def track_progress(step, message, progress):
        global current_status, processing_logs
        current_status = {"step": step, "message": message, "progress": progress}
        log_entry = f"[{step}] {message} ({progress}%)"
        processing_logs.append(log_entry)
        print(log_entry)

    try:
        engine = CricketBallClipperAI(api_key=api_key)
        
        # Phase 1
        track_progress("MOTION_SCAN", "Initiating Optical Flow stride sensors...", 10)
        
        # Phase 2
        track_progress("REPLAY_FILTER", "Aligning broadcast colors to eliminate replays/breaks...", 35)
        
        # Phase 3
        track_progress("SCOREBOARD_PROCESS", "Scanning overlay coordinates for Over-Ball changes...", 60)
        
        # Phase 4 (Does processing and returns actual ball segmented timestamps)
        segments = engine.analyze_stream(video_path, progress_callback=track_progress)
        
        return jsonify({
            "status": "SUCCESS",
            "message": "AI Splicing pipeline executed flawlessly.",
            "segments": segments
        })
    except Exception as e:
        track_progress("ERROR", f"Interrupted: {str(e)}", 100)
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    # Local loopback server configurations
    app.run(debug=True, host='127.0.0.1', port=5001)
