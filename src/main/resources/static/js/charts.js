// charts.js — Chart.js initialisation
// Dashboard charts are wired in Step 7.
// Boss-detail attempt timeline is populated below when the canvas is present.

document.addEventListener("DOMContentLoaded", () => {
  const canvas = document.getElementById("attemptsChart");
  if (!canvas) return;

  // Attempt data is injected by the template in Step 7.
  // Placeholder: renders an empty chart so the canvas isn't blank.
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
});
