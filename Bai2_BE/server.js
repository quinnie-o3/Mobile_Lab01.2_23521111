const express = require("express");
const cors = require("cors");

const app = express();
const PORT = 3000;

app.use(cors());
app.use(express.json());

app.post("/analyze", (req, res) => {
  const text = (req.body.text || "").toLowerCase().trim();

  if (!text) {
    return res.status(400).json({
      error: "Text is required"
    });
  }

  let label = "NEUTRAL";
  let explanation = "No sentiment keyword detected.";

  const negativeWords = ["no", "hate", "bad", "sad", "angry", "terrible"];
  const positiveWords = ["yes", "love", "good", "happy", "great", "excellent"];

  const hasNegative = negativeWords.some(word => text.includes(word));
  const hasPositive = positiveWords.some(word => text.includes(word));

  if (hasNegative) {
    label = "NEGATIVE";
    explanation = "Detected negative keyword.";
  } else if (hasPositive) {
    label = "POSITIVE";
    explanation = "Detected positive keyword.";
  }

  return res.json({
    label,
    explanation
  });
});

app.listen(PORT, () => {
  console.log(`Server running at http://localhost:${PORT}`);
});