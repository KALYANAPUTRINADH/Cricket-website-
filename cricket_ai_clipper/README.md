# AI Cricket Ball-by-Ball Splicing & Auto-Clipper

An automated pipeline to process raw cricket match videos or broadcasts, pinpoint bowler release events, omit slow-motion replays, and divide streams into elegant ball-by-ball segment clips labeled with standard Over syntax (e.g. `1.1`, `1.2` etc.).

---

## 🔬 Multi-Stage Processing Pipeline

This system solves the core computer vision challenges of sports stream analysis through 4 distinct stages:

```
[STAGE 1: MOTION DECODER] 
  └── Calculates frame-to-frame Optical Flow.
  └── Pinpoints localized velocity spikes in the crease area (Bowler's delivery stride).

[STAGE 2: BROADCAST REPLAY SHIELD]
  └── Track rapid color distribution swings (Logo wipes).
  └── Flags and filters slow-motion framerates and duplicate scene cuts.

[STAGE 3: OVERLAY PARSER]
  └── Track region-of-interest configurations (Scoreboard graphics).
  └── Syncs segment ends with Over-Ball counter changes on screen.

[STAGE 4: GEMINI REFINEMENT]
  └── Evaluates multimodal video frames against Gemini-3.5-Flash.
  └── Extracts context coordinates: Batter's stroke type, run events, & custom log comments.
```

---

## 🛠️ VS Code Quick-Start Installation

Get this web dashboard up and running in **VS Code** under 5 minutes:

### 1. Open Folder in VS Code
1. Export or copy the `/cricket_ai_clipper/` directory to any folder of your choice on your local drive.
2. Inside VS Code, go to **File > Open Folder...** and select the folder.

### 2. Prepare Python Environment
- Open your internal console: **Terminal > New Terminal** (or press ``Ctrl+```).
- Install the project packages:
  ```bash
  pip install -r requirements.txt
  ```

### 3. Start the Web Engine
Launch the Flask local server:
```bash
python app.py
```

### 4. Experience the Dashboard
1. The terminal will initiate the loopback thread:
   ```
   * Running on http://127.0.0.1:5001/ (Press CTRL+C to quit)
   ```
2. Hold `Ctrl` or `Cmd` and click the link to load the UI in your browser.
3. Drop a cricket stream file, select your Broadcast overlays from the dropdown, and hit **Segment Ball-by-Ball Timeline** to witness the CV Motion Peak algorithm and Gemini timeline structures organize your stream into separate video clips!
