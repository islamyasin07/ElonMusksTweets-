import java.nio.file.{Files, Paths}
import org.apache.spark.rdd.RDD
import java.io.File

object ReportGenerator {
  def saveReport(
      outputPath: String,
      percent1: Double,
      percent2: Double,
      mean: Double,
      std: Double,
      dailyCounts: RDD[(String, String, Int)]
  ): Unit = {

    new File(outputPath).mkdirs()

    val statsJson =
      s"""{"percent1":$percent1,"percent2":$percent2,"mean":$mean,"std":$std}"""

    val html =
s"""
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Elon Musk Tweets Dashboard</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

  <style>
    #bgVideo {
      position: fixed;
      top: 50%;
      left: 50%;
      min-width: 100%;
      min-height: 100%;
      width: auto;
      height: auto;
      transform: translate(-50%, -50%);
      z-index: -1;
      object-fit: cover;
      object-position: center center;
      filter: brightness(0.35);
    }

    body {
      margin: 0;
      overflow-x: hidden;
      font-family: 'Segoe UI', sans-serif;
      color: #fff;
      background: transparent !important;
    }

    .container {
      background: rgba(0, 0, 0, 0.15);
      border-radius: 1rem;
      padding: 2rem;
      backdrop-filter: blur(10px);
      box-shadow: 0 0 40px rgba(0, 212, 255, 0.25);
      margin-top: 4vh;
    }

    h1 {
      color: #00d4ff;
      text-align: center;
      text-shadow: 0 0 20px rgba(0,212,255,0.6);
      margin-bottom: 2rem;
      font-weight: 600;
    }

    
    .card {
      border: none;
      border-radius: 1rem;
      background: rgba(255, 255, 255, 0.08);
      backdrop-filter: blur(12px);
      box-shadow: 0 0 25px rgba(0,212,255,0.25);
      transition: all 0.3s ease;
    }

    .card:hover {
      transform: translateY(-5px);
      box-shadow: 0 0 40px rgba(0,212,255,0.45);
    }

    .card-header {
      background: rgba(0,0,0,0.3);
      border-bottom: 1px solid rgba(255,255,255,0.1);
      font-weight: bold;
      text-align: center;
      letter-spacing: 0.5px;
    }

    .card-body {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      font-size: 1.6rem;
      color: #fff;
    }

    .pulse {
      animation: pulse 2.5s infinite;
    }
    @keyframes pulse {
      0% { text-shadow: 0 0 5px #00d4ff; }
      50% { text-shadow: 0 0 20px #00d4ff; }
      100% { text-shadow: 0 0 5px #00d4ff; }
    }

    footer {
      text-align: center;
      margin-top: 2rem;
      color: #aaa;
      font-size: 0.9rem;
      text-shadow: 0 0 8px rgba(0,212,255,0.3);
    }

    /* Floating logo */
    .logo {
      display: block;
      margin: 0 auto 1rem auto;
      width: 120px;
      filter: drop-shadow(0 0 10px rgba(0,212,255,0.6));
      opacity: 0.9;
      border-radius: 50%;
    }

    @media (max-width: 768px) {
      .card-body { font-size: 1.2rem; }
      h1 { font-size: 1.8rem; }
      .logo { width: 90px; }
    }
  </style>
</head>

<body>
  <!-- 🎥 Video background -->
  <video autoplay muted loop id="bgVideo">
    <source src="cozy.mp4" type="video/mp4">
    Your browser does not support the video tag.
  </video>

  <!-- 🌌 Dashboard Content -->
  <div class="container text-center">
    <img src="https://png.pngtree.com/png-vector/20241120/ourmid/pngtree-playful-cartoon-astronaut-floating-in-space-png-image_14498972.png"
         alt="Juice WRLD" class="logo">
    <h1>Elon Musk Tweets Analysis</h1>

    <div class="row g-4 mt-4">
      <div class="col-md-4">
        <div class="card h-100">
          <div class="card-header text-info">Tweets ≥ 1 Keyword</div>
          <div class="card-body">
            <h3 id="percent1" class="pulse">--%</h3>
          </div>
        </div>
      </div>
      <div class="col-md-4">
        <div class="card h-100">
          <div class="card-header text-success">Tweets with 2 Keywords</div>
          <div class="card-body">
            <h3 id="percent2" class="pulse">--%</h3>
          </div>
        </div>
      </div>
      <div class="col-md-4">
        <div class="card h-100">
          <div class="card-header text-warning">Tweet Length Stats</div>
          <div class="card-body flex-column">
            <p id="mean" class="pulse">--</p>
            <p id="std" class="pulse">--</p>
          </div>
        </div>
      </div>
    </div>

    <footer class="mt-5">
      <p> © 2025 Islam Yasin</p>
    </footer>
  </div>

  <script>
    const stats = $statsJson;

    document.addEventListener('DOMContentLoaded', () => {
      document.getElementById('percent1').textContent = stats.percent1.toFixed(2) + '%';
      document.getElementById('percent2').textContent = stats.percent2.toFixed(2) + '%';
      document.getElementById('mean').textContent = 'Mean: ' + stats.mean.toFixed(2) + ' words';
      document.getElementById('std').textContent  = 'Std: '  + stats.std.toFixed(2)  + ' words';
    });
  </script>
</body>
</html>
"""

    Files.write(Paths.get(s"$outputPath/report.html"), html.getBytes)
  }
}
