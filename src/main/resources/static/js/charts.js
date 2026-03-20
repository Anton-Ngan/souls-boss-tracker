// charts.js — Chart.js initialisation for dashboard and boss-detail pages

document.addEventListener("DOMContentLoaded", () => {
  // ── Dashboard charts ────────────────────────────────────────────────────
  if (typeof dashboardData !== "undefined") {
    initTopBossesChart();
    initGameCompletionChart();
  }

  // ── Boss-detail attempt timeline ────────────────────────────────────────
  const attemptsCanvas = document.getElementById("attemptsChart");
  if (attemptsCanvas) {
    initAttemptsChart(attemptsCanvas);
  }
});

function initTopBossesChart() {
  const canvas = document.getElementById("topBossesChart");
  if (!canvas) return;

  new Chart(canvas, {
    type: "bar",
    data: {
      labels: dashboardData.topBossLabels,
      datasets: [
        {
          label: "Deaths",
          data: dashboardData.topBossDeaths,
          backgroundColor: "rgba(220,53,69,0.7)",
          borderColor: "#dc3545",
          borderWidth: 1,
          borderRadius: 4,
        },
      ],
    },
    options: {
      indexAxis: "y",
      responsive: true,
      plugins: { legend: { display: false } },
      scales: {
        x: {
          ticks: { color: "#aaa" },
          grid: { color: "#333" },
          beginAtZero: true,
        },
        y: { ticks: { color: "#ddd" }, grid: { color: "#333" } },
      },
    },
  });
}

function initGameCompletionChart() {
  const canvas = document.getElementById("gameCompletionChart");
  if (!canvas) return;

  const percentages = dashboardData.gameTotal.map((total, i) =>
    total > 0 ? Math.round((dashboardData.gameCleared[i] / total) * 100) : 0
  );

  new Chart(canvas, {
    type: "doughnut",
    data: {
      labels: dashboardData.gameLabels,
      datasets: [
        {
          data: percentages,
          backgroundColor: [
            "rgba(200,169,126,0.8)",
            "rgba(99,102,241,0.8)",
            "rgba(220,53,69,0.8)",
            "rgba(34,197,94,0.8)",
          ],
          borderColor: "#1a1a1a",
          borderWidth: 3,
        },
      ],
    },
    options: {
      responsive: true,
      plugins: {
        legend: { position: "bottom", labels: { color: "#ccc", padding: 12 } },
        tooltip: {
          callbacks: {
            label: (ctx) => ` ${ctx.label}: ${ctx.raw}% cleared`,
          },
        },
      },
    },
  });
}

function initAttemptsChart(canvas) {
  new Chart(canvas, {
    type: "line",
    data: {
      labels: [],
      datasets: [
        {
          label: "Deaths",
          data: [],
          borderColor: "#dc3545",
          backgroundColor: "rgba(220,53,69,0.15)",
          tension: 0.3,
          fill: true,
        },
      ],
    },
    options: {
      responsive: true,
      plugins: { legend: { display: false } },
      scales: {
        x: { ticks: { color: "#aaa" }, grid: { color: "#333" } },
        y: {
          ticks: { color: "#aaa" },
          grid: { color: "#333" },
          beginAtZero: true,
        },
      },
    },
  });
}
